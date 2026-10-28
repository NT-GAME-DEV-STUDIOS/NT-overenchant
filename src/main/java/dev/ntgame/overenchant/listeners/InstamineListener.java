package dev.ntgame.overenchant.listeners;

import dev.ntgame.overenchant.OverEnchantPlugin;
import dev.ntgame.overenchant.config.EnchantRule;
import dev.ntgame.overenchant.config.MaxEffect;
import dev.ntgame.overenchant.config.OverEnchantConfig;
import dev.ntgame.overenchant.util.XOverEnchantVersion;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

/** Instantly breaks any block, regardless of hardness, once a tool's enchant reaches an INSTAMINE-tagged cap. */
public class InstamineListener implements Listener {

    private final OverEnchantPlugin plugin;

    public InstamineListener(OverEnchantPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (event.getInstaBreak()) return;
        OverEnchantConfig config = plugin.getOverEnchantConfig();

        Player player = event.getPlayer();
        if (player == null || !player.hasPermission("overenchant.use")) return;
        if (XOverEnchantVersion.isBedrockOrUnbreakable(event.getBlock())) return;

        ItemStack tool = event.getItemInHand();
        if (tool == null || !tool.hasItemMeta()) return;
        ItemMeta meta = tool.getItemMeta();
        if (meta == null || !meta.hasEnchants()) return;

        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            EnchantRule rule = config.ruleFor(entry.getKey());
            if (rule != null && rule.effect() == MaxEffect.INSTAMINE && entry.getValue() >= rule.cap()) {
                event.setInstaBreak(true);
                return;
            }
        }
    }
}
