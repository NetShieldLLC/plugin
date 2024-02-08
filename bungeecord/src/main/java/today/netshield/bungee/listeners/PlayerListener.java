package today.netshield.bungee.listeners;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import today.netshield.bungee.utils.Authentication;

public class PlayerListener implements Listener {
    private final Authentication authentication = new Authentication();

    @EventHandler
    public void onPlayerLogin(PostLoginEvent event) {
        authentication.handle(event.getPlayer());
    }
}
