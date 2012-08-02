package net.obnoxint.mcdev.consolename;

import java.io.File;

import net.obnoxint.mcdev.feature.Feature;
import net.obnoxint.mcdev.omclib.OmcLibPlugin;
import net.obnoxint.mcdev.omclib.metrics.MetricsGraph;
import net.obnoxint.mcdev.omclib.metrics.MetricsInstance;
import net.obnoxint.mcdev.omclib.metrics.MetricsPlotter;
import net.obnoxint.mcdev.omclib.metrics.OmcLibMetricsFeature;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin implements Feature {

    private static ConsoleName instance = null;

    static final String NO_PERMISSION_MSG = "You don't have the permission required in order to use this command: §o";

    static final Permission PERMISSION_SENDBROADCAST = new Permission("consolename.sendbroadcast", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_CUSTOM = new Permission("consolename.sendbroadcast.custom", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_SIGN = new Permission("consolename.sendbroadcast.sign", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_GLOBAL = new Permission("consolename.setprefix.global", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OTHER = new Permission("consolename.setprefix.other", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OWN = new Permission("consolename.setprefix.own", PermissionDefault.OP);
    static final Permission PERMISSION_SETPROPERTY = new Permission("consolename.setproperty", PermissionDefault.OP);

    public static ConsoleName getInstance() {
        return instance;
    }

    /**
     * Sends a broadcast message.
     * 
     * @param prefix the prefix. If null or an empty String is given the default prefix (see: CONFIG_PREFIX_DEFAULT) will be used.
     * @param message the message. Must not be null or empty.
     * @param sender the sender. If null is given no {@link BroadcastEvent} will be called.
     */
    public static void sendBroadcastMessage(final String prefix, final String message, final CommandSender sender) {
        if (getInstance().isFeatureActive()) {
            if (message != null && !message.trim().isEmpty()) {
                final String p = (prefix == null || prefix.trim().isEmpty()) ? ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT : prefix.trim();
                final String m = message.trim();
                Bukkit.getServer().broadcastMessage(p + " " + m);
                if (sender != null) {
                    Bukkit.getPluginManager().callEvent(new BroadcastEvent(prefix, message, sender));
                }
            }
        } else {
            getInstance().getLogger().info("Tried sending broadcast but feature is inactiv.");
        }
    }

    private static void setInstance(final ConsoleName instance) {
        if (ConsoleName.instance == null && instance != null) {
            ConsoleName.instance = instance;
        }
    }

    private static void unsetInstance() {
        if (instance != null) {
            instance = null;
        }
    }

    private boolean active = false;
    private File dataFolder = null;
    private ConsoleNameProperties properties = null;
    private OmcLibPlugin omcLib;
    @Override
    public File getDataFolder() {
        if (dataFolder == null) {
            dataFolder = super.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
        }
        return dataFolder;
    }

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
    public void onDisable() {
        setFeatureActive(false);
        unsetInstance();
    }

    @Override
    public void onEnable() {
        setInstance(this);
        omcLib = (OmcLibPlugin) getServer().getPluginManager().getPlugin("omc-lib");

        getServer().getPluginManager().registerEvents(new ConsoleNameListener(this), this);

        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST).setExecutor(new ConsoleNameBCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_CUSTOM).setExecutor(new ConsoleNameBCCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_SETPREFIX).setExecutor(new ConsoleNameBCSetCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_SETPROPERTY).setExecutor(new ConsoleNameBCPropCommandExecutor(this));

        try {
            omcLib.getFeatureManager().addFeature(this);
            if (!isFeatureActive()) {
                setFeatureActive(true);
            }
        } catch (final UnsupportedOperationException e) {
            getLogger().info("Can not add feature.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void setFeatureActive(final boolean active) {
        if (this.active != active) {
            if (active) {
                getFeatureProperties().loadProperties();
            } else {
                getFeatureProperties().storeProperties();
            }
            this.active = active;
        }
    }

    void updateMetrics(final BroadcastType type){
        final OmcLibMetricsFeature f = omcLib.getMetricsFeature();
        if (f != null){
            final MetricsInstance i = f.getMetricsInstance(this); 
            final MetricsGraph g = i.getGraph("Broadcasts");
            final MetricsPlotter p = g.getPlotter(type.getId());
            p.modifyBalance(1);
            g.updatePlotter(p);
            i.updateGraph(g);            
        }
    }

    void resetDefaultPrefix(final CommandSender sender, final String targetPlayer) {
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

}