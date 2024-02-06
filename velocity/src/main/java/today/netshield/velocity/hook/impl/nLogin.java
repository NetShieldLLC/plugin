package today.netshield.velocity.hook.impl;

import com.nickuc.login.api.event.velocity.auth.LoginEvent;
import com.nickuc.login.api.event.velocity.auth.RegisterEvent;
import com.velocitypowered.api.event.Subscribe;
import today.netshield.velocity.hook.Authentication;

public class nLogin {
    private final Authentication authentication = new Authentication();
    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        authentication.handle(event.getPlayer());
    }

    @Subscribe
    public void onPlayerRegister(RegisterEvent event) {
        authentication.handle(event.getPlayer());
    }
}
