package net.obnoxint.mcdev.consolename;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin implements Listener {

    static final String NO_PERMISSION_MSG = "You don't have the permission required in order to use this command: §o";

    static final Permission PERMISSION_SENDBROADCAST = new Permission("consolename.sendbroadcast", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_CUSTOM = new Permission("consolename.sendbroadcast.custom", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_SIGN = new Permission("consolename.sendbroadcast.sign", PermissionDefault.OP);
    static final Permission PERMISSION_SENDBROADCAST_SAY = new Permission("consolename.sendbroadcast.say", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_GLOBAL = new Permission("consolename.setprefix.global", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OTHER = new Permission("consolename.setprefix.other", PermissionDefault.OP);
    static final Permission PERMISSION_SETPREFIX_OWN = new Permission("consolename.setprefix.own", PermissionDefault.OP);
    static final Permission PERMISSION_SETPROPERTY = new Permission("consolename.setproperty", PermissionDefault.OP);

    private static final String CHAT_FORMAT_SYMBOL = "§";

    private ConsoleNameConfiguration configuration;

    public ConsoleNameConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    @Override
    public void onEnable() {
        configuration = new ConsoleNameConfiguration(this);
        System.out.print(replaceChatFormatSymbol("&1Hallo"));

        getServer().getPluginManager().registerEvents(this, this);

        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST).setExecutor(new ConsoleNameBCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_CUSTOM).setExecutor(new ConsoleNameBCCCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_BROADCAST_SETPREFIX).setExecutor(new ConsoleNameBCSetCommandExecutor(this));
        getCommand(ConsoleNameCommandExecutor.COMMAND_SETPROPERTY).setExecutor(new ConsoleNameBCPropCommandExecutor(this));

    }

    public void sendBroadcastMessage(final CommandSender sender, final String message) {
        final String prefix = (sender instanceof Player) ? configuration.getPrefix((Player) sender) : configuration.getPrefix();
        sendBroadcastMessage(prefix, message, true);
    }

    public void sendBroadcastMessage(final String message) {
        sendBroadcastMessage(Bukkit.getConsoleSender(), message);
    }

    public void sendBroadcastMessage(final String prefix, final String message, final boolean replaceChatFormatSymbol) {
        final String p = replaceChatFormatSymbol ? replaceChatFormatSymbol(prefix) : prefix;
        final String m = replaceChatFormatSymbol ? replaceChatFormatSymbol(message) : message;
        Bukkit.broadcastMessage(p + " " + m);
    }

    @EventHandler
    void onBlockDamage(final BlockDamageEvent event) {
        final Player p = event.getPlayer();
        final Block b = event.getBlock();
        final Material m = b.getType();
        if ((m.equals(Material.SIGN_POST) || m.equals(Material.WALL_SIGN))
                && configuration.isEnableSignBroadcast()
                && p.getItemInHand().getType().equals(configuration.getSignBroadcastTool())
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
                sendBroadcastMessage(configuration.getPrefix(p), msg, true);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        final String msg = event.getMessage();
        final Player p = event.getPlayer();
        if (msg.startsWith("/say ") && msg.length() > 5 && configuration.isOverrideSayCommand()) {
            if (p.hasPermission(ConsoleName.PERMISSION_SENDBROADCAST_SAY)) {
                sendBroadcastMessage(configuration.getPrefix(event.getPlayer()), msg.substring(4), true);
            } else {
                p.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SENDBROADCAST_SAY);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void onServerCommand(final ServerCommandEvent event) {
        final String cmd = event.getCommand();
        if (cmd.startsWith("say ") && cmd.length() > 4 && configuration.isOverrideSayCommand()) {
            sendBroadcastMessage(configuration.getPrefix(), cmd.substring(3), true);
            event.setCommand(""); // prevent the server from executing the command.
        }
    }

    private String replaceChatFormatSymbol(final String string) {
        return string.replace(configuration.getChatFormatSymbol(), CHAT_FORMAT_SYMBOL);
    }

}