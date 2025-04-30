package today.netshield.bungee.commands;

import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import today.netshield.bungee.NetShield;
import today.netshield.bungee.utils.CC;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public class NetShieldCommand extends Command {

    public NetShieldCommand() {
        super("netshield", "netshield.admin", "nsreload");
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        NetShield.getInstance().setConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(NetShield.getInstance().getDataFolder(), "config.yml")));
        sender.sendMessage(TextComponent.fromLegacyText(CC.colorize("&#20bd78La configuración de NetShield ha sido recargada con éxito.")));
    }
}
