package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCCommandExecutor extends ConsoleNameCommandExecutor {

    ConsoleNameBCCommandExecutor(final ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length > 0 && ((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SENDBROADCAST) : true)) {
            String msg = "";
            for (int i = 0; i < args.length; i++) {
                msg = msg + " " + args[i];
            }
            getPlugin().sendBroadcastMessage(sender, msg);
            return true;
        } else if (sender instanceof Player && !((Player) sender).hasPermission(ConsoleName.PERMISSION_SENDBROADCAST)) {
            sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SENDBROADCAST.getName());
            return true;
        }
        return false;
    }

}
