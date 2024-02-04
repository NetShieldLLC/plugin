package today.netshield.bungee.hook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import okhttp3.*;
import today.netshield.bungee.NetShield;
import today.netshield.bungee.utils.CC;

import java.io.IOException;
import java.util.List;

public class Authentication {
    public void handle(ProxiedPlayer player) {
        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", NetShield.getInstance().getConfig().getString("KEY"));

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("ip", player.getAddress().getAddress().getHostAddress());

        jsonObject.add("playerData", playerData);

        String jsonString = gson.toJson(jsonObject);

        RequestBody requestBody = RequestBody.create(jsonString, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/api/checkuser")
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
                CC.log("Error: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void kickPlayer(ProxiedPlayer player) {
        List<String> kickMessage = NetShield.getInstance().getConfig().getStringList("KICK_MESSAGE");

        StringBuilder message = new StringBuilder();
        for (String line : kickMessage) {
            message.append(ChatColor.translateAlternateColorCodes('&', line)).append("\n");
        }
        player.disconnect(message.toString());
    }
}
