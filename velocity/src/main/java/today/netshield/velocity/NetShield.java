package today.netshield.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import today.netshield.velocity.config.ConfigManager;
import today.netshield.velocity.listeners.PlayerListener;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id          = "netshield",
        name        = "NetShield-Velocity",
        version     =  "1.1",
        description = "Secure your minecraft cracked player's accounts."
)
@Getter
public class NetShield {
    @Getter
    private static NetShield instance;

    @Inject
    private static Logger logger;

    @DataDirectory
    @Inject
    private Path path;

    @Inject
    private ProxyServer proxyServer;

    private ConfigManager configManager;

    @Subscribe
    @SneakyThrows
    public void onProxyInitialization(ProxyInitializeEvent event) {
        instance = this;

        File dataFolder = path.toFile();
        configManager = new ConfigManager(dataFolder);

        configManager.getConfig().node("KEY", "KICK_MESSAGE");

        proxyServer.getEventManager().register(this, new PlayerListener());
    }

    public void log(String s) {
        logger.info(s);
    }
}
