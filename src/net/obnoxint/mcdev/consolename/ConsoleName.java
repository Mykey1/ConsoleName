package net.obnoxint.mcdev.consolename;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConsoleName extends JavaPlugin {

	public static class ConsoleNameListener implements Listener {

		private final ConsoleName plugin;

		public ConsoleNameListener(ConsoleName plugin) {
			this.plugin = plugin;
		}

		@EventHandler
		public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
			String msg = event.getMessage();
			if (msg.startsWith("/say") && plugin.isOverrideSayCommand()) {
				msg = "/" + COMMAND_BROADCAST + " " + msg.substring(5);
				event.setMessage(msg);
			}
		}

		@EventHandler
		public void onServerCommand(ServerCommandEvent event) {
			String cmd = event.getCommand();
			if (cmd.startsWith("say") && plugin.isOverrideSayCommand()) {
				cmd = COMMAND_BROADCAST + " " + cmd.substring(4);
				event.setCommand(cmd);
			}
		}

	}

	private static final String COMMAND_BROADCAST = "bc";

	private static final boolean CONFIG_OVERRIDESAYCOMMAND_DEFAULT = false;

	private static final String CONFIG_OVERRIDESAYCOMMAND_PATH = "overrideSayCommand";

	private static final String CONFIG_PREFIX_DEFAULT = ChatColor.ITALIC.toString() + ChatColor.GOLD.toString() + "[Console]" + ChatColor.RESET.toString() + ":";

	private static final String CONFIG_PREFIX_PATH = "prefix";

	private File configFile = null;

	private boolean overrideSayCommand;

	private String prefix;

	/**
	 * @return The broadcast prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @return true if the 'say' command is being overriden by the 'bc' command.
	 */
	public boolean isOverrideSayCommand() {
		return overrideSayCommand;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase(COMMAND_BROADCAST) && args.length > 0) {
			String s = "";
			for (int i = 0; i < args.length; i++) {
				s = s + " " + args[i];
			}
			getServer().broadcastMessage(getPrefix() + s);
			return true;
		}
		return false;
	}

	@Override
	public void onDisable() {
		saveConfigFile();
	}

	@Override
	public void onEnable() {
		setPrefix(getConfig().getString(CONFIG_PREFIX_PATH, CONFIG_PREFIX_DEFAULT));
		setOverrideSayCommand(getConfig().getBoolean(CONFIG_OVERRIDESAYCOMMAND_PATH, CONFIG_OVERRIDESAYCOMMAND_DEFAULT));
		getServer().getPluginManager().registerEvents(new ConsoleNameListener(this), this);
	}

	/**
	 * Enables or disables the plugins function to override the 'say' command.
	 * 
	 * @param overrideSayCommand true if the 'say' command should be overridden.
	 */
	public void setOverrideSayCommand(boolean overrideSayCommand) {
		this.overrideSayCommand = overrideSayCommand;
	}

	/**
	 * Sets the broadcast prefix.
	 * 
	 * @param prefix The broadcast prefix. May contain chat format codes.
	 */
	public void setPrefix(String prefix) {
		if (prefix != null && !prefix.isEmpty()) {
			this.prefix = prefix;
		}
	}

	private File getConfigFile() {
		if (configFile == null) {
			configFile = new File(getDataFolder(), "config.yml");
		}
		return configFile;
	}

	private void saveConfigFile() {
		getConfig().set(CONFIG_PREFIX_PATH, getPrefix());
		getConfig().set(CONFIG_OVERRIDESAYCOMMAND_PATH, isOverrideSayCommand());
		try {
			getConfig().save(getConfigFile());
		} catch (IOException e) {
			getLogger().severe(getDescription().getName() + " failed to save the configuration file.");
		}
	}

}