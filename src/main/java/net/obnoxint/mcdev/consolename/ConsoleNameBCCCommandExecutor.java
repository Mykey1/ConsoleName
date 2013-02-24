package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCCCommandExecutor extends ConsoleNameCommandExecutor {

    private static String[] parseBCCCommandArgs(final String[] args) {
        String line = "";
        String[] r;
        for (final String s : args) {
            line += " " + s;
        }
        line = line.trim();
        if (line.startsWith(COMMAND_BROADCAST_CUSTOM_ARG_PRE)) {
            line = line.substring(COMMAND_BROADCAST_CUSTOM_ARG_PRE.length());
            r = line.split(COMMAND_BROADCAST_CUSTOM_ARG_MSG);
            if (r.length == 2) {
                return r;
            }
        }
        return null;
    }

    ConsoleNameBCCCommandExecutor(final ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length >= 2 && ((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_CUSTOM) : true)) {
            final String[] split = parseBCCCommandArgs(args);
            if (split != null) {
                getPlugin().sendBroadcastMessage(split[0], split[1], true);
                return true;
            }
        } else if (sender instanceof Player && !((Player) sender).hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_CUSTOM)) {
            sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SENDBROADCAST_CUSTOM.getName());
            return true;
        }
        return false;
    }

}
