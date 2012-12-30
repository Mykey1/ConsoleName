package net.obnoxint.mcdev.consolename;

import net.minecraft.server.v1_4_5.Packet130UpdateSign;
import net.obnoxint.mcdev.mosaic.MosaicBase;
import net.obnoxint.mcdev.mosaic.MosaicBase.MosaicLogger;
import net.obnoxint.mcdev.mosaic.MosaicFeature;
import net.obnoxint.mcdev.mosaic.MosaicFeatureManager;
import net.obnoxint.mcdev.mosaic.MosaicPlugin;
import net.obnoxint.mcdev.mosaic.metrics.MetricsGraph;
import net.obnoxint.mcdev.mosaic.metrics.MetricsInstance;
import net.obnoxint.mcdev.mosaic.metrics.MetricsPlotter;
import net.obnoxint.mcdev.mosaic.metrics.MosaicMetricsFeature;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_4_5.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;

public final class ConsoleNameFeature implements MosaicFeature, Listener {

    private boolean active = false;
    private ConsoleNameProperties properties = null;
    private MosaicLogger logger = null;
    public static final String FEATURE_NAME = "consolename";

    public ConsoleNameFeature() {
        logger = MosaicBase.getMosaicFeatureLogger(this);
    }

    @Override
    public MosaicFeatureManager getMosaicFeatureManager() {
        return MosaicBase.getFeatureManager();
    }

    @Override
    public String getMosaicFeatureName() {
        return FEATURE_NAME;
    }

    @Override
    public Plugin getMosaicFeaturePlugin() {
        return ConsoleName.getInstance();
    }

    @Override
    public ConsoleNameProperties getMosaicFeatureProperties() {
        if (properties == null) {
            properties = new ConsoleNameProperties(this);
        }
        return properties;
    }

    @Override
    public boolean isMosaicFeatureActive() {
        return active;
    }

    @EventHandler
    public void onBlockDamage(final BlockDamageEvent event) {
        final Player p = event.getPlayer();
        final Block b = event.getBlock();
        final Material m = b.getType();
        if ((m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN))
                && getMosaicFeatureProperties().isEnableSignBroadcast()
                && p.getItemInHand().getType().equals(getMosaicFeatureProperties().getSignBroadcastTool())
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
                sendBroadcastMessage(BroadcastType.SIGN, getMosaicFeatureProperties().getPrefix(p), msg);
                Bukkit.getScheduler().runTaskLaterAsynchronously(getMosaicFeaturePlugin(), new Runnable() {

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
        final Player p = event.getPlayer();
        if (msg.startsWith("/say ") && msg.length() > 5 && getMosaicFeatureProperties().isOverrideSayCommand()) {
            if (p.hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_SAY)) {
                sendBroadcastMessage(BroadcastType.SAY_PLAYER, getMosaicFeatureProperties().getPrefix(event.getPlayer()), msg.substring(4));
            } else {
                p.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SENDBROADCAST_SAY);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerCommand(final ServerCommandEvent event) {
        final String cmd = event.getCommand();
        if (cmd.startsWith("say ") && cmd.length() > 4 && getMosaicFeatureProperties().isOverrideSayCommand()) {
            sendBroadcastMessage(BroadcastType.SAY_SERVER, getMosaicFeatureProperties().getPrefix(), cmd.substring(3));
            event.setCommand(""); // prevent the server from executing the command.
        }
    }

    public void sendBroadcastMessage(final CommandSender sender, final String message) {
        final String prefix = (sender instanceof Player) ? getMosaicFeatureProperties().getPrefix((Player) sender) : getMosaicFeatureProperties().getPrefix();
        sendBroadcastMessage(BroadcastType.SIMPLE, prefix, message);
    }

    public void sendBroadcastMessage(final String message) {
        sendBroadcastMessage(Bukkit.getConsoleSender(), message);
    }

    public void sendBroadcastMessage(final String prefix, final String message) {
        sendBroadcastMessage(prefix, message, false);
    }

    public void sendBroadcastMessage(final String prefix, final String message, final boolean replaceChatFormatSymbol) {
        sendBroadcastMessage(BroadcastType.CUSTOM,
                (replaceChatFormatSymbol) ? getMosaicFeatureProperties().replaceChatFormatSymbol(prefix) : prefix,
                (replaceChatFormatSymbol) ? getMosaicFeatureProperties().replaceChatFormatSymbol(message) : message);
    }

    @Override
    public void setMosaicFeatureActive(final boolean active) {
        if (this.active != active) {
            if (active) {
                getMosaicFeatureProperties().load();
            } else {
                getMosaicFeatureProperties().store();
            }
            this.active = active;
        }
    }

    void resetDefaultPrefix(final CommandSender sender, final Player targetPlayer) {
        String msg;
        if (targetPlayer == null) {
            getMosaicFeatureProperties().setPrefix(ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT);
            msg = "Broadcast prefix reset to: " + ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT;
        } else {
            getMosaicFeatureProperties().setPrefix(targetPlayer, null);
            msg = "Broadcast prefix of player " + targetPlayer.getName() + " removed.";
        }
        sender.sendMessage(msg);
    }

    void sendBroadcastMessage(final BroadcastType type, final String prefix, final String message) {
        if (isMosaicFeatureActive() && message != null && !message.trim().isEmpty()) {
            Bukkit.getServer().broadcastMessage((prefix == null || prefix.trim().isEmpty()) ? ConsoleNameProperties.PROPERTY_PREFIX_DEFAULT : prefix.trim() + " " + message.trim());
            updateMetrics(type);
        } else {
            logger.info("Tried sending broadcast but feature is inactive.");
        }
    }

    private void updateMetrics(final BroadcastType type) {
        final MosaicMetricsFeature f = MosaicPlugin.getInstance().getMetricsFeature();
        if (f != null) {
            final MetricsInstance i = f.getMetricsInstance(ConsoleName.getInstance());
            final MetricsGraph g = i.getGraph("Broadcasts");
            final MetricsPlotter p = g.getPlotter(type.getId());
            p.modifyBalance(1);
            g.updatePlotter(p);
            i.updateGraph(g);
        }
    }

}
