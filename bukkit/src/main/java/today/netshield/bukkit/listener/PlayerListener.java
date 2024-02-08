package today.netshield.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import today.netshield.bukkit.utils.Authentication;

public class PlayerListener implements Listener {
    private final Authentication authentication = new Authentication();

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        authentication.handle(event.getPlayer());
    }
}