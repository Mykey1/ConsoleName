package net.obnoxint.mcdev.consolename;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin {

    public static class ConsoleNameListener implements Listener {

        private final ConsoleName plugin;

        public ConsoleNameListener(ConsoleName plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
            String msg = event.getMessage();
            if (msg.startsWith("/say") && plugin.isOverrideSayCommand()) {
                msg = "/" + COMMAND_BROADCAST + " " + msg.substring(5);
                event.setMessage(msg);
            }
        }

        @EventHandler
        public void onServerCommand(ServerCommandEvent event) {
            String cmd = event.getCommand();
            if (cmd.startsWith("say") && plugin.isOverrideSayCommand()) {
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

    private static final boolean CONFIG_OVERRIDESAYCOMMAND_DEFAULT = false;
    private static final String CONFIG_OVERRIDESAYCOMMAND_PATH = "overrideSayCommand";
    private static final String CONFIG_PERPLAYERPREFIX_PATH = "perPlayer";
    private static final String CONFIG_PREFIX_DEFAULT = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[Console]" + ChatColor.RESET.toString() + ":";
    private static final String CONFIG_PREFIX_PATH = "prefix";

    /**
     * Sends a broadcast message.
     * 
     * @param prefix the prefix. If null or an empty String is given the default prefix (see: CONFIG_PREFIX_DEFAULT) will be used.
     * @param message the message. Must not be null or empty.
     */
    public static void sendBroadcastMessage(String prefix, String message) {
        if (message != null && !message.trim().isEmpty()) {
            String p = (prefix == null || prefix.trim().isEmpty()) ? CONFIG_PREFIX_DEFAULT : prefix.trim();
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

    private File configFile = null;

    private Metrics metrics;

    private boolean overrideSayCommand;

    private String prefix;

    private HashMap<String, String> prefixes = new HashMap<>();

    /**
     * @return The broadcast prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param player The player.
     * @return The broadcast prefix of the given player.
     */
    public String getPrefix(Player player) {
        String r = null;
        if (player != null) {
            r = prefixes.get(player.getName());
        }
        return (r == null) ? prefix : r;
    }

    /**
     * @return true if the 'say' command is being overriden by the 'bc' command.
     */
    public boolean isOverrideSayCommand() {
        return overrideSayCommand;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String pre;
        String msg;
        String cmd = command.getName().toLowerCase();
        if (cmd.equals(COMMAND_BROADCAST) && args.length > 0) {
            pre = (sender instanceof Player) ? getPrefix((Player) sender) : getPrefix();
            msg = "";
            for (int i = 0; i < args.length; i++) {
                msg = msg + " " + args[i];
            }
            sendBroadcastMessage(pre, msg);
            return true;
        } else if (cmd.equals(COMMAND_BROADCAST_CUSTOM) && args.length >= 2 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SENDBROADCAST_CUSTOM) : true)) {
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
                        setPrefix(player, null);
                        player.sendMessage("Your personal broadcast prefix has been removed.");
                    } else {
                        pre = "";
                        for (int i = 1; i < args.length; i++) {
                            pre += args[i] + " ";
                        }
                        pre = pre.trim();
                        setPrefix(player, pre);
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
                            setPrefix(targetPlayer, pre);
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
                    setPrefix(pre);
                    sender.sendMessage("Broadcast prefix set to: " + pre);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onDisable() {
        saveConfigFile();
    }

    @Override
    public void onEnable() {
        loadConfigFile();
        getServer().getPluginManager().registerEvents(new ConsoleNameListener(this), this);

        if (startMetrics()) {
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion()
                    + " is now using Hidendras Metrics. See http://forums.bukkit.org/threads/53449/ for more information.");
        }
    }

    /**
     * Enables or disables the plugins function to override the 'say' command.
     * 
     * @param overrideSayCommand true if the 'say' command should be overridden.
     */
    public void setOverrideSayCommand(boolean overrideSayCommand) {
        this.overrideSayCommand = overrideSayCommand;
    }

    /**
     * Sets or removes the broadcast prefix for a broadcast message of a particular player.
     * 
     * @param player the player.
     * @param prefix the broadcast prefix. If null or an empty String is given, the personalized prefix will be removed.
     */
    public void setPrefix(Player player, String prefix) {
        if (player != null) {
            setPrefix(player.getName(), prefix);
        }
    }

    /**
     * Sets the broadcast prefix.
     * 
     * @param prefix The broadcast prefix. May contain chat format codes.
     */
    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = prefix;
        }
    }

    private File getConfigFile() {
        if (configFile == null) {
            configFile = new File(getDataFolder(), "config.yml");
        }
        return configFile;
    }

    private void loadConfigFile() {
        setPrefix(getConfig().getString(CONFIG_PREFIX_PATH, CONFIG_PREFIX_DEFAULT));
        setOverrideSayCommand(getConfig().getBoolean(CONFIG_OVERRIDESAYCOMMAND_PATH, CONFIG_OVERRIDESAYCOMMAND_DEFAULT));
        ConfigurationSection sec = getConfig().getConfigurationSection(CONFIG_PERPLAYERPREFIX_PATH);
        if (sec != null) {
            prefixes.clear();
            for (String s : sec.getKeys(false)) {
                setPrefix(s, sec.getString(s));
            }
        }
    }

    private void resetDefaultPrefix(CommandSender sender, String targetPlayer) {
        String msg;
        if (targetPlayer == null) {
            setPrefix(CONFIG_PREFIX_DEFAULT);
            msg = "Broadcast prefix reset to: " + CONFIG_PREFIX_DEFAULT;
        } else {
            setPrefix(targetPlayer, null);
            msg = "Broadcast prefix of player " + targetPlayer + " removed.";
        }
        sender.sendMessage(msg);
    }

    private void saveConfigFile() {
        getConfig().set(CONFIG_PREFIX_PATH, prefix);
        getConfig().set(CONFIG_OVERRIDESAYCOMMAND_PATH, overrideSayCommand);
        getConfig().createSection(CONFIG_PERPLAYERPREFIX_PATH, prefixes);
        try {
            getConfig().save(getConfigFile());
        } catch (IOException e) {
            getLogger().severe(getDescription().getName() + " failed to save the configuration file.");
        }
    }

    private void setPrefix(String playerName, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            prefixes.remove(playerName);
        } else {
            prefixes.put(playerName, prefix);
        }
    }

    private boolean startMetrics() {
        try {
            metrics = new Metrics(this);
            return metrics.start();
        } catch (IOException e) {}
        return false;
    }

}