package net.obnoxint.mcdev.consolename;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public final class ConsoleNameConfiguration {

    private static final String CONFIG_FILE = "plugins" + File.separator + "ConsoleName" + File.separator + "config.yml";

    private static final String CONFIG_PATH_PREFIX = "prefix";
    private static final String CONFIG_PATH_PREFIX_PLAYERS = CONFIG_PATH_PREFIX + ".players";

    private static final String CONFIG_KEY_CHATFORMATSYMBOL = "chatFormatSymbol";
    private static final String CONFIG_KEY_OVERRIDESAYCOMMAND = "overrideSayCommand";
    private static final String CONFIG_KEY_ENABLESIGNBROADCAST = "enableSignBroadcast";
    private static final String CONFIG_KEY_SIGNBROADCASTTOOL = "signBroadcastTool";
    private static final String CONFIG_KEY_PREFIX_CONSOLE = CONFIG_PATH_PREFIX + ".console";
    private static final String CONFIG_KEY_PREFIX_DEFAULT = CONFIG_PATH_PREFIX + ".default";

    private static final String CONFIG_DEFAULT_CHATFORMATSYMBOL = "&";
    private static final boolean CONFIG_DEFAULT_OVERRIDESAYCOMMAND = true;
    private static final boolean CONFIG_DEFAULT_ENABLESIGNBROADCAST = true;
    private static final Material CONFIG_DEFAULT_SIGNBROADCASTTOOL = Material.SIGN;
    private static final String CONFIG_DEFAULT_PREFIX_CONSOLE = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[CONSOLE]" + ChatColor.RESET.toString() + ":";
    private static final String CONFIG_DEFAULT_PREFIX_DEFAULT = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[BROADCAST]" + ChatColor.RESET.toString() + ":";

    private final ConsoleName plugin;
    private final FileConfiguration config;

    // options
    private String chatFormatSymbol = CONFIG_DEFAULT_CHATFORMATSYMBOL;
    private boolean overrideSayCommand = CONFIG_DEFAULT_OVERRIDESAYCOMMAND;

    private boolean enableSignBroadcast = CONFIG_DEFAULT_ENABLESIGNBROADCAST;
    private Material signBroadcastTool = CONFIG_DEFAULT_SIGNBROADCASTTOOL;

    private final Map<UUID, String> prefixPlayers = new HashMap<UUID, String>();
    private String prefixConsole = CONFIG_DEFAULT_PREFIX_CONSOLE;
    private String prefixDefault = CONFIG_DEFAULT_PREFIX_DEFAULT;

    ConsoleNameConfiguration(final ConsoleName plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        readConfig();
    }

    public String getChatFormatSymbol() {
        return chatFormatSymbol;
    }

    public String getPrefix() {
        return prefixConsole;
    }

    public String getPrefix(final Player player) {
        final UUID uid = player.getUniqueId();
        final String r = prefixPlayers.get(uid);
        return r == null ? prefixDefault : r;
    }

    public Material getSignBroadcastTool() {
        return signBroadcastTool;
    }

    public boolean isEnableSignBroadcast() {
        return enableSignBroadcast;
    }

    public boolean isOverrideSayCommand() {
        return overrideSayCommand;
    }

    public void load() {
        try {
            config.load(CONFIG_FILE);
        } catch (final FileNotFoundException e) {
            plugin.saveDefaultConfig();
            load();
        } catch (final Exception e) {
            plugin.getLogger().severe("An error occured while loading the configuration file.");
            e.printStackTrace();
        }
    }

    public void readConfig() {
        setChatFormatSymbol(config.getString(CONFIG_KEY_CHATFORMATSYMBOL, CONFIG_DEFAULT_CHATFORMATSYMBOL));
        setOverrideSayCommand(config.getBoolean(CONFIG_KEY_OVERRIDESAYCOMMAND, CONFIG_DEFAULT_OVERRIDESAYCOMMAND));
        setEnableSignBroadcast(config.getBoolean(CONFIG_KEY_ENABLESIGNBROADCAST, CONFIG_DEFAULT_ENABLESIGNBROADCAST));

        final String sbt = config.getString(CONFIG_KEY_SIGNBROADCASTTOOL, CONFIG_DEFAULT_SIGNBROADCASTTOOL.name());
        final Material mat = Material.getMaterial(sbt.toUpperCase());
        if (mat == null) {
            plugin.getLogger().warning("Unknown material: " + sbt);
        } else {
            setSignBroadcastTool(mat);
        }

        setPrefix(config.getString(CONFIG_KEY_PREFIX_CONSOLE, CONFIG_DEFAULT_PREFIX_CONSOLE));
        setPrefix((Player) null, config.getString(CONFIG_KEY_PREFIX_DEFAULT, CONFIG_DEFAULT_PREFIX_DEFAULT));

        final ConfigurationSection cs_pre = config.getConfigurationSection(CONFIG_PATH_PREFIX_PLAYERS);
        for (final String s : cs_pre.getKeys(false)) {
            try {
                setPrefix(UUID.fromString(s), cs_pre.getString(s));
            } catch (final IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID: " + s);
            }
        }
    }

    public void reload() {
        load();
        readConfig();
    }

    public void resetPrefix() {
        prefixConsole = CONFIG_DEFAULT_PREFIX_CONSOLE;
    }

    public void resetPrefix(final Player player) {
        if (player == null) {
            prefixDefault = CONFIG_DEFAULT_PREFIX_DEFAULT;
        } else {
            prefixPlayers.remove(player.getUniqueId());
        }
    }

    public void save() {
        try {
            config.save(CONFIG_FILE);
        } catch (final IOException e) {
            plugin.getLogger().warning("An error occured while saving the configuration file.");
            e.printStackTrace();
        }
    }

    public void setChatFormatSymbol(final String chatFormatSymbol) {
        if (chatFormatSymbol == null) {
            this.chatFormatSymbol = CONFIG_DEFAULT_CHATFORMATSYMBOL;
        } else {
            final String s = chatFormatSymbol.trim();
            Validate.notEmpty(s, "chatFormatSymbol (trimmed) must not be empty");
            this.chatFormatSymbol = s;
        }
    }

    public void setEnableSignBroadcast(final boolean enableSignBroadcast) {
        this.enableSignBroadcast = enableSignBroadcast;
    }

    public void setOverrideSayCommand(final boolean overrideSayCommand) {
        this.overrideSayCommand = overrideSayCommand;
    }

    public void setPrefix(final Player player, final String prefix) {
        final String s = prefix.trim();
        Validate.notEmpty(s, "prefix (trimmed) must not be empty");
        if (player == null) {
            prefixDefault = s;
        } else {
            setPrefix(player.getUniqueId(), s);
        }
    }

    public void setPrefix(final String prefix) {
        this.prefixConsole = prefix.trim();
    }

    public void setSignBroadcastTool(final Material signBroadcastTool) {
        this.signBroadcastTool = signBroadcastTool;
    }

    public void writeConfig() {

        config.set(CONFIG_KEY_CHATFORMATSYMBOL, chatFormatSymbol);
        config.set(CONFIG_KEY_OVERRIDESAYCOMMAND, overrideSayCommand);
        config.set(CONFIG_KEY_ENABLESIGNBROADCAST, enableSignBroadcast);
        config.set(CONFIG_KEY_SIGNBROADCASTTOOL, signBroadcastTool.name());
        config.set(CONFIG_KEY_PREFIX_CONSOLE, prefixConsole);
        config.set(CONFIG_KEY_PREFIX_DEFAULT, prefixDefault);

        config.set(CONFIG_PATH_PREFIX_PLAYERS, null);
        for (final UUID uid : prefixPlayers.keySet()) {
            config.set(CONFIG_PATH_PREFIX_PLAYERS + "." + uid.toString(), prefixPlayers.get(uid));
        }

    }

    private void setPrefix(final UUID uid, final String prefix) {
        if (uid != null && prefix != null && !prefix.isEmpty()) {
            prefixPlayers.put(uid, prefix);
        }
    }

}
