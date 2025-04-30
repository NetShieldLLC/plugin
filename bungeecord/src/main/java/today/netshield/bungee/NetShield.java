package today.netshield.bungee;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import today.netshield.bungee.commands.NetShieldCommand;
import today.netshield.bungee.listeners.ConnectionListener;
import today.netshield.bungee.utils.ConfigFiles;

import java.io.File;

@Getter @Setter
public final class NetShield extends Plugin {
    @Getter private static NetShield instance;

    private Configuration config;

    @SneakyThrows
    @Override
    public void onEnable() {
        instance = this;

        new ConfigFiles().makeConfig();
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new NetShieldCommand());
        getProxy().getPluginManager().registerListener(this, new ConnectionListener(this));

        getLogger().info("NetShield has been enabled!");
    }
}