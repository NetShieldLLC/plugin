package today.netshield.bungee;

import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import today.netshield.bungee.listeners.PlayerListener;
import today.netshield.bungee.utils.ConfigFiles;

import java.io.File;
import java.io.IOException;

public final class NetShield extends Plugin {
    @Getter
    private static NetShield instance;
    @Getter
    private Configuration config;

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

        getProxy().getPluginManager().registerListener(this, new PlayerListener());
    }
}