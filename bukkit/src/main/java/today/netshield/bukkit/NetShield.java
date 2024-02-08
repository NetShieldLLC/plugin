package today.netshield.bukkit;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import today.netshield.bukkit.listener.PlayerListener;

public final class NetShield extends JavaPlugin {
    @Getter
    private static NetShield instance;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
