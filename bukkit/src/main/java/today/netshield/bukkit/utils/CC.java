package today.netshield.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CC {
    public static String t(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(t(text));
    }
}
