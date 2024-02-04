package today.netshield.bungee.hook.impl;

import com.nickuc.login.api.event.bungee.auth.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import today.netshield.bungee.hook.Authentication;

public class nLogin implements Listener {
    private final Authentication authentication = new Authentication();
    @EventHandler
    public void onPlayerLogin(LoginEvent event) {
        authentication.handle(event.getPlayer());
    }
}
