package today.netshield.velocity.commands.impl;

import today.netshield.velocity.NetShield;
import today.netshield.velocity.commands.BaseCommand;
import today.netshield.velocity.utils.CC;

/**
 * This code was made by jsexp, in case of any unauthorized
 * use, at least please leave credits.
 * Find more about me @ my <a href="https://github.com/hardcorefactions">GitHub</a> :D
 * © 2025 - jsexp
 */
public class NetShieldCommand extends BaseCommand {
    public NetShieldCommand() {
        super("netshield", "nsreload");
    }

    @Override
    public void execute(Invocation invocation) {
        NetShield.getInstance().getConfigManager().reload();
        invocation.source().sendMessage(CC.colorize("&#20bd78La configuración de NetShield ha sido recargada con éxito."));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("netshield.admin");
    }
}
