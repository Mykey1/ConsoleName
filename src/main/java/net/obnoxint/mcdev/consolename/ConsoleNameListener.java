package net.obnoxint.mcdev.consolename;

import net.minecraft.server.Packet130UpdateSign;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

class ConsoleNameListener implements Listener {

    private final ConsoleName plugin;

    public ConsoleNameListener(final ConsoleName plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event) {
        final Player p = event.getPlayer();
        final Block b = event.getBlock();
        final Material m = b.getType();
        if ((m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN))
                && plugin.getFeatureProperties().isEnableSignBroadcast()
                && p.getItemInHand().getType().equals(plugin.getFeatureProperties().getSignBroadcastTool())
                && p.hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_SIGN)) {
            final Sign s = (Sign) b.getState();
            String msg = "";
            for (int i = 0; i < s.getLines().length; i++) {
                final String l = s.getLine(i).trim();
                if (!l.isEmpty()) {
                    msg += l + " ";
                }
            }
            if (!msg.isEmpty()) {
                ConsoleName.sendBroadcastMessage(BroadcastType.SIGN, plugin.getFeatureProperties().getPrefix(p), msg);
                Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {

                    @Override
                    public void run() {
                        ((CraftPlayer) p).getHandle().netServerHandler.sendPacket(new Packet130UpdateSign(b.getX(), b.getY(), b.getZ(), s.getLines()));
                    }
                }, 50);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        final String msg = event.getMessage();
        if (msg.startsWith("/say ") && msg.length() > 5 && plugin.getFeatureProperties().isOverrideSayCommand()) {
            ConsoleName.sendBroadcastMessage(BroadcastType.SAY_PLAYER, plugin.getFeatureProperties().getPrefix(event.getPlayer()), msg.substring(4));
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(final ServerCommandEvent event) {
        final String cmd = event.getCommand();
        if (cmd.startsWith("say ") && cmd.length() > 4 && plugin.getFeatureProperties().isOverrideSayCommand()) {
            ConsoleName.sendBroadcastMessage(BroadcastType.SAY_SERVER, plugin.getFeatureProperties().getPrefix(), cmd.substring(3));
            event.setCommand(""); // prevent the server from executing the command.
        }
    }

}
