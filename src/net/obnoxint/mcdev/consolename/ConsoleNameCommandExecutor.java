package net.obnoxint.mcdev.consolename;

import org.bukkit.command.CommandExecutor;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

abstract class ConsoleNameCommandExecutor implements CommandExecutor {

    protected static final String COMMAND_BROADCAST = "bc";
    protected static final String COMMAND_BROADCAST_CUSTOM = "bcc";
    protected static final String COMMAND_BROADCAST_CUSTOM_ARG_MSG = "--msg=";
    protected static final String COMMAND_BROADCAST_CUSTOM_ARG_PRE = "--pre=";
    protected static final String COMMAND_BROADCAST_SETPREFIX = "bcset";
    protected static final String COMMAND_SETPROPERTY = "bcprop";

    protected static final String NO_PERMISSION_MSG = "You don't have the permission required in order to use this command: ";

    protected static final Permission PERMISSION_SENDBROADCAST = new Permission("consolename.sendbroadcast", PermissionDefault.OP);
    protected static final Permission PERMISSION_SENDBROADCAST_CUSTOM = new Permission("consolename.sendbroadcast.custom", PermissionDefault.OP);
    protected static final Permission PERMISSION_SETPREFIX_GLOBAL = new Permission("consolename.setprefix.global", PermissionDefault.OP);
    protected static final Permission PERMISSION_SETPREFIX_OTHER = new Permission("consolename.setprefix.other", PermissionDefault.OP);
    protected static final Permission PERMISSION_SETPREFIX_OWN = new Permission("consolename.setprefix.own", PermissionDefault.OP);
    protected static final Permission PERMISSION_SETPROPERTY = new Permission("consolename.setproperty", PermissionDefault.OP);

    private final ConsoleName plugin;

    protected ConsoleNameCommandExecutor(ConsoleName plugin) {
        this.plugin = plugin;
    }

    protected final ConsoleName getPlugin() {
        return plugin;
    }

}
