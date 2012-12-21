package net.obnoxint.mcdev.consolename;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

final class ConsoleNameBCPropCommandExecutor extends ConsoleNameCommandExecutor {

    private enum PropertyAlias {
        CHAT_FORMAT_SYMBOL("chatformatsymbol", "cfs"),
        OVERRIDE_SAY_COMMAND("overridesaycommand", "osc"),
        ENABLE_SIGN_BROADCAST("enablesignbroadcast", "esb"),
        SIGN_BROADCAST_TOOL("signbroadcasttool", "sbt");

        private static HashMap<String, PropertyAlias> aliasMap;

        static {
            aliasMap = new HashMap<>();
            for (final PropertyAlias a : values()) {
                for (final String s : a.aliases) {
                    aliasMap.put(s, a);
                }
            }
        }

        static PropertyAlias getByAlias(final String alias) {
            return aliasMap.get(alias.toLowerCase());
        }

        private final String[] aliases;

        private PropertyAlias(final String... aliases) {
            this.aliases = aliases;
        }
    }

    ConsoleNameBCPropCommandExecutor() {
        super();
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2 && ((sender instanceof Player) ? ((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPROPERTY) : true)) {
            final PropertyAlias alias = PropertyAlias.getByAlias(args[0]);
            if (alias != null) {
                boolean b;
                switch (alias) {
                case CHAT_FORMAT_SYMBOL:
                    final String s = args[1];
                    getFeature().getMosaicFeatureProperties().setChatFormatSymbol(s);
                    sender.sendMessage("The new chat format symbol is \"" + s + "\".");
                break;
                case OVERRIDE_SAY_COMMAND:
                    b = Boolean.valueOf(args[1]);
                    getFeature().getMosaicFeatureProperties().setOverrideSayCommand(b);
                    sender.sendMessage("The 'say' command will" + ((!b) ? " not" : "") + " be overridden by the '" + COMMAND_BROADCAST + "' command.");
                break;
                case ENABLE_SIGN_BROADCAST:
                    b = Boolean.valueOf(args[1]);
                    getFeature().getMosaicFeatureProperties().setEnableSignBroadcast(b);
                    sender.sendMessage("Sending broadcasts by left-clicking signs with §o" + getFeature().getMosaicFeatureProperties().getSignBroadcastTool().name() + "§r is "
                            + ((!b) ? "disabled." : "enabled. "));
                break;
                case SIGN_BROADCAST_TOOL:
                    Material m;
                    if (sender instanceof Player && args[1].equals("~")) {
                        m = ((Player) sender).getItemInHand().getType();
                    } else {
                        try {
                            m = Material.getMaterial(Integer.parseInt(args[1]));
                            if (m == null) {
                                sender.sendMessage("Material §o" + args[1] + "§r does not exist.");
                                return true;
                            }
                        } catch (final NumberFormatException e) {
                            sender.sendMessage("§o" + args[1] + "§r is not a valid material id.");
                            return true;
                        }
                    }
                    getFeature().getMosaicFeatureProperties().setSignBroadcastTool(m);
                    sender.sendMessage("Sign broadcast tool set to §o" + m.name() + "§r.");
                break;
                }
                return true;
            }
        } else if (sender instanceof Player && !((Player) sender).hasPermission(ConsoleName.PERMISSION_SETPROPERTY)) {
            sender.sendMessage(ConsoleName.NO_PERMISSION_MSG + ConsoleName.PERMISSION_SETPROPERTY.getName());
            return true;
        }
        return false;
    }
}
