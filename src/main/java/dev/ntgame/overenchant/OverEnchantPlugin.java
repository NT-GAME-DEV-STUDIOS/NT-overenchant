package dev.ntgame.overenchant;

import dev.ntgame.overenchant.commands.OverEnchantCommand;
import dev.ntgame.overenchant.config.OverEnchantConfig;
import dev.ntgame.overenchant.listeners.AnvilOverenchantListener;
import dev.ntgame.overenchant.listeners.InstamineListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class OverEnchantPlugin extends JavaPlugin {

    private OverEnchantConfig overEnchantConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        overEnchantConfig = OverEnchantConfig.load(this);

        getServer().getPluginManager().registerEvents(new AnvilOverenchantListener(this), this);
        getServer().getPluginManager().registerEvents(new InstamineListener(this), this);

        PluginCommand command = getCommand("overenchant");
        if (command != null) {
            OverEnchantCommand cmd = new OverEnchantCommand(this);
            command.setExecutor(cmd);
            command.setTabCompleter(cmd);
        }

        getLogger().info("OverEnchant v" + getDescription().getVersion() + " activated (Cross-version 1.8 - 1.21.4+).");
    }

    public void reloadOverEnchantConfig() {
        overEnchantConfig = OverEnchantConfig.load(this);
    }

    public OverEnchantConfig getOverEnchantConfig() {
        return overEnchantConfig;
    }
}
