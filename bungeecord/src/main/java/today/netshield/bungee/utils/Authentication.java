package today.netshield.bungee.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import lombok.NonNull;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import today.netshield.bungee.NetShield;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public class Authentication {
    private final Configuration config;
    private final OkHttpClient client;
    private final Gson gson;
    private final NetShield plugin;

    public Authentication(NetShield plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.gson = new Gson();

        this.client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    public CompletableFuture<Void> handleAsync(@NonNull ProxiedPlayer player) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        try {
            String jsonPayload = createRequestPayload(player);
            if (jsonPayload == null) {
                plugin.getLogger().info("Failed to create authentication payload for " + player.getName());
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
                        plugin.getLogger().info("Authentication request timed out for player " + player.getName());
                    } else {
                        plugin.getLogger().info("Authentication request failed for player " + player.getName() + ": " + e.getMessage());
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
                            plugin.getLogger().info("API Error: " + response.code() + " - " + response.message());
                        }
                        future.complete(null);
                    }
                }
            });
        } catch (Exception e) {
            plugin.getLogger().info("Unexpected error during authentication: " + e.getMessage());
            future.complete(null);
        }

        return future;
    }

    private void processResponse(ProxiedPlayer player, String jsonResponse) {
        try {
            String code = extractCodeFromResponse(jsonResponse);
            if (code == null) {
                plugin.getLogger().info("Invalid response format from API for player " + player.getName());
                return;
            }

            if (shouldBlockPlayer(code)) {
                List<String> kickMessages = getKickMessage(code);
                plugin.getProxy().getScheduler().runAsync(plugin, () -> kickPlayer(player, kickMessages));
                plugin.getLogger().info("Player " + player.getName() + " was kicked with status code: " + code);
            } else {
                plugin.getLogger().info("Player " + player.getName() + " passed check with status code: " + code);
            }
        } catch (JsonParseException e) {
            plugin.getLogger().info("Failed to parse API response: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().info("Error processing authentication response: " + e.getMessage());
        }
    }

    private String createRequestPayload(ProxiedPlayer player) {
        try {
            String licenseKey = config.getString("KEY", null);
            if (licenseKey == null || licenseKey.trim().isEmpty()) {
                plugin.getLogger().info("License key not configured");
                return null;
            }

            JsonObject jsonObject = new JsonObject();
            JsonObject playerData = new JsonObject();

            jsonObject.addProperty("license", licenseKey);
            playerData.addProperty("name", player.getName());

            InetSocketAddress inetAddress = (InetSocketAddress) player.getSocketAddress();
            String ip = inetAddress.getAddress().getHostAddress();
            playerData.addProperty("ip", ip);

            jsonObject.add("playerData", playerData);
            return gson.toJson(jsonObject);
        } catch (Exception e) {
            plugin.getLogger().info("Failed to create authentication payload: " + e.getMessage());
            return null;
        }
    }

    private String extractCodeFromResponse(String jsonResponse) {
        try {
            JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
            return jsonObject != null && jsonObject.has("status") ?
                    jsonObject.get("status").getAsString() : null;
        } catch (JsonParseException e) {
            plugin.getLogger().info("Failed to parse API response: " + e.getMessage());
            return null;
        }
    }

    private boolean shouldBlockPlayer(String code) {
        if (code == null) return false;

        List<String> kickCodes = config.getStringList("KICK_ON_CODES");
        return kickCodes.contains(code);
    }

    private List<String> getKickMessage(String code) {
        if (code == null) {
            return getDefaultKickMessage();
        }

        String configKey = code.toLowerCase();
        List<String> kickMessages = config.getStringList("MESSAGES." + configKey);

        if (kickMessages.isEmpty()) {
            return getDefaultKickMessage();
        }

        return kickMessages;
    }

    private List<String> getDefaultKickMessage() {
        return config.getStringList("MESSAGES.default_kick");
    }

    private void kickPlayer(ProxiedPlayer player, List<String> kickMessages) {
        if (player == null || !player.isConnected()) return;

        try {
            StringBuilder messageBuilder = new StringBuilder();
            for (String line : kickMessages) {
                messageBuilder.append(line).append("\n");
            }

            player.disconnect(TextComponent.fromLegacyText(CC.colorize(messageBuilder.toString())));
        } catch (Exception e) {
            plugin.getLogger().info("Failed to kick player " + player.getName() + ": " + e.getMessage());
            player.disconnect("You are currently blocked by NetShield. More info at discord.netshield.today");
        }
    }
}
