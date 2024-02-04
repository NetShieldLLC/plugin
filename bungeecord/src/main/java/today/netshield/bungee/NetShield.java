package today.netshield.bungee;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import okhttp3.*;
import today.netshield.bungee.hook.impl.nLogin;
import today.netshield.bungee.utils.CC;
import today.netshield.bungee.utils.ConfigFiles;

import java.io.File;
import java.io.IOException;

public final class NetShield extends Plugin {
    private static NetShield instance;
    private Configuration config;

    public static NetShield getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        try {
            new ConfigFiles().makeConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CC.log("&7&m------------------------");
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
                .url("https://netshield.bombardeen.me/api/checkkey")
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                JsonObject responseObject = gson.fromJson(jsonResponse, JsonObject.class);
                String status = responseObject.has("status") ? responseObject.get("status").getAsString() : null;

                if (!status.equals("VALID_KEY")) {
                    CC.log("&9Status: &c" + status);
                    CC.log("&c&oShutting down the plugin...");
                    getProxy().getPluginManager().unregisterCommands(this);
                    getProxy().getPluginManager().unregisterListeners(this);
                } else {
                    CC.log("&9Status: &a" + status);
                    CC.log("&eThanks for using &lNetShield&e!");
                    registerListeners();
                }
            } else {
                CC.log("Error: " + response.code() + " - " + response.message());
                getProxy().getPluginManager().unregisterCommands(this);
                getProxy().getPluginManager().unregisterListeners(this);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CC.log("&7&m------------------------");
    }

    private void registerListeners() {
        if (getProxy().getPluginManager().getPlugin("nLogin") != null) {
            getProxy().getPluginManager().registerListener(this, new nLogin());
        }
    }

    public Configuration getConfig() {
        return config;
    }
}