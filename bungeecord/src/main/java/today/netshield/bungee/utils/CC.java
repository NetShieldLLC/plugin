package today.netshield.bungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

public class CC {
    public static String t(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void log(String text) {
        ProxyServer.getInstance().getConsole().sendMessage(t(text));
    }
}
