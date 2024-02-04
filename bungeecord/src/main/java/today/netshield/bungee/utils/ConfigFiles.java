package today.netshield.bungee.utils;

import today.netshield.bungee.NetShield;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ConfigFiles {
    public void makeConfig() throws IOException {
        if (!NetShield.getInstance().getDataFolder().exists()) {
            NetShield.getInstance().getLogger().info("Created config folder: " + NetShield.getInstance().getDataFolder().mkdir());
        }

        File configFile = new File(NetShield.getInstance().getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            try (FileOutputStream outputStream = new FileOutputStream(configFile);
                 InputStream in = NetShield.class.getResourceAsStream("/config.yml")) {  // Add leading slash

                if (in == null) {
                    throw new IOException("Resource not found: config.yml");
                }

                byte[] buf = new byte[8192];
                int length;
                while ((length = in.read(buf)) != -1) {
                    outputStream.write(buf, 0, length);
                }
            }
        }
    }
}
