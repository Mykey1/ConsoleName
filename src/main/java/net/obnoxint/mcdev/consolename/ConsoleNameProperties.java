package net.obnoxint.mcdev.consolename;

import net.obnoxint.mcdev.mosaic.MosaicBase;
import net.obnoxint.mcdev.mosaic.MosaicFeatureProperties;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public final class ConsoleNameProperties extends MosaicFeatureProperties {

    public static final String PROPERTY_CHATFORMATSYMBOL_DEFAULT = "&";
    public static final String PROPERTY_PREFIX_DEFAULT = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[Console]" + ChatColor.RESET.toString() + ":";
    public static final int PROPERTY_SIGNBROADCASTTOOLID_DEFAULT = Material.BOOK.getId(); // 340

    private static final String CHAT_FORMAT_SYMBOL = "§";
    private static final String PROPERTY_CHATFORMATSYMBOL_NAME = "chatFormatSymbol";
    private static final String PROPERTY_OVERRIDESAYCOMMAND_NAME = "overrideSayCommand";
    private static final String PROPERTY_PREFIX_NAME = "prefix";
    private static final String PROPERTY_ENABLESIGNBROADCAST_NAME = "enableSignBroadcast";
    private static final String PROPERTY_SIGNBROADCASTTOOLID_NAME = "signBroadcastToolId";

    private static final String PROPERTY_MANAGER_PERPLAYER_PREFIX = "playerBroadcastPrefix";

    private String chatFormatSymbol = PROPERTY_CHATFORMATSYMBOL_DEFAULT;
    private boolean overrideSayCommand = false;
    private String prefix = PROPERTY_PREFIX_DEFAULT;
    private boolean enableSignBroadcast = false;
    private int signBroadcastToolId = PROPERTY_SIGNBROADCASTTOOLID_DEFAULT;

    ConsoleNameProperties(final ConsoleNameFeature feature) {
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
    public String getPrefix(final Player player) {
        String r = null;
        if (player != null) {
            r = MosaicBase.getPropertyManager().getPropertyContainer(player).getProperty(getFeature(), PROPERTY_MANAGER_PERPLAYER_PREFIX);
        }
        return (r == null) ? prefix : r;
    }

    public Material getSignBroadcastTool() {
        return Material.getMaterial(signBroadcastToolId);
    }

    public boolean isEnableSignBroadcast() {
        return enableSignBroadcast;
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
            if (!this.chatFormatSymbol.equals(chatFormatSymbol)) {
                this.chatFormatSymbol = chatFormatSymbol;
                setDirty();
            }
        }
    }

    public void setEnableSignBroadcast(final boolean enableSignBroadcast) {
        if (this.enableSignBroadcast != enableSignBroadcast) {
            this.enableSignBroadcast = enableSignBroadcast;
            setDirty();
        }
    }

    /**
     * Enables or disables the plugins function to override the 'say' command.
     * 
     * @param overrideSayCommand true if the 'say' command should be overridden.
     */
    public void setOverrideSayCommand(final boolean overrideSayCommand) {
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
    public void setPrefix(final Player player, final String prefix) {
        if (player != null) {
            if (prefix == null || prefix.isEmpty()) {
                MosaicBase.getPropertyManager().getPropertyContainer(player).removeProperty(getFeature(), PROPERTY_MANAGER_PERPLAYER_PREFIX);
            } else {
                MosaicBase.getPropertyManager().getPropertyContainer(player).setProperty(getFeature(), PROPERTY_MANAGER_PERPLAYER_PREFIX, prefix);
            }
        }
    }

    /**
     * Sets the broadcast prefix.
     * 
     * @param prefix The broadcast prefix. May contain chat format codes.
     */
    public void setPrefix(final String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            this.prefix = replaceChatFormatSymbol(prefix);
            setDirty();
        }
    }

    public void setSignBroadcastTool(final Material signBroadcastTool) {
        final int id = signBroadcastTool.getId();
        if (this.signBroadcastToolId != id) {
            this.signBroadcastToolId = id;
            setDirty();
        }
    }

    @Override
    protected void onFileCreated() {
        getProperties().setProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, String.valueOf(overrideSayCommand));
        getProperties().setProperty(PROPERTY_CHATFORMATSYMBOL_NAME, chatFormatSymbol);
        getProperties().setProperty(PROPERTY_PREFIX_NAME, prefix);
        store();
    }

    @Override
    protected void onLoaded() {
        overrideSayCommand = Boolean.valueOf(getProperties().getProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, PROPERTY_DEFAULT_BOOLEAN_FALSE));
        chatFormatSymbol = getProperties().getProperty(PROPERTY_CHATFORMATSYMBOL_NAME, PROPERTY_CHATFORMATSYMBOL_DEFAULT);
        prefix = getProperties().getProperty(PROPERTY_PREFIX_NAME, PROPERTY_PREFIX_DEFAULT);
        enableSignBroadcast = Boolean.valueOf(getProperties().getProperty(PROPERTY_ENABLESIGNBROADCAST_NAME, PROPERTY_DEFAULT_BOOLEAN_FALSE));

        // load signBroadcastId
        try {
            final int id = Integer.valueOf(getProperties().getProperty(PROPERTY_SIGNBROADCASTTOOLID_NAME));
            if (Material.getMaterial(id) != null) {
                signBroadcastToolId = id;
            }
        } catch (final NumberFormatException e) {}
    }

    @Override
    protected void onStore() {
        getProperties().setProperty(PROPERTY_OVERRIDESAYCOMMAND_NAME, String.valueOf(overrideSayCommand));
        getProperties().setProperty(PROPERTY_CHATFORMATSYMBOL_NAME, chatFormatSymbol);
        getProperties().setProperty(PROPERTY_PREFIX_NAME, prefix);
        getProperties().setProperty(PROPERTY_ENABLESIGNBROADCAST_NAME, String.valueOf(enableSignBroadcast));
        getProperties().setProperty(PROPERTY_SIGNBROADCASTTOOLID_NAME, String.valueOf(signBroadcastToolId));
    }

    String replaceChatFormatSymbol(final String string) {
        return string.replaceAll(getChatFormatSymbol(), CHAT_FORMAT_SYMBOL);
    }

}
