package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCCommandExecutor extends ConsoleNameCommandExecutor {

    ConsoleNameBCCommandExecutor(ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SENDBROADCAST) : true)) {
            String pre = (sender instanceof Player) ? getPlugin().getFeatureProperties().getPrefix((Player) sender) : getPlugin().getFeatureProperties().getPrefix();
            String msg = "";
            for (int i = 0; i < args.length; i++) {
                msg = msg + " " + args[i];
            }
            ConsoleName.sendBroadcastMessage(pre, msg, sender);
            return true;
        } else if (sender instanceof Player && !((Player) sender).hasPermission(PERMISSION_SENDBROADCAST)) {
            sender.sendMessage(NO_PERMISSION_MSG + PERMISSION_SENDBROADCAST.getName());
            return true;
        }
        return false;
    }

}
