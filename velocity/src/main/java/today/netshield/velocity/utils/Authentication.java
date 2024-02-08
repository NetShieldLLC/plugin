package today.netshield.velocity.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import lombok.SneakyThrows;
import okhttp3.*;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import today.netshield.velocity.NetShield;
import today.netshield.velocity.config.ConfigManager;

import java.io.IOException;
import java.util.List;

public class Authentication {
    private final ConfigManager configManager = NetShield.getInstance().getConfigManager();

    public void handle(Player player) {
        OkHttpClient client = new OkHttpClient();

        String jsonString = getString(player);

        RequestBody requestBody = RequestBody.create(jsonString, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://netshield.bombardeen.me/api/checkuser")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                String code = jsonResponse.contains("\"code\"") ?
                        jsonResponse.split("\"code\"")[1].split(":")[1].split(",")[0].replaceAll("\"", "").trim() : null;

                if (code == null || !code.equalsIgnoreCase("VALID_PLAYER")) {
                    kickPlayer(player);
                }
            } else {
                NetShield.getInstance().log("Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException ignored) {}
    }

    private String getString(Player player) {
        Gson gson = new Gson();
        CommentedConfigurationNode config = configManager.getConfig();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", config.node("KEY").getString());

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getUsername());
        playerData.addProperty("ip", player.getRemoteAddress().getAddress().getHostAddress());

        jsonObject.add("playerData", playerData);

        return gson.toJson(jsonObject);
    }

    @SneakyThrows
    private void kickPlayer(Player player) throws SerializationException {
        CommentedConfigurationNode config = configManager.getConfig();
        List<String> kickMessage = config.node("KICK_MESSAGE").getList(String.class);

        StringBuilder message = new StringBuilder();
        for (String line : kickMessage) {
            message.append(line).append("\n");
        }
        player.disconnect(CC.colorize(message.toString()));
    }
}
