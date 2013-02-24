package net.obnoxint.mcdev.consolename;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCSetCommandExecutor extends ConsoleNameCommandExecutor {

    ConsoleNameBCSetCommandExecutor(final ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final ConsoleNameConfiguration config = getPlugin().getConfiguration();
        String pre;
        if (args.length == 0) { // reset global prefix (no arguments)
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (player.hasPermission(ConsoleName.PERMISSION_SETPREFIX_GLOBAL)) {
                    config.resetPrefix(player);
                } else {
                    player.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_GLOBAL.getName());
                }
            } else {
                config.resetPrefix();
            }
            return true;
        } else {
            if (args[0].equals("~") && sender instanceof Player) { // set or reset own per-player prefix (first argument is ~)
                final Player player = (Player) sender;
                if (player.hasPermission(ConsoleName.PERMISSION_SETPREFIX_OWN)) {
                    if (args.length == 1) {
                        config.setPrefix(player, null);
                        player.sendMessage("Your personal broadcast prefix has been removed.");
                    } else {
                        pre = "";
                        for (int i = 1; i < args.length; i++) {
                            pre += args[i] + " ";
                        }
                        pre = pre.trim();
                        config.setPrefix(player, pre);
                        player.sendMessage("Your personal broadcast prefix has been set to: " + config.getPrefix(player));
                    }
                } else {
                    sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPREFIX_OWN.getName());
                }
                return true;
            } else if (args[0].startsWith("@")) { // set or reset per-player prefix of another player (first argument starts with @)
                if (((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPREFIX_OTHER) : true)) {
                    final String targetName = args[0].substring(1).trim();
                    final Player targetPlayer = Bukkit.getPlayer(targetName);
                    if (targetPlayer != null) {
                        if (args.length == 1) {
                            config.resetPrefix(targetPlayer);
                            sender.sendMessage("Broadcast prefix of " + targetPlayer.getName() + " has been reset.");
                        } else {
                            pre = "";
                            for (int i = 1; i < args.length; i++) {
                                pre += args[i] + " ";
                            }
                            pre = pre.trim();
                            config.setPrefix(targetPlayer, pre);
                            sender.sendMessage("Broadcast prefix of " + targetPlayer.getName() + " has been set to: " + config.getPrefix(targetPlayer));
                        }
                        return true;
                    } else {
                        sender.sendMessage("Can not set broadcast prefix for " + targetName + ": Player not found.");
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
                config.setPrefix(pre);
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
