package dev.ntgame.overenchant.commands;

import dev.ntgame.overenchant.OverEnchantPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class OverEnchantCommand implements CommandExecutor, TabCompleter {

    private final OverEnchantPlugin plugin;

    public OverEnchantCommand(OverEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isDe() {
        return plugin.getOverEnchantConfig() != null && plugin.getOverEnchantConfig().isDe();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("info")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);

        if (sub.equals("reload")) {
            if (!sender.hasPermission("overenchant.admin")) {
                sender.sendMessage(isDe() ? "§8[§dOverEnchant§8] §cDazu hast du keine Berechtigung." : "§8[§dOverEnchant§8] §cYou do not have permission.");
                return true;
            }
            plugin.reloadOverEnchantConfig();
            sender.sendMessage(isDe() ? "§8[§dOverEnchant§8] §aKonfiguration erfolgreich neu geladen." : "§8[§dOverEnchant§8] §aOverEnchant config reloaded successfully.");
            return true;
        }

        if (sub.equals("lang") || sub.equals("language")) {
            if (!sender.hasPermission("overenchant.admin")) {
                sender.sendMessage(isDe() ? "§8[§dOverEnchant§8] §cDazu hast du keine Berechtigung." : "§8[§dOverEnchant§8] §cYou do not have permission.");
                return true;
            }
            if (args.length >= 2) {
                String target = args[1].toLowerCase(Locale.ROOT);
                if (target.equals("de") || target.equals("en")) {
                    plugin.getOverEnchantConfig().setLanguage(target);
                    if ("de".equals(target)) {
                        sender.sendMessage("§8[§dOverEnchant§8] §aSprache wurde auf §eDeutsch§a geändert.");
                    } else {
                        sender.sendMessage("§8[§dOverEnchant§8] §aLanguage set to §eEnglish§a.");
                    }
                    return true;
                }
            }
            sender.sendMessage(isDe()
                    ? "§8[§dOverEnchant§8] §7Aktuelle Sprache: §e" + plugin.getOverEnchantConfig().getLanguage() + " §7(Verfügbar: §bde§7, §ben§7)"
                    : "§8[§dOverEnchant§8] §7Current language: §e" + plugin.getOverEnchantConfig().getLanguage() + " §7(Available: §bde§7, §ben§7)");
            return true;
        }

        sender.sendMessage(isDe() ? "§8[§dOverEnchant§8] §cUnbekannter Befehl. Verwende §e/overenchant help" : "§8[§dOverEnchant§8] §cUnknown command. Use §e/overenchant help");
        return true;
    }

    private void sendHelp(CommandSender sender) {
        if (isDe()) {
            sender.sendMessage("§d§l================= OverEnchant ==================");
            sender.sendMessage("§7Kombiniere im Amboss gleiche Verzauberungsstufen über");
            sender.sendMessage("§7das Vanilla-Maximum hinaus (z.B. Schärfe V + V = VI).");
            sender.sendMessage("§eBefehle:");
            sender.sendMessage("§e/overenchant info §7- Zeigt diese Hilfe an");
            if (sender.hasPermission("overenchant.admin")) {
                sender.sendMessage("§e/overenchant reload §7- Lädt die Konfiguration neu");
                sender.sendMessage("§e/overenchant lang <de|en> §7- Ändert die Sprache (Global)");
            }
            sender.sendMessage("§d§l===============================================");
        } else {
            sender.sendMessage("§d§l================= OverEnchant ==================");
            sender.sendMessage("§7Merge identical enchantment levels in an anvil beyond");
            sender.sendMessage("§7vanilla limits (e.g. Sharpness V + V = VI).");
            sender.sendMessage("§eCommands:");
            sender.sendMessage("§e/overenchant info §7- Shows this help");
            if (sender.hasPermission("overenchant.admin")) {
                sender.sendMessage("§e/overenchant reload §7- Reloads configuration");
                sender.sendMessage("§e/overenchant lang <de|en> §7- Changes language (Global)");
            }
            sender.sendMessage("§d§l===============================================");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>(Collections.singletonList("info"));
            if (sender.hasPermission("overenchant.admin")) {
                list.add("reload");
                list.add("lang");
            }
            List<String> result = new ArrayList<String>();
            String prefix = args[0].toLowerCase(Locale.ROOT);
            for (String s : list) {
                if (s.startsWith(prefix)) {
                    result.add(s);
                }
            }
            return result;
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("lang") || args[0].equalsIgnoreCase("language")) && sender.hasPermission("overenchant.admin")) {
            List<String> options = Arrays.asList("de", "en");
            List<String> result = new ArrayList<String>();
            for (String o : options) {
                if (o.startsWith(args[1].toLowerCase(Locale.ROOT))) {
                    result.add(o);
                }
            }
            return result;
        }
        return Collections.emptyList();
    }
}
