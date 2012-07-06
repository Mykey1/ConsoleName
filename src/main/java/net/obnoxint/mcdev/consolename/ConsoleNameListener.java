package net.obnoxint.mcdev.consolename;

import net.minecraft.server.Packet130UpdateSign;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
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
        final Player p = event.getPlayer();
        final Block b = event.getBlock();
        Material m = b.getType();
        if ((m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN))
                && plugin.getFeatureProperties().isEnableSignBroadcast()
                && p.getItemInHand().getType().equals(plugin.getFeatureProperties().getSignBroadcastTool())
                && p.hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_SIGN)) {
            final Sign s = (Sign) b.getState();
            String msg = "";
            for (int i = 0; i < s.getLines().length; i++) {
                String l = s.getLine(i).trim();
                if (!l.isEmpty()) {
                    msg += l + " ";
                }
            }
            if (!msg.isEmpty()) {
                ConsoleName.sendBroadcastMessage(plugin.getFeatureProperties().getPrefix(p), plugin.getFeatureProperties().replaceChatFormatSymbol(msg), p);
                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {                    
                    @Override
                    public void run() {
                        ((CraftPlayer)p).getHandle().netServerHandler.sendPacket(new Packet130UpdateSign(b.getX(), b.getY(), b.getZ(), s.getLines()));
                    }
                }, 50);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (msg.startsWith("/say ") && msg.length() > 5 && plugin.getFeatureProperties().isOverrideSayCommand()) {
            msg = "/" + ConsoleNameCommandExecutor.COMMAND_BROADCAST + msg.substring(4);
            event.setMessage(msg);
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String cmd = event.getCommand();
        if (cmd.startsWith("say ") && cmd.length() > 4 && plugin.getFeatureProperties().isOverrideSayCommand()) {
            cmd = ConsoleNameCommandExecutor.COMMAND_BROADCAST + cmd.substring(3);
            event.setCommand(cmd);
        }
    }

}
