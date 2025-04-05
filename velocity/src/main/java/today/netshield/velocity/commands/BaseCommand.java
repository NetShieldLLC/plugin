package today.netshield.velocity.commands;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import today.netshield.velocity.NetShield;
import today.netshield.velocity.utils.CC;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public abstract class BaseCommand implements SimpleCommand {
    protected static final Component CONSOLE_SENDER = CC.colorize("&#ff0000Este comando sólo puede ser ejecutado desde el juego.");

    public BaseCommand(String name) {
        CommandManager manager = NetShield.getInstance().getServer().getCommandManager();
        manager.register(
                manager.metaBuilder(name)
                        .plugin(NetShield.getInstance())
                        .build(),
                this);
    }

    public BaseCommand(String name, String... aliases) {
        CommandManager manager = NetShield.getInstance().getServer().getCommandManager();
        manager.register(
                manager.metaBuilder(name)
                        .aliases(aliases)
                        .plugin(NetShield.getInstance())
                        .build(),
                this);
    }

    @Override
    public void execute(Invocation invocation) {
        invocation.source().sendMessage(CC.colorize("&#ff0000Este comando no ha sido creado correctamente."));
    }
}
