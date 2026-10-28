package dev.ntgame.overenchant.config;

import org.bukkit.enchantments.Enchantment;

/**
 * Enchantment rule with max cap and optional effect.
 */
public class EnchantRule {

    private final Enchantment enchantment;
    private final int cap;
    private final MaxEffect effect;

    public EnchantRule(Enchantment enchantment, int cap, MaxEffect effect) {
        this.enchantment = enchantment;
        this.cap = cap;
        this.effect = effect;
    }

    public Enchantment enchantment() {
        return enchantment;
    }

    public int cap() {
        return cap;
    }

    public MaxEffect effect() {
        return effect;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getCap() {
        return cap;
    }

    public MaxEffect getEffect() {
        return effect;
    }
}
