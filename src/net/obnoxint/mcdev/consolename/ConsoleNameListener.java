package net.obnoxint.mcdev.consolename;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

class ConsoleNameListener implements Listener {

    private final ConsoleName plugin;

    public ConsoleNameListener(ConsoleName plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith("/say") && plugin.getFeatureProperties().isOverrideSayCommand()) {
            msg = "/" + ConsoleNameCommandExecutor.COMMAND_BROADCAST + " " + msg.substring(5);
            event.setMessage(msg);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String cmd = event.getCommand();
        if (cmd.startsWith("say") && plugin.getFeatureProperties().isOverrideSayCommand()) {
            cmd = ConsoleNameCommandExecutor.COMMAND_BROADCAST + " " + cmd.substring(4);
            event.setCommand(cmd);
        }
    }

}
