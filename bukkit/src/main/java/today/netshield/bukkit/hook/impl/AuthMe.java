package today.netshield.bukkit.hook.impl;

import fr.xephi.authme.events.LoginEvent;
import fr.xephi.authme.events.RegisterEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import today.netshield.bukkit.hook.Authentication;

public class AuthMe implements Listener {
    private final Authentication authentication = new Authentication();

    @EventHandler
    public void onPlayerLogin(LoginEvent event) {
        authentication.handle(event.getPlayer());
    }

    @EventHandler
    public void onPlayerRegister(RegisterEvent event) {
        authentication.handle(event.getPlayer());
    }
}
