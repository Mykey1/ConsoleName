package net.obnoxint.mcdev.consolename;

import java.io.IOException;

import net.obnoxint.mcdev.feature.Feature;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin implements Feature {

    public static class ConsoleNameListener implements Listener {

        private final ConsoleName plugin;

        public ConsoleNameListener(ConsoleName plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
            String msg = event.getMessage();
            if (msg.startsWith("/say") && plugin.getFeatureProperties().isOverrideSayCommand()) {
                msg = "/" + COMMAND_BROADCAST + " " + msg.substring(5);
                event.setMessage(msg);
            }
        }

        @EventHandler
        public void onServerCommand(ServerCommandEvent event) {
            String cmd = event.getCommand();
            if (cmd.startsWith("say") && plugin.getFeatureProperties().isOverrideSayCommand()) {
                cmd = COMMAND_BROADCAST + " " + cmd.substring(4);
                event.setCommand(cmd);
            }
        }

    } // End of ConsoleNameListener

    public static final String COMMAND_BROADCAST = "bc";
    public static final String COMMAND_BROADCAST_CUSTOM = "bcc";
    public static final String COMMAND_BROADCAST_CUSTOM_ARG_MSG = "--msg=";
    public static final String COMMAND_BROADCAST_CUSTOM_ARG_PRE = "--pre=";
    public static final String COMMAND_BROADCAST_SETPREFIX = "bcset";

    public static final Permission PERMISSION_SENDBROADCAST_CUSTOM = new Permission("consolename.sendbroadcast.custom", PermissionDefault.OP);
    public static final Permission PERMISSION_SETPREFIX_GLOBAL = new Permission("consolename.setprefix.global", PermissionDefault.OP);
    public static final Permission PERMISSION_SETPREFIX_OTHER = new Permission("consolename.setprefix.other", PermissionDefault.OP);
    public static final Permission PERMISSION_SETPREFIX_OWN = new Permission("consolename.setprefix.own", PermissionDefault.OP);

    /**
     * Sends a broadcast message.
     * 
     * @param prefix the prefix. If null or an empty String is given the default prefix (see: CONFIG_PREFIX_DEFAULT) will be used.
     * @param message the message. Must not be null or empty.
     */
    public static void sendBroadcastMessage(String prefix, String message) {
        if (message != null && !message.trim().isEmpty()) {
            String p = (prefix == null || prefix.trim().isEmpty()) ? ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT : prefix.trim();
            String m = message.trim();
            Bukkit.getServer().broadcastMessage(p + " " + m);
        }
    }

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

    private boolean active = false;

    private Metrics metrics;

    private ConsoleNameProperties properties = null;

    @Override
    public String getFeatureName() {
        return getDescription().getName();
    }

    @Override
    public Plugin getFeaturePlugin() {
        return this;
    }

    @Override
    public ConsoleNameProperties getFeatureProperties() {
        if (properties == null) {
            properties = new ConsoleNameProperties(this);
        }
        return properties;
    }

    @Override
    public boolean isFeatureActive() {
        return active;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (isFeatureActive()){
            String pre;
            String msg;
            String cmd = command.getName().toLowerCase();
            ConsoleNameProperties prop = getFeatureProperties();
            if (cmd.equals(COMMAND_BROADCAST) && args.length > 0) {
                pre = (sender instanceof Player) ? prop.getPrefix((Player) sender) : prop.getPrefix();
                msg = "";
                for (int i = 0; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                sendBroadcastMessage(pre, msg);
                return true;
            } else if (cmd.equals(COMMAND_BROADCAST_CUSTOM) && args.length >= 2
                    && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SENDBROADCAST_CUSTOM) : true)) {
                String[] split = parseBCCCommandArgs(args);
                if (split != null) {
                    sendBroadcastMessage(split[0], split[1]);
                    return true;
                }
            } else if (cmd.equals(COMMAND_BROADCAST_SETPREFIX)) {
                if (args.length == 0 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SETPREFIX_GLOBAL) : true)) {
                    resetDefaultPrefix(sender, null);
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
                                resetDefaultPrefix(sender, targetPlayer);
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
            }
        } else {
            getLogger().info("Tried command " + command.getName() + " but " + getFeatureName() + " is inactive.");
        }
        return false;
    }

    @Override
    public void onDisable() {
        setFeatureActive(false);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ConsoleNameListener(this), this);
        setFeatureActive(true);

        if (startMetrics()) {
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion()
                    + " is now using Hidendras Metrics. See http://forums.bukkit.org/threads/53449/ for more information.");
        }
    }

    @Override
    public void setFeatureActive(boolean active) {
        if (this.active != active) {
            if (active) {
                getFeatureProperties().loadProperties();
            } else {
                getFeatureProperties().storeProperties();
            }
            this.active = active;
        }
    }

    private void resetDefaultPrefix(CommandSender sender, String targetPlayer) {
        String msg;
        if (targetPlayer == null) {
            getFeatureProperties().setPrefix(ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT);
            msg = "Broadcast prefix reset to: " + ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT;
        } else {
            getFeatureProperties().setPrefix(targetPlayer, null);
            msg = "Broadcast prefix of player " + targetPlayer + " removed.";
        }
        sender.sendMessage(msg);
    }

    private boolean startMetrics() {
        try {
            metrics = new Metrics(this);
            return metrics.start();
        } catch (IOException e) {}
        return false;
    }

}