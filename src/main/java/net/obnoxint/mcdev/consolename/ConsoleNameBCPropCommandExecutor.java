package net.obnoxint.mcdev.consolename;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCPropCommandExecutor extends ConsoleNameCommandExecutor {

    private enum PropertyAlias {
        CHAT_FORMAT_SYMBOL("chatformatsymbol", "cfs"),
        OVERRIDE_SAY_COMMAND("overridesaycommand", "osc");

        private static HashMap<String, PropertyAlias> aliasMap;

        static {
            aliasMap = new HashMap<String, PropertyAlias>();
            for (PropertyAlias a : values()) {
                for (String s : a.aliases) {
                    aliasMap.put(s, a);
                }
            }
        }

        static PropertyAlias getByAlias(String alias) {
            return aliasMap.get(alias);
        }

        private final String[] aliases;

        private PropertyAlias(String... aliases) {
            this.aliases = aliases;
        }
    }

    ConsoleNameBCPropCommandExecutor(ConsoleName plugin) {
        super(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2 && ((sender instanceof Player) ? ((Player) sender).hasPermission(PERMISSION_SETPROPERTY) : true)) {
            PropertyAlias alias = PropertyAlias.getByAlias(args[0]);
            if (alias != null) {
                ConsoleNameProperties prop = getPlugin().getFeatureProperties();
                switch (alias) {
                case CHAT_FORMAT_SYMBOL:
                    String s = args[1];
                    prop.setChatFormatSymbol(s);
                    sender.sendMessage("The new chat format symbol is \"" + s + "\".");
                break;
                case OVERRIDE_SAY_COMMAND:
                    boolean b = Boolean.valueOf(args[1]);
                    prop.setOverrideSayCommand(b);
                    sender.sendMessage("The 'say' command will" + ((!b) ? " not" : "") + " be overridden by the '" + COMMAND_BROADCAST + "' command.");
                break;
                }
                return true;
            }
        } else if (sender instanceof Player && !((Player) sender).hasPermission(PERMISSION_SETPROPERTY)) {
            sender.sendMessage(NO_PERMISSION_MSG + PERMISSION_SETPROPERTY.getName());
            return true;
        }
        return false;
    }
}
