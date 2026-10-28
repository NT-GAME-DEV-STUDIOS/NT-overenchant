package dev.ntgame.overenchant.commands;

import dev.ntgame.overenchant.OverEnchantPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class OverEnchantCommand implements CommandExecutor, TabCompleter {

    private final OverEnchantPlugin plugin;

    public OverEnchantCommand(OverEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadOverEnchantConfig();
            sender.sendMessage("§aOverEnchant config reloaded.");
            return true;
        }
        sender.sendMessage("§cUsage: /overenchant reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}
