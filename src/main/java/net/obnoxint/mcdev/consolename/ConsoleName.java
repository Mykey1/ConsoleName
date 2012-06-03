package net.obnoxint.mcdev.consolename;

import java.io.File;
import java.io.IOException;

import net.obnoxint.mcdev.feature.Feature;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin implements Feature {

    private static ConsoleName instance = null;

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
    public static void sendBroadcastMessage(String prefix, String message, CommandSender sender) {
        if (getInstance().isFeatureActive()) {
            if (message != null && !message.trim().isEmpty()) {
                String p = (prefix == null || prefix.trim().isEmpty()) ? ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT : prefix.trim();
                String m = message.trim();
                Bukkit.getServer().broadcastMessage(p + " " + m);
                if (sender != null) {
                    Bukkit.getPluginManager().callEvent(new BroadcastEvent(prefix, message, sender));
                }
            }
        } else {
            getInstance().getLogger().info("Tried sending broadcast but feature is inactiv.");
        }
    }

    private static void setInstance(ConsoleName instance) {
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

        getServer().getPluginManager().registerEvents(new ConsoleNameListener(this), this);

        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST).setExecutor(new ConsoleNameBCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_CUSTOM).setExecutor(new ConsoleNameBCCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_SETPREFIX).setExecutor(new ConsoleNameBCSetCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_SETPROPERTY).setExecutor(new ConsoleNameBCPropCommandExecutor(this));

        setFeatureActive(true);
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

    void resetDefaultPrefix(CommandSender sender, String targetPlayer) {
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