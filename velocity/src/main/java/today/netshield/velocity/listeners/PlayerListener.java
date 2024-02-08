package today.netshield.velocity.listeners;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import today.netshield.velocity.utils.Authentication;

public class PlayerListener {
    private final Authentication authentication = new Authentication();

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        authentication.handle(event.getPlayer());
    }
}
