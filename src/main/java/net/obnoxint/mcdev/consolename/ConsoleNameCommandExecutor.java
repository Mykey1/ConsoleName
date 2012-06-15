package net.obnoxint.mcdev.consolename;

import org.bukkit.command.CommandExecutor;

abstract class ConsoleNameCommandExecutor implements CommandExecutor {

    protected static final String COMMAND_BROADCAST = "bc";
    protected static final String COMMAND_BROADCAST_CUSTOM = "bcc";
    protected static final String COMMAND_BROADCAST_CUSTOM_ARG_MSG = "--msg=";
    protected static final String COMMAND_BROADCAST_CUSTOM_ARG_PRE = "--pre=";
    protected static final String COMMAND_BROADCAST_SETPREFIX = "bcset";
    protected static final String COMMAND_SETPROPERTY = "bcprop";

    private final ConsoleName plugin;

    protected ConsoleNameCommandExecutor(ConsoleName plugin) {
        this.plugin = plugin;
    }

    protected final ConsoleName getPlugin() {
        return plugin;
    }

}
