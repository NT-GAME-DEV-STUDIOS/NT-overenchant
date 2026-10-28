package dev.ntgame.overenchant.listeners;

import dev.ntgame.overenchant.OverEnchantPlugin;
import dev.ntgame.overenchant.config.OverEnchantConfig;
import dev.ntgame.overenchant.util.XOverEnchantVersion;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnvilOverenchantListener implements Listener {

    private final OverEnchantPlugin plugin;

    public AnvilOverenchantListener(OverEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        OverEnchantConfig config = plugin.getOverEnchantConfig();
        AnvilInventory inv = event.getInventory();

        ItemStack base = inv.getItem(0);
        ItemStack addition = inv.getItem(1);
        if (base == null || addition == null) return;
        if (addition.getType() != base.getType() && !isEnchantedBook(addition)) return;

        Map<Enchantment, Integer> baseEnch = enchantsOf(base);
        Map<Enchantment, Integer> addEnch = enchantsOf(addition);

        boolean baseHasOverlevels = false;
        for (Map.Entry<Enchantment, Integer> entry : baseEnch.entrySet()) {
            if (entry.getValue() > entry.getKey().getMaxLevel()) {
                baseHasOverlevels = true;
                break;
            }
        }

        if (addEnch.isEmpty() && !baseHasOverlevels) return;

        Map<Enchantment, Integer> merged = new LinkedHashMap<Enchantment, Integer>(baseEnch);
        boolean anyOverVanillaCap = baseHasOverlevels;
        int overlevels = 0;

        for (Map.Entry<Enchantment, Integer> entry : addEnch.entrySet()) {
            Enchantment ench = entry.getKey();
            int addLevel = entry.getValue();
            Integer existingBase = baseEnch.get(ench);
            int baseLevel = existingBase != null ? existingBase.intValue() : 0;

            // If applying to an item (not a book), respect target item applicability
            if (!isEnchantedBook(base) && baseLevel <= 0 && !ench.canEnchantItem(base)) {
                continue;
            }

            // If applying to an item (not a book), respect incompatibility conflicts
            if (!isEnchantedBook(base)) {
                boolean conflicts = false;
                for (Enchantment existing : merged.keySet()) {
                    if (!existing.equals(ench) && existing.conflictsWith(ench)) {
                        conflicts = true;
                        break;
                    }
                }
                if (conflicts) continue;
            }

            int computed = (baseLevel > 0 && addLevel > 0)
                    ? (baseLevel == addLevel ? baseLevel + 1 : Math.max(baseLevel, addLevel))
                    : Math.max(baseLevel, addLevel);
            computed = Math.min(computed, config.capFor(ench));

            if (computed > ench.getMaxLevel()) {
                anyOverVanillaCap = true;
                overlevels += computed - Math.max(ench.getMaxLevel(), baseLevel);
            }
            merged.put(ench, computed);
        }

        // Within vanilla's own cap and without existing overlevels, vanilla produces the right result.
        if (!anyOverVanillaCap) return;

        ItemStack result = base.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) return;
        applyEnchants(meta, merged);
        applyRename(event, meta);

        if (meta instanceof Repairable) {
            ((Repairable) meta).setRepairCost(0);
        }
        result.setItemMeta(meta);

        repairDurability(base, addition, result);

        int cost = Math.max(1, overlevels * config.xpCostPerOverlevel());
        XOverEnchantVersion.setRepairCost(event.getView(), inv, cost);

        event.setResult(result);
    }

    private boolean isEnchantedBook(ItemStack item) {
        if (item == null) return false;
        Material m = item.getType();
        return m == Material.BOOK || m.name().equals("ENCHANTED_BOOK") || (item.hasItemMeta() && item.getItemMeta() instanceof EnchantmentStorageMeta);
    }

    private Map<Enchantment, Integer> enchantsOf(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Collections.emptyMap();
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof EnchantmentStorageMeta) {
            return new LinkedHashMap<Enchantment, Integer>(((EnchantmentStorageMeta) meta).getStoredEnchants());
        }
        return new LinkedHashMap<Enchantment, Integer>(meta.getEnchants());
    }

    private void applyEnchants(ItemMeta meta, Map<Enchantment, Integer> merged) {
        if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            for (Enchantment e : new ArrayList<Enchantment>(storageMeta.getStoredEnchants().keySet())) {
                storageMeta.removeStoredEnchant(e);
            }
            for (Map.Entry<Enchantment, Integer> entry : merged.entrySet()) {
                if (entry.getValue() > 0) {
                    storageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
        } else {
            for (Enchantment e : new ArrayList<Enchantment>(meta.getEnchants().keySet())) {
                meta.removeEnchant(e);
            }
            for (Map.Entry<Enchantment, Integer> entry : merged.entrySet()) {
                if (entry.getValue() > 0) {
                    meta.addEnchant(entry.getKey(), entry.getValue(), true);
                }
            }
        }
    }

    private void applyRename(PrepareAnvilEvent event, ItemMeta meta) {
        String rename = null;
        try {
            Method m = event.getView().getClass().getMethod("getRenameText");
            rename = (String) m.invoke(event.getView());
        } catch (Throwable ignored) {
            try {
                Method m = event.getInventory().getClass().getMethod("getRenameText");
                rename = (String) m.invoke(event.getInventory());
            } catch (Throwable ignored2) {}
        }

        if (rename != null && !rename.trim().isEmpty()) {
            meta.setDisplayName(rename);
        }
    }

    /** Mirrors vanilla's anvil repair math (12% bonus durability) when both items are the same tool type. */
    private void repairDurability(ItemStack base, ItemStack addition, ItemStack result) {
        if (addition.getType() != base.getType()) return;
        short maxDurability = base.getType().getMaxDurability();
        if (maxDurability <= 0) return;

        int baseDmg = XOverEnchantVersion.getItemDamage(base);
        int additionDmg = XOverEnchantVersion.getItemDamage(addition);

        int repairAmount = (maxDurability - baseDmg)
                + (maxDurability - additionDmg)
                + (int) (maxDurability * 0.12);
        int newDmg = Math.max(0, maxDurability - repairAmount);
        XOverEnchantVersion.setItemDamage(result, newDmg);
    }
}
