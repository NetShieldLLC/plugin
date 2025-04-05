package today.netshield.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.slf4j.Logger;

import today.netshield.velocity.commands.impl.NetShieldCommand;
import today.netshield.velocity.config.ConfigManager;
import today.netshield.velocity.listeners.ConnectionListener;

import java.io.File;
import java.nio.file.Path;

@Plugin(
        id = "netshield",
        name = "NetShield",
        authors = "jsexp",
        version = "2.0.0"
)
@Getter
public class NetShield {

    @Getter private static NetShield instance;

    private final ProxyServer server;
    private final Logger logger;
    private final ConfigManager configManager;
    private final Path dataDirectory;

    @Inject
    public NetShield(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        File configDir = dataDirectory.toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        this.configManager = new ConfigManager(configDir);
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        new NetShieldCommand();
        server.getEventManager().register(this, new ConnectionListener(this));
        log("NetShield has been enabled!");
    }

    public void log(String message) {
        logger.info(message);
    }
}