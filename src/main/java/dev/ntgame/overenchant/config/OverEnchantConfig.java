package dev.ntgame.overenchant.config;

import dev.ntgame.overenchant.OverEnchantPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Loads {@code config.yml}: per-enchantment anvil caps plus the anvil XP-cost setting. */
public class OverEnchantConfig {

    /** Every leveled vanilla enchantment config.yml is allowed to reference, keyed by its vanilla id. */
    private static final Map<String, Enchantment> KNOWN_ENCHANTMENTS = new HashMap<>();

    static {
        register(Enchantment.PROTECTION);
        register(Enchantment.FIRE_PROTECTION);
        register(Enchantment.FEATHER_FALLING);
        register(Enchantment.BLAST_PROTECTION);
        register(Enchantment.PROJECTILE_PROTECTION);
        register(Enchantment.RESPIRATION);
        register(Enchantment.AQUA_AFFINITY);
        register(Enchantment.THORNS);
        register(Enchantment.DEPTH_STRIDER);
        register(Enchantment.FROST_WALKER);
        register(Enchantment.BINDING_CURSE);
        register(Enchantment.SHARPNESS);
        register(Enchantment.SMITE);
        register(Enchantment.BANE_OF_ARTHROPODS);
        register(Enchantment.KNOCKBACK);
        register(Enchantment.FIRE_ASPECT);
        register(Enchantment.LOOTING);
        register(Enchantment.SWEEPING_EDGE);
        register(Enchantment.EFFICIENCY);
        register(Enchantment.SILK_TOUCH);
        register(Enchantment.UNBREAKING);
        register(Enchantment.FORTUNE);
        register(Enchantment.POWER);
        register(Enchantment.PUNCH);
        register(Enchantment.FLAME);
        register(Enchantment.INFINITY);
        register(Enchantment.LUCK_OF_THE_SEA);
        register(Enchantment.LURE);
        register(Enchantment.LOYALTY);
        register(Enchantment.IMPALING);
        register(Enchantment.RIPTIDE);
        register(Enchantment.CHANNELING);
        register(Enchantment.MULTISHOT);
        register(Enchantment.QUICK_CHARGE);
        register(Enchantment.PIERCING);
        register(Enchantment.DENSITY);
        register(Enchantment.BREACH);
        register(Enchantment.WIND_BURST);
        register(Enchantment.MENDING);
        register(Enchantment.VANISHING_CURSE);
        register(Enchantment.SOUL_SPEED);
        register(Enchantment.SWIFT_SNEAK);
        registerOptional("LUNGE");

        if (Enchantment.BINDING_CURSE != null) {
            KNOWN_ENCHANTMENTS.put("curse_of_binding", Enchantment.BINDING_CURSE);
        }
        if (Enchantment.VANISHING_CURSE != null) {
            KNOWN_ENCHANTMENTS.put("curse_of_vanishing", Enchantment.VANISHING_CURSE);
        }
    }

    private static void register(Enchantment enchantment) {
        if (enchantment != null) {
            KNOWN_ENCHANTMENTS.put(enchantment.getKey().getKey().toLowerCase(), enchantment);
        }
    }

    private static void registerOptional(String fieldName) {
        try {
            var field = Enchantment.class.getField(fieldName);
            Object val = field.get(null);
            if (val instanceof Enchantment ench) {
                register(ench);
            }
        } catch (Throwable ignored) {
        }
    }

    @SuppressWarnings("deprecation")
    private static org.bukkit.Registry<Enchantment> getEnchantmentRegistry() {
        try {
            return io.papermc.paper.registry.RegistryAccess.registryAccess()
                    .getRegistry(io.papermc.paper.registry.RegistryKey.ENCHANTMENT);
        } catch (Throwable ignored) {
            return org.bukkit.Registry.ENCHANTMENT;
        }
    }

    @SuppressWarnings("deprecation")
    public static Enchantment resolveEnchantment(String key) {
        if (key == null || key.isBlank()) return null;
        String normalized = key.trim().toLowerCase();

        Enchantment enchantment = KNOWN_ENCHANTMENTS.get(normalized);
        if (enchantment != null) return enchantment;

        try {
            org.bukkit.NamespacedKey namespacedKey = normalized.contains(":")
                    ? org.bukkit.NamespacedKey.fromString(normalized)
                    : org.bukkit.NamespacedKey.minecraft(normalized);
            if (namespacedKey != null) {
                var registry = getEnchantmentRegistry();
                if (registry != null) {
                    enchantment = registry.get(namespacedKey);
                    if (enchantment != null) {
                        register(enchantment);
                        return enchantment;
                    }
                }
                enchantment = Enchantment.getByKey(namespacedKey);
                if (enchantment != null) {
                    register(enchantment);
                    return enchantment;
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            enchantment = Enchantment.getByName(normalized.toUpperCase());
            if (enchantment != null) {
                register(enchantment);
                return enchantment;
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private final Map<Enchantment, EnchantRule> rules = new LinkedHashMap<>();
    private int xpCostPerOverlevel = 3;

    public static OverEnchantConfig load(OverEnchantPlugin plugin) {
        OverEnchantConfig config = new OverEnchantConfig();
        plugin.reloadConfig();
        var root = plugin.getConfig();

        config.xpCostPerOverlevel = Math.max(0, root.getInt("anvil.xp-cost-per-overlevel", 3));

        // Populate registry dynamic discoveries
        try {
            var registry = getEnchantmentRegistry();
            if (registry != null) {
                for (Enchantment ench : registry) {
                    register(ench);
                }
            }
        } catch (Throwable ignored) {
        }

        ConfigurationSection section = root.getConfigurationSection("enchantments");
        if (section == null) return config;

        for (String key : section.getKeys(false)) {
            Enchantment enchantment = resolveEnchantment(key);
            if (enchantment == null) {
                plugin.getLogger().warning("Unknown enchantment key in config.yml: '" + key + "' - skipping.");
                continue;
            }
            ConfigurationSection entry = section.getConfigurationSection(key);
            if (entry == null) continue;

            int cap = entry.getInt("cap", enchantment.getMaxLevel());
            if (cap < enchantment.getMaxLevel()) cap = enchantment.getMaxLevel();

            MaxEffect effect = MaxEffect.NONE;
            String effectName = entry.getString("max-effect", "none");
            try {
                effect = MaxEffect.valueOf(effectName.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                plugin.getLogger().warning("Unknown max-effect '" + effectName + "' for enchantment '" + key + "' - defaulting to none.");
            }

            config.rules.put(enchantment, new EnchantRule(enchantment, cap, effect));
        }
        return config;
    }


    public EnchantRule ruleFor(Enchantment enchantment) {
        return rules.get(enchantment);
    }

    public int capFor(Enchantment enchantment) {
        EnchantRule rule = rules.get(enchantment);
        return rule != null ? rule.cap() : enchantment.getMaxLevel();
    }

    public int xpCostPerOverlevel() {
        return xpCostPerOverlevel;
    }

    public Map<Enchantment, EnchantRule> rules() {
        return rules;
    }
}
