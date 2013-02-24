package net.obnoxint.mcdev.consolename;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class ConsoleNameConfiguration {

    private static final String CONFIG_PATH_PREFIX = "prefix.";
    private static final String CONFIG_PATH_PREFIX_PLAYER = CONFIG_PATH_PREFIX + "player.";

    private static final String CONFIG_KEY_CHATFORMATSYMBOL = "chatFormatSymbol";
    private static final String CONFIG_KEY_OVERRIDESAYCOMMAND = "overrideSayCommand";
    private static final String CONFIG_KEY_ENABLESIGNBROADCAST = "enableSignBroadcast";
    private static final String CONFIG_KEY_SIGNBROADCASTTOOLID = "signBroadcastToolId";
    private static final String CONFIG_KEY_PREFIX_CONSOLE = CONFIG_PATH_PREFIX + "console";
    private static final String CONFIG_KEY_PREFIX_DEFAULT = CONFIG_PATH_PREFIX + "default";

    private static final String CONFIG_DEFAULT_CHATFORMATSYMBOL = "&";
    private static final boolean CONFIG_DEFAULT_OVERRIDESAYCOMMAND = true;
    private static final boolean CONFIG_DEFAULT_ENABLESIGNBROADCAST = true;
    private static final int CONFIG_DEFAULT_SIGNBROADCASTTOOLID = Material.SIGN.getId(); // 323
    private static final String CONFIG_DEFAULT_PREFIX_CONSOLE = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[CONSOLE]" + ChatColor.RESET.toString() + ":";
    private static final String CONFIG_DEFAULT_PREFIX_PLAYER = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[BROADCAST]" + ChatColor.RESET.toString() + ":";

    private final FileConfiguration config;

    ConsoleNameConfiguration(final ConsoleName plugin) {
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
    }

    public String getChatFormatSymbol() {
        return config.getString(CONFIG_KEY_CHATFORMATSYMBOL, CONFIG_DEFAULT_CHATFORMATSYMBOL);
    }

    public String getPrefix() {
        return config.getString(CONFIG_KEY_PREFIX_CONSOLE, CONFIG_DEFAULT_PREFIX_CONSOLE);
    }

    public String getPrefix(final Player player) {
        final String path = player == null ? CONFIG_KEY_PREFIX_DEFAULT : CONFIG_PATH_PREFIX_PLAYER + player.getName();
        return config.getString(path, config.getString(CONFIG_KEY_PREFIX_DEFAULT, CONFIG_DEFAULT_PREFIX_PLAYER));
    }

    public Material getSignBroadcastTool() {
        return Material.getMaterial(config.getInt(CONFIG_KEY_SIGNBROADCASTTOOLID, CONFIG_DEFAULT_SIGNBROADCASTTOOLID));
    }

    public boolean isEnableSignBroadcast() {
        return config.getBoolean(CONFIG_KEY_ENABLESIGNBROADCAST, CONFIG_DEFAULT_ENABLESIGNBROADCAST);
    }

    /**
     * @return true if the 'say' command is being overriden by the 'bc' command.
     */
    public boolean isOverrideSayCommand() {
        return config.getBoolean(CONFIG_KEY_OVERRIDESAYCOMMAND, CONFIG_DEFAULT_OVERRIDESAYCOMMAND);
    }

    public void resetPrefix() {
        config.set(CONFIG_KEY_PREFIX_CONSOLE, CONFIG_DEFAULT_PREFIX_CONSOLE);
    }

    public void resetPrefix(final Player player) {
        if (player == null) {
            config.set(CONFIG_KEY_PREFIX_DEFAULT, CONFIG_DEFAULT_PREFIX_PLAYER);
        } else {
            config.set(CONFIG_PATH_PREFIX_PLAYER + player.getName(), null);
        }
    }

    public void setChatFormatSymbol(final String chatFormatSymbol) {
        if (chatFormatSymbol == null) {
            config.set(CONFIG_KEY_CHATFORMATSYMBOL, CONFIG_DEFAULT_CHATFORMATSYMBOL);
        } else {
            config.set(CONFIG_KEY_CHATFORMATSYMBOL, chatFormatSymbol.trim());
        }
    }

    public void setEnableSignBroadcast(final boolean enableSignBroadcast) {
        config.set(CONFIG_KEY_ENABLESIGNBROADCAST, enableSignBroadcast);
    }

    /**
     * Enables or disables the plugins function to override the 'say' command.
     * 
     * @param overrideSayCommand true if the 'say' command should be overridden.
     */
    public void setOverrideSayCommand(final boolean overrideSayCommand) {
        config.set(CONFIG_KEY_OVERRIDESAYCOMMAND, overrideSayCommand);
    }

    public void setPrefix(final Player player, final String prefix) {
        final String path = player == null ? CONFIG_KEY_PREFIX_DEFAULT : CONFIG_PATH_PREFIX_PLAYER + player.getName();
        final String value = (player == null && prefix == null) ? "" : prefix;
        config.set(path, value);
    }

    public void setPrefix(final String prefix) {
        config.set(CONFIG_KEY_PREFIX_CONSOLE, prefix == null ? "" : prefix);
    }

    public void setSignBroadcastTool(final Material signBroadcastTool) {
        config.set(CONFIG_KEY_SIGNBROADCASTTOOLID, signBroadcastTool.getId());
    }

}
