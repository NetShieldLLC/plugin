package today.netshield.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import today.netshield.velocity.NetShield;
import today.netshield.velocity.utils.Authentication;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public class ConnectionListener {
    private final NetShield plugin;
    private final Authentication authentication;

    private static final long AUTH_TIMEOUT_MS = 3000;

    public ConnectionListener(NetShield plugin) {
        this.plugin = plugin;
        this.authentication = new Authentication(plugin);
    }

    @Subscribe(priority = Short.MAX_VALUE)
    public void onLogin(LoginEvent event) {
        Player player = event.getPlayer();
        plugin.log("Player " + player.getUsername() + " is logging in, starting authentication check");

        CompletableFuture<Void> authFuture = authentication.handleAsync(player);
        authFuture.orTimeout(AUTH_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .exceptionally(ex -> {
                    plugin.log("Authentication timed out or failed for player " + player.getUsername() + ": " + ex.getMessage());
                    return null;
                });

    }
}