package today.netshield.bukkit;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;
import today.netshield.bukkit.listeners.PlayerListener;
import today.netshield.bukkit.utils.CC;

import java.io.IOException;

public final class NetShield extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        CC.log("&7&m" + StringUtils.repeat("-", 24));
        CC.log("&b&lNet&9&lShield");
        CC.log("&f");
        CC.log("&9Version: "+ getDescription().getVersion());
        CC.log("&7&oChecking key...");

        OkHttpClient client = new OkHttpClient();

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", getConfig().getString("KEY"));
        String jsonString = gson.toJson(jsonObject);

        RequestBody requestBody = RequestBody.create(jsonString, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("http://127.0.0.1:5000/api/checkkey")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                String status = jsonResponse.contains("\"status\"") ?
                        jsonResponse.split("\"status\"")[1].split(":")[1].split(",")[0].replaceAll("\"", "").trim() : null;

                if (!status.equals("VALID_KEY")) {
                    CC.log("&9Status: &c" + status);
                    CC.log("&c&oShutting down the plugin...");
                    getPluginLoader().disablePlugin(this);
                } else {
                    CC.log("&9Status: &a" + status);
                    CC.log("&eThanks for using &lNetShield&e!");
                }
            } else {
                CC.log("Error: " + response.code() + " - " + response.message());
                getPluginLoader().disablePlugin(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CC.log("&7&m" + StringUtils.repeat("-", 24));

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
