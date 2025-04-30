package today.netshield.bungee.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import today.netshield.bungee.NetShield;
import today.netshield.bungee.utils.Authentication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ConnectionListener implements Listener {
    private final NetShield plugin;
    private final Authentication authentication;

    private static final long AUTH_TIMEOUT_MS = 3000;

    public ConnectionListener(NetShield plugin) {
        this.plugin = plugin;
        this.authentication = new Authentication(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        plugin.getLogger().info("Player " + player.getName() + " is logging in, starting authentication check");

        CompletableFuture<Void> authFuture = authentication.handleAsync(player);
        authFuture.orTimeout(AUTH_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    plugin.getLogger().info("Authentication timed out or failed for player " + player.getName() + ": " + ex.getMessage());
                    return null;
                });
    }
}
