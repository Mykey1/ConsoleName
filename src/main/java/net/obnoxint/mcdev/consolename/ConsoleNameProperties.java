package net.obnoxint.mcdev.consolename;

import java.util.HashMap;

import net.obnoxint.mcdev.feature.FeatureProperties;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ConsoleNameProperties extends FeatureProperties {

    public static final String PROPERTY_CHATFORMATSYMBOL_DEFAULT = "&";
    public static final String PROPERTY_PREFIX_DEFAULT = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[Console]" + ChatColor.RESET.toString() + ":";

    private static final String CHAT_FORMAT_SYMBOL = "§";
    private static final String PROPERTY_CHATFORMATSYMBOL_NAME = "chatFormatSymbol";
    private static final String PROPERTY_OVERRIDESAYCOMMAND_NAME = "overrideSayCommand";
    private static final String PROPERTY_PERPLAYER_NAME_PREFIX = "player_";
    private static final String PROPERTY_PREFIX_NAME = "prefix";
    private String chatFormatSymbol = PROPERTY_CHATFORMATSYMBOL_DEFAULT;

    private boolean overrideSayCommand = false;
    private String prefix = PROPERTY_PREFIX_DEFAULT;
    private HashMap<String, String> prefixes = new HashMap<String, String>();

    ConsoleNameProperties(ConsoleName feature) {
        super(feature);
    }

    public String getChatFormatSymbol() {
        return chatFormatSymbol;
    }

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
            r = getPrefix(player.getName());
        }
        return (r == null) ? prefix : r;
    }

    /**
     * @return true if the 'say' command is being overriden by the 'bc' command.
     */
    public boolean isOverrideSayCommand() {
        return overrideSayCommand;
    }

    public void setChatFormatSymbol(String chatFormatSymbol) {
        if (chatFormatSymbol == null) {
            this.chatFormatSymbol = PROPERTY_CHATFORMATSYMBOL_DEFAULT;
            setDirty();
        } else {
            chatFormatSymbol = chatFormatSymbol.trim();
            if (this.chatFormatSymbol != chatFormatSymbol) {
                this.chatFormatSymbol = chatFormatSymbol;
                setDirty();
            }
        }
    }

    /**
     * Enables or disables the plugins function to override the 'say' command.
     * 
     * @param overrideSayCommand true if the 'say' command should be overridden.
     */
    public void setOverrideSayCommand(boolean overrideSayCommand) {
        if (this.overrideSayCommand != overrideSayCommand) {
            this.overrideSayCommand = overrideSayCommand;
            setDirty();
        }
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
            this.prefix = replaceChatFormatSymbol(prefix);
            setDirty();
        }
    }

    @Override
    protected void onFileCreated() {
        getProperties().setProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, String.valueOf(overrideSayCommand));
        getProperties().setProperty(PROPERTY_CHATFORMATSYMBOL_NAME, chatFormatSymbol);
        getProperties().setProperty(PROPERTY_PREFIX_NAME, prefix);
    }

    @Override
    protected void onLoaded() {
        overrideSayCommand = Boolean.valueOf(getProperties().getProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, PROPERTY_DEFAULT_BOOLEAN_FALSE));
        chatFormatSymbol = getProperties().getProperty(PROPERTY_CHATFORMATSYMBOL_NAME, PROPERTY_CHATFORMATSYMBOL_DEFAULT);
        prefix = getProperties().getProperty(PROPERTY_PREFIX_NAME, PROPERTY_PREFIX_DEFAULT);

        // load per-player prefixes
        for (String prop : getProperties().stringPropertyNames()) {
            if (prop.startsWith(PROPERTY_PERPLAYER_NAME_PREFIX)) {
                prop = prop.substring(PROPERTY_PERPLAYER_NAME_PREFIX.length());
                if (!prop.isEmpty()) {
                    prefixes.put(prop, getProperties().getProperty(PROPERTY_PERPLAYER_NAME_PREFIX + prop));
                }
            }
        }
    }

    @Override
    protected void onStore() {
        getProperties().setProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, String.valueOf(overrideSayCommand));
        getProperties().setProperty(PROPERTY_CHATFORMATSYMBOL_NAME, chatFormatSymbol);
        getProperties().setProperty(PROPERTY_PREFIX_NAME, prefix);

        // store per-player prefixes
        for (String prop : prefixes.keySet()) {
            getProperties().setProperty(PROPERTY_PERPLAYER_NAME_PREFIX + prop, prefixes.get(prop));
        }
    }

    String getPrefix(String playerName) {
        return prefixes.get(playerName);
    }

    String replaceChatFormatSymbol(String string) {
        return string.replaceAll(getChatFormatSymbol(), CHAT_FORMAT_SYMBOL);
    }

    void setPrefix(String playerName, String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            prefixes.remove(playerName);
        } else {
            prefixes.put(playerName, replaceChatFormatSymbol(prefix));
        }
        setDirty();
    }

}
