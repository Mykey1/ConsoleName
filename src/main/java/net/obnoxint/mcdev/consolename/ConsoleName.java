package net.obnoxint.mcdev.consolename;

import net.obnoxint.mcdev.mosaic.MosaicBase;
import net.obnoxint.mcdev.mosaic.MosaicFeature;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class ConsoleName extends MosaicBase {

    private static ConsoleName instance = null;

    static final String NO_PERMISSION_MSG = "You don't have the permission required in order to use this command: §o";

    static final Permission PERMISSION_SENDBROADCAST = new Permission("consolename.sendbroadcast", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_CUSTOM = new Permission("consolename.sendbroadcast.custom", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_SIGN = new Permission("consolename.sendbroadcast.sign", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_SAY = new Permission("consolename.sendbroadcast.say", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_GLOBAL = new Permission("consolename.setprefix.global", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OTHER = new Permission("consolename.setprefix.other", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OWN = new Permission("consolename.setprefix.own", PermissionDefault.OP);
    static final Permission PERMISSION_SETPROPERTY = new Permission("consolename.setproperty", PermissionDefault.OP);

    public static ConsoleNameFeature getConsoleNameFeature() {
        return (ConsoleNameFeature) MosaicBase.getFeatureManager().getManagedFeature(ConsoleNameFeature.FEATURE_NAME, false);
    }

    public static ConsoleName getInstance() {
        return instance;
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

    @Override
    public Class<? extends MosaicFeature>[] getImplementedMosaicFeatures() {
        @SuppressWarnings("unchecked")
        final Class<? extends MosaicFeature>[] r = new Class[1];
        r[0] = ConsoleNameFeature.class;
        return r;
    }

    @Override
    public void onDisable() {
        unsetInstance();
    }

    @Override
    public void onEnable() {
        setInstance(this);

        getServer().getPluginManager().registerEvents(getConsoleNameFeature(), this);

        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST).setExecutor(new ConsoleNameBCCommandExecutor());
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_CUSTOM).setExecutor(new ConsoleNameBCCCommandExecutor());
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_SETPREFIX).setExecutor(new ConsoleNameBCSetCommandExecutor());
        getCommand(ConsoleNameCommandExecutor.COMMAND_SETPROPERTY).setExecutor(new ConsoleNameBCPropCommandExecutor());

    }

}