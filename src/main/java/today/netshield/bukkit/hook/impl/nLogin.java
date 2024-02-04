package today.netshield.bukkit.hook.impl;

import com.nickuc.login.api.event.bukkit.auth.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import today.netshield.bukkit.hook.Authentication;

public class nLogin implements Listener {
    private final Authentication authentication = new Authentication();

    @EventHandler
    public void onPlayerLogin(LoginEvent event) {
        authentication.handle(event.getPlayer());
    }
}
