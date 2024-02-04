package today.netshield.bukkit.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerLogin(LoginEvent event) throws IOException {
        Player player = event.getPlayer();

        URL url = new URL("http://127.0.0.1:5000/api/checkuser");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", "WRAUSAWHXSSPZK24");

        JsonObject playerData = new JsonObject();
        playerData.addProperty("name", player.getName());
        playerData.addProperty("ip", player.getAddress().getAddress().getHostAddress());

        jsonObject.add("playerData", playerData);

        String jsonString = gson.toJson(jsonObject);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }

        String jsonResponse = response.toString();
        String code = jsonResponse.contains("\"code\"") ? jsonResponse.split("\"code\"")[1].split(":")[1].split(",")[0].replaceAll("\"", "").trim() : null;
        assert code != null;
        if (!code.equalsIgnoreCase("VALID_PLAYER")) {
            String[] kickMessage = {
                    "&b&lNet&9&lShield &7&m-&f Blocked",
                    "&f",
                    "&cYour account is blocked from the &lNetShield&c network.",
                    "&cIf you think this is an error. Contact us at Discord.",
                    "&f",
                    "&9Discord &7» &fdiscord.gg/netshield"
            };

            StringBuilder message = new StringBuilder();
            for (String line : kickMessage) {
                message.append(ChatColor.translateAlternateColorCodes('&', line)).append("\n");
            }
            player.kickPlayer(message.toString());
        }

        con.disconnect();
    }
}
