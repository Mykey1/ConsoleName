package net.obnoxint.mcdev.consolename;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCSetCommandExecutor extends ConsoleNameCommandExecutor {

    ConsoleNameBCSetCommandExecutor(final ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        String pre;
        final ConsoleNameProperties prop = getPlugin().getFeatureProperties();
        if (args.length == 0) { // reset global prefix (no arguments)
            if ((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPREFIX_GLOBAL) : true) {
                getPlugin().resetDefaultPrefix(sender, null);
            } else {
                sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_GLOBAL.getName());
            }
            return true;
        } else {
            if (args[0].equals("~") && sender instanceof Player) { // set or reset own per-player prefix (first argument is ~)
                if (((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPREFIX_OWN)) {
                    final Player player = (Player) sender;
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
                        player.sendMessage("Your personal broadcast prefix has been set to: " + getPlugin().getFeatureProperties().getPrefix(player));
                    }
                } else {
                    sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_OWN.getName());
                }
                return true;
            } else if (args[0].startsWith("@")) { // set or reset per-player prefix of another player (first argument starts with @)
                if (((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPREFIX_OTHER) : true)) {
                    final String targetPlayer = args[0].substring(1).trim();
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
                            sender.sendMessage("Broadcast prefix of " + targetPlayer + " has been set to: " + getPlugin().getFeatureProperties().getPrefix(targetPlayer));
                        }
                        return true;
                    }
                } else {
                    sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_OTHER.getName());
                    return true;
                }
            } else if (((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPREFIX_GLOBAL) : true)) { // set global prefix (other cases)
                pre = "";
                for (int i = 0; i < args.length; i++) {
                    pre += args[i] + " ";
                }
                pre = pre.trim();
                prop.setPrefix(pre);
                sender.sendMessage("Broadcast prefix set to: " + pre);
                return true;
            } else {
                sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_GLOBAL.getName());
                return true;
            }
        }
        return false;
    }

}
