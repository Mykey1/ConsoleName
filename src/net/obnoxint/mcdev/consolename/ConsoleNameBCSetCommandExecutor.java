package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCSetCommandExecutor extends ConsoleNameCommandExecutor {

    ConsoleNameBCSetCommandExecutor(ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pre;
        ConsoleNameProperties prop = getPlugin().getFeatureProperties();
        if (args.length == 0 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SETPREFIX_GLOBAL) : true)) {
            getPlugin().resetDefaultPrefix(sender, null);
            return true;
        } else {
            if (args[0].equals("~") && sender instanceof Player && ((Player) sender).hasPermission(PERMISSION_SETPREFIX_OWN)) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    prop.setPrefix(player, null);
                    player.sendMessage("Your personal broadcast prefix has been removed.");
                } else {
                    pre = "";
                    for (int i = 1; i < args.length; i++) {
                        pre += args[i] + " ";
                    }
                    pre = pre.trim();
                    prop.setPrefix(player, pre);
                    player.sendMessage("Your personal broadcast prefix has been set to: " + pre);
                }
                return true;
            } else if (args[0].startsWith("@") && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SETPREFIX_OTHER) : true)) {
                String targetPlayer = args[0].substring(1).trim();
                if (!targetPlayer.isEmpty()) {
                    if (args.length == 1) {
                        getPlugin().resetDefaultPrefix(sender, targetPlayer);
                    } else {
                        pre = "";
                        for (int i = 1; i < args.length; i++) {
                            pre += args[i] + " ";
                        }
                        pre = pre.trim();
                        prop.setPrefix(targetPlayer, pre);
                        sender.sendMessage("Broadcast prefix of " + targetPlayer + " has been set to: " + pre);
                    }
                    return true;
                }
            } else if (((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SETPREFIX_GLOBAL) : true)) {
                pre = "";
                for (int i = 0; i < args.length; i++) {
                    pre += args[i] + " ";
                }
                pre = pre.trim();
                prop.setPrefix(pre);
                sender.sendMessage("Broadcast prefix set to: " + pre);
                return true;
            }
        }
        return false;
    }

}
