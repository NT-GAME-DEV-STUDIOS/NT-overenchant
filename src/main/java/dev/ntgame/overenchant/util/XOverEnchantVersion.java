package dev.ntgame.overenchant.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class XOverEnchantVersion {

    private static final Map<String, String> MODERN_TO_LEGACY_NAMES = new HashMap<>();

    static {
        // Map modern vanilla keys to 1.8-1.12 Bukkit Enchantment names
        MODERN_TO_LEGACY_NAMES.put("sharpness", "DAMAGE_ALL");
        MODERN_TO_LEGACY_NAMES.put("smite", "DAMAGE_UNDEAD");
        MODERN_TO_LEGACY_NAMES.put("bane_of_arthropods", "DAMAGE_ARTHROPODS");
        MODERN_TO_LEGACY_NAMES.put("knockback", "KNOCKBACK");
        MODERN_TO_LEGACY_NAMES.put("fire_aspect", "FIRE_ASPECT");
        MODERN_TO_LEGACY_NAMES.put("looting", "LOOT_BONUS_MOBS");
        MODERN_TO_LEGACY_NAMES.put("sweeping_edge", "SWEEPING_EDGE");
        MODERN_TO_LEGACY_NAMES.put("efficiency", "DIG_SPEED");
        MODERN_TO_LEGACY_NAMES.put("silk_touch", "SILK_TOUCH");
        MODERN_TO_LEGACY_NAMES.put("unbreaking", "DURABILITY");
        MODERN_TO_LEGACY_NAMES.put("fortune", "LOOT_BONUS_BLOCKS");
        MODERN_TO_LEGACY_NAMES.put("power", "ARROW_DAMAGE");
        MODERN_TO_LEGACY_NAMES.put("punch", "ARROW_KNOCKBACK");
        MODERN_TO_LEGACY_NAMES.put("flame", "ARROW_FIRE");
        MODERN_TO_LEGACY_NAMES.put("infinity", "ARROW_INFINITE");
        MODERN_TO_LEGACY_NAMES.put("luck_of_the_sea", "LUCK");
        MODERN_TO_LEGACY_NAMES.put("lure", "LURE");
        MODERN_TO_LEGACY_NAMES.put("protection", "PROTECTION_ENVIRONMENTAL");
        MODERN_TO_LEGACY_NAMES.put("fire_protection", "PROTECTION_FIRE");
        MODERN_TO_LEGACY_NAMES.put("feather_falling", "PROTECTION_FALL");
        MODERN_TO_LEGACY_NAMES.put("blast_protection", "PROTECTION_EXPLOSIONS");
        MODERN_TO_LEGACY_NAMES.put("projectile_protection", "PROTECTION_PROJECTILE");
        MODERN_TO_LEGACY_NAMES.put("respiration", "OXYGEN");
        MODERN_TO_LEGACY_NAMES.put("aqua_affinity", "WATER_WORKER");
        MODERN_TO_LEGACY_NAMES.put("thorns", "THORNS");
        MODERN_TO_LEGACY_NAMES.put("depth_strider", "DEPTH_STRIDER");
        MODERN_TO_LEGACY_NAMES.put("frost_walker", "FROST_WALKER");
        MODERN_TO_LEGACY_NAMES.put("mending", "MENDING");
        MODERN_TO_LEGACY_NAMES.put("binding_curse", "BINDING_CURSE");
        MODERN_TO_LEGACY_NAMES.put("curse_of_binding", "BINDING_CURSE");
        MODERN_TO_LEGACY_NAMES.put("vanishing_curse", "VANISHING_CURSE");
        MODERN_TO_LEGACY_NAMES.put("curse_of_vanishing", "VANISHING_CURSE");
        MODERN_TO_LEGACY_NAMES.put("loyalty", "LOYALTY");
        MODERN_TO_LEGACY_NAMES.put("impaling", "IMPALING");
        MODERN_TO_LEGACY_NAMES.put("riptide", "RIPTIDE");
        MODERN_TO_LEGACY_NAMES.put("channeling", "CHANNELING");
        MODERN_TO_LEGACY_NAMES.put("multishot", "MULTISHOT");
        MODERN_TO_LEGACY_NAMES.put("quick_charge", "QUICK_CHARGE");
        MODERN_TO_LEGACY_NAMES.put("piercing", "PIERCING");
        MODERN_TO_LEGACY_NAMES.put("density", "DENSITY");
        MODERN_TO_LEGACY_NAMES.put("breach", "BREACH");
        MODERN_TO_LEGACY_NAMES.put("wind_burst", "WIND_BURST");
        MODERN_TO_LEGACY_NAMES.put("soul_speed", "SOUL_SPEED");
        MODERN_TO_LEGACY_NAMES.put("swift_sneak", "SWIFT_SNEAK");
        MODERN_TO_LEGACY_NAMES.put("lunge", "LUNGE");
    }

    private XOverEnchantVersion() {}

    public static Enchantment resolveEnchantment(String key) {
        if (key == null) return null;
        String clean = key.trim().toLowerCase();
        if (clean.isEmpty()) return null;

        // 1. Try resolving via NamespacedKey on 1.13+
        try {
            org.bukkit.NamespacedKey nsKey = clean.contains(":")
                    ? org.bukkit.NamespacedKey.fromString(clean)
                    : org.bukkit.NamespacedKey.minecraft(clean);
            if (nsKey != null) {
                Enchantment byKey = Enchantment.getByKey(nsKey);
                if (byKey != null) return byKey;
            }
        } catch (Throwable ignored) {}

        // 2. Try mapped legacy name
        String legacyName = MODERN_TO_LEGACY_NAMES.get(clean);
        if (legacyName != null) {
            Enchantment byName = Enchantment.getByName(legacyName);
            if (byName != null) return byName;
        }

        // 3. Try direct getByName
        Enchantment byDirect = Enchantment.getByName(clean.toUpperCase());
        if (byDirect != null) return byDirect;

        // 4. Iterate all registered enchantments and match either key or name
        for (Enchantment e : Enchantment.values()) {
            if (e == null) continue;
            try {
                Method m = e.getClass().getMethod("getKey");
                Object k = m.invoke(e);
                if (k != null && k.toString().equalsIgnoreCase("minecraft:" + clean)) {
                    return e;
                }
            } catch (Throwable ignored) {}

            if (e.getName().equalsIgnoreCase(clean) || e.getName().equalsIgnoreCase(legacyName)) {
                return e;
            }
        }

        return null;
    }

    public static String getEnchantmentKeyOrName(Enchantment enchantment) {
        if (enchantment == null) return "unknown";
        try {
            Method m = enchantment.getClass().getMethod("getKey");
            Object k = m.invoke(enchantment);
            if (k != null) {
                Method getKeyMethod = k.getClass().getMethod("getKey");
                return getKeyMethod.invoke(k).toString().toLowerCase();
            }
        } catch (Throwable ignored) {}
        return enchantment.getName().toLowerCase();
    }

    public static int getItemDamage(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            return ((Damageable) meta).getDamage();
        }
        return item.getDurability();
    }

    public static void setItemDamage(ItemStack item, int damage) {
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            ((Damageable) meta).setDamage(damage);
            item.setItemMeta(meta);
        } else {
            item.setDurability((short) damage);
        }
    }

    public static boolean isBedrockOrUnbreakable(Block block) {
        if (block == null) return false;
        Material mat = block.getType();
        String name = mat.name();
        if (name.equals("BEDROCK") || name.equals("BARRIER") || name.equals("END_PORTAL_FRAME") || name.equals("COMMAND_BLOCK")) {
            return true;
        }
        try {
            Method m = mat.getClass().getMethod("getHardness");
            float hardness = ((Number) m.invoke(mat)).floatValue();
            return hardness < 0.0f;
        } catch (Throwable ignored) {
            return false;
        }
    }

    public static void setRepairCost(InventoryView view, AnvilInventory anvil, int cost) {
        if (view != null) {
            try {
                Method m = view.getClass().getMethod("setRepairCost", int.class);
                m.invoke(view, cost);
            } catch (Throwable ignored) {}

            try {
                Method m = view.getClass().getMethod("setMaximumRepairCost", int.class);
                m.invoke(view, Math.max(40, cost));
            } catch (Throwable ignored) {}
        }

        if (anvil != null) {
            try {
                Method m = anvil.getClass().getMethod("setRepairCost", int.class);
                m.invoke(anvil, cost);
            } catch (Throwable ignored) {}
        }
    }
}
