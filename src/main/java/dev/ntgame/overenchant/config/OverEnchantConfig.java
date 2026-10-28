package dev.ntgame.overenchant.config;

import dev.ntgame.overenchant.OverEnchantPlugin;
import dev.ntgame.overenchant.util.XOverEnchantVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Loads config.yml: per-enchantment anvil caps plus the anvil XP-cost setting. */
public class OverEnchantConfig {

    private final Map<Enchantment, EnchantRule> rules = new LinkedHashMap<Enchantment, EnchantRule>();
    private int xpCostPerOverlevel = 3;
    private String language = "en";
    private OverEnchantPlugin plugin;

    public static OverEnchantConfig load(OverEnchantPlugin plugin) {
        OverEnchantConfig config = new OverEnchantConfig();
        config.plugin = plugin;
        plugin.reloadConfig();
        FileConfiguration root = plugin.getConfig();

        config.language = root.getString("language", "en").toLowerCase(Locale.ROOT);
        if (!"de".equals(config.language) && !"en".equals(config.language)) {
            config.language = "en";
        }

        config.xpCostPerOverlevel = Math.max(0, root.getInt("anvil.xp-cost-per-overlevel", 3));

        ConfigurationSection section = root.getConfigurationSection("enchantments");
        if (section == null) return config;

        for (String key : section.getKeys(false)) {
            Enchantment enchantment = XOverEnchantVersion.resolveEnchantment(key);
            if (enchantment == null) {
                // In older Minecraft versions (e.g. 1.8), modern enchantments like density/wind_burst do not exist
                plugin.getLogger().fine("Enchantment '" + key + "' is not present on this Minecraft version - skipped.");
                continue;
            }
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            int cap = entry.getInt("cap", enchantment.getMaxLevel());
            if (cap < enchantment.getMaxLevel()) cap = enchantment.getMaxLevel();

            MaxEffect effect = MaxEffect.NONE;
            String effectName = entry.getString("max-effect", "none");
            try {
                effect = MaxEffect.valueOf(effectName.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Unknown max-effect '" + effectName + "' for enchantment '" + key + "' - defaulting to none.");
            }

            config.rules.put(enchantment, new EnchantRule(enchantment, cap, effect));
        }
        return config;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String lang) {
        String clean = (lang != null && lang.equalsIgnoreCase("de")) ? "de" : "en";
        this.language = clean;
        if (plugin != null) {
            plugin.getConfig().set("language", clean);
            plugin.saveConfig();
        }
    }

    public boolean isDe() {
        return "de".equalsIgnoreCase(language);
    }

    public EnchantRule ruleFor(Enchantment enchantment) {
        return rules.get(enchantment);
    }

    public int capFor(Enchantment enchantment) {
        EnchantRule rule = rules.get(enchantment);
        return rule != null ? rule.cap() : (enchantment != null ? enchantment.getMaxLevel() : 1);
    }

    public int xpCostPerOverlevel() {
        return xpCostPerOverlevel;
    }

    public Map<Enchantment, EnchantRule> rules() {
        return rules;
    }
}
