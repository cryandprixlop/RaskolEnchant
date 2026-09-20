package ru.raskol.enchant.listener;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ru.raskol.enchant.RaskolEnchant;
import ru.raskol.enchant.enchant.EnchantLogic;
import ru.raskol.enchant.gui.AltarGui;
import ru.raskol.enchant.items.EnchantItems;

public final class AltarListener implements Listener {

    private final RaskolEnchant plugin;
    private final EnchantItems items;
    private final EnchantLogic logic;

    public AltarListener(RaskolEnchant plugin, EnchantItems items, EnchantLogic logic) {
        this.plugin = plugin;
        this.items = items;
        this.logic = logic;
    }

    /* ПКМ по столу зачарований -> наш алтарь вместо ванильного GUI */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onTableClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block b = e.getClickedBlock();
        if (b == null || b.getType() != Material.ENCHANTING_TABLE) return;
        e.setCancelled(true);
        e.getPlayer().openInventory(new AltarGui().getInventory());
    }

    @EventHandler
    public void onAltarClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof AltarGui)) return;
        Inventory top = e.getInventory();
        Inventory clicked = e.getClickedInventory();

        if (clicked == top) {
            int slot = e.getSlot();
            if (slot == AltarGui.SLOT_BUTTON) {
                e.setCancelled(true);
                if (e.getWhoClicked() instanceof Player p) attempt(p, top);
                return;
            }
            if (slot == AltarGui.SLOT_TARGET || slot == AltarGui.SLOT_MATERIAL) {
                return; // разрешаем класть и забирать
            }
            e.setCancelled(true);
            return;
        }
        // shift-клик из инвентаря игрока мог бы закинуть предмет в филлер — запрещаем
        if (e.isShiftClick()) e.setCancelled(true);
    }

    private void attempt(Player p, Inventory top) {
        ItemStack target = top.getItem(AltarGui.SLOT_TARGET);
        ItemStack matStack = top.getItem(AltarGui.SLOT_MATERIAL);

        if (target == null || target.getType() == Material.AIR) {
            p.sendMessage("§cПоложи предмет для зачарования в левый слот.");
            return;
        }
        EnchantItems.Type type = items.detect(matStack);
        if (type == EnchantItems.Type.NONE) {
            p.sendMessage("§cПоложи материал: книгу, осколок призмарина или чертёж.");
            return;
        }

        EnchantLogic.Attempt a = logic.attempt(target, type);
        if (a.outcome == EnchantLogic.Outcome.NOT_ENCHANTABLE
                || a.outcome == EnchantLogic.Outcome.FULL) {
            p.sendMessage(a.message);
            return;
        }

        // сжигаем 1 материал (и при успехе, и при неудаче)
        int left = matStack.getAmount() - 1;
        if (left <= 0) top.setItem(AltarGui.SLOT_MATERIAL, null);
        else matStack.setAmount(left);

        if (a.outcome == EnchantLogic.Outcome.SUCCESS) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
            p.spawnParticle(Particle.ENCHANT, p.getLocation().add(0, 1, 0), 40, 0.5, 0.5, 0.5);
        } else {
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 0.8f);
            p.spawnParticle(Particle.LARGE_SMOKE, p.getLocation().add(0, 1, 0), 15, 0.3, 0.3, 0.3);
        }
        p.sendMessage(a.message);
    }
}
