package net.obnoxint.mcdev.consolename;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

class ConsoleNameListener implements Listener {

    private final ConsoleName plugin;

    public ConsoleNameListener(ConsoleName plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Block b = event.getBlock();
        Material m = b.getType();
        Player p = event.getPlayer();
        if ((m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN))
                && plugin.getFeatureProperties().isEnableSignBroadcast()
                && p.getItemInHand().getType().equals(plugin.getFeatureProperties().getSignBroadcastTool())
                && p.hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_SIGN)) {
            Sign s = (Sign) b;
            String msg = "";
            for (int i = 0; i < s.getLines().length; i++) {
                String l = s.getLine(i).trim();
                if (!l.isEmpty()) {
                    msg += l + " ";
                }
            }
            msg = msg.trim();
            if (!msg.isEmpty()) {
                ConsoleName.sendBroadcastMessage(plugin.getFeatureProperties().getPrefix(p), msg, p);
                event.setCancelled(true);
            }
        }
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
