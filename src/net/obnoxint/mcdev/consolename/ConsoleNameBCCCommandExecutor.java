package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCCCommandExecutor extends ConsoleNameCommandExecutor {

    private static String[] parseBCCCommandArgs(String[] args) {
        String line = "";
        String[] r;
        for (String s : args) {
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

    protected ConsoleNameBCCCommandExecutor(ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 2 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SENDBROADCAST_CUSTOM) : true)) {
            String[] split = parseBCCCommandArgs(args);
            if (split != null) {
                split[0] = getPlugin().getFeatureProperties().replaceChatFormatSymbol(split[0]);
                split[1] = getPlugin().getFeatureProperties().replaceChatFormatSymbol(split[1]);
                ConsoleName.sendBroadcastMessage(split[0], split[1]);
                return true;
            }
        }
        return false;
    }

}
