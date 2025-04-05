package today.netshield.velocity.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.velocitypowered.api.proxy.Player;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import today.netshield.velocity.NetShield;
import today.netshield.velocity.config.ConfigManager;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public class Authentication {
    private final ConfigManager configManager;
    private final OkHttpClient client;
    private final Gson gson;
    private final NetShield plugin;

    public Authentication(NetShield plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.gson = new Gson();

        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public CompletableFuture<Void> handleAsync(@NonNull Player player) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            String jsonPayload = createRequestPayload(player);
            if (jsonPayload == null) {
                plugin.log("Failed to create authentication payload for " + player.getUsername());
                future.complete(null);
                return future;
            }

            RequestBody requestBody = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url("https://netshield.bombardeen.me:2096/api/checkuser")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    if (e instanceof SocketTimeoutException) {
                        plugin.log("Authentication request timed out for player " + player.getUsername());
                    } else {
                        plugin.log("Authentication request failed for player " + player.getUsername() + ": " + e.getMessage());
                    }
                    future.complete(null);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try (response) {
                        if (response.body() != null) {
                            String jsonResponse = response.body().string();
                            processResponse(player, jsonResponse);
                        } else {
                            plugin.log("API Error: " + response.code() + " - " + response.message());
                        }
                        future.complete(null);
                    }
                }
            });
        } catch (Exception e) {
            plugin.log("Unexpected error during authentication: " + e.getMessage());
            future.complete(null);
        }

        return future;
    }

    private void processResponse(Player player, String jsonResponse) {
        try {
            String code = extractCodeFromResponse(jsonResponse);
            if (code == null) {
                plugin.log("Invalid response format from API for player " + player.getUsername());
                return;
            }

            CommentedConfigurationNode config = configManager.getConfig();
            if (shouldBlockPlayer(config, code)) {
                List<String> kickMessages = getKickMessage(code, config);
                plugin.getServer().getScheduler().buildTask(plugin, () -> kickPlayer(player, kickMessages)).schedule();
                plugin.log("Player " + player.getUsername() + " was kicked with status code: " + code);
            } else {
                plugin.log("Player " + player.getUsername() + " passed check with status code: " + code);
            }
        } catch (JsonParseException e) {
            plugin.log("Failed to parse API response: " + e.getMessage());
        } catch (SerializationException e) {
            plugin.log("Failed to retrieve configuration: " + e.getMessage());
        } catch (Exception e) {
            plugin.log("Error processing authentication response: " + e.getMessage());
        }
    }

    private String createRequestPayload(Player player) {
        try {
            CommentedConfigurationNode config = configManager.getConfig();
            String licenseKey = config.node("KEY").getString();
            if (licenseKey == null || licenseKey.trim().isEmpty()) {
                plugin.log("License key not configured");
                return null;
            }

            JsonObject jsonObject = new JsonObject();
            JsonObject playerData = new JsonObject();

            jsonObject.addProperty("license", licenseKey);
            playerData.addProperty("name", player.getUsername());

            String ipAddress = Optional.ofNullable(player.getRemoteAddress())
                    .map(addr -> addr.getAddress().getHostAddress())
                    .orElse("unknown");
            playerData.addProperty("ip", ipAddress);

            jsonObject.add("playerData", playerData);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            plugin.log("Failed to create authentication payload: " + e.getMessage());
            return null;
        }
    }

    private String extractCodeFromResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            return jsonObject != null && jsonObject.has("status") ?
                    jsonObject.get("status").getAsString() : null;
        } catch (JsonParseException e) {
            plugin.log("Failed to parse API response: " + e.getMessage());
            return null;
        }
    }

    private boolean shouldBlockPlayer(CommentedConfigurationNode config, String code)
            throws SerializationException {
        if (code == null) return false;

        List<String> kickCodes = config.node("KICK_ON_CODES").getList(String.class, Collections.emptyList());
        return kickCodes.contains(code);
    }

    private List<String> getKickMessage(String code, CommentedConfigurationNode config)
            throws SerializationException {
        if (code == null) {
            return getDefaultKickMessage(config);
        }

        String configKey = code.toLowerCase();
        CommentedConfigurationNode codeMessageNode = config.node("MESSAGES", configKey);

        if (!codeMessageNode.empty()) {
            return codeMessageNode.getList(String.class, getDefaultKickMessage(config));
        }

        return getDefaultKickMessage(config);
    }

    private List<String> getDefaultKickMessage(CommentedConfigurationNode config)
            throws SerializationException {
        return config.node("MESSAGES", "default_kick")
                .getList(String.class, Collections.singletonList(
                        "You are currently blocked by NetShield. More info at discord.netshield.today"));
    }

    private void kickPlayer(Player player, List<String> kickMessages) {
        if (player == null || !player.isActive()) return;

        try {
            StringBuilder messageBuilder = new StringBuilder();
            for (String line : kickMessages) {
                messageBuilder.append(line).append("\n");
            }

            Component kickComponent = LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(messageBuilder.toString());
            player.disconnect(kickComponent);
        } catch (Exception e) {
            plugin.log("Failed to kick player " + player.getUsername() + ": " + e.getMessage());
            player.disconnect(Component.text("You are currently blocked by NetShield. More info at discord.netshield.today"));
        }
    }
}