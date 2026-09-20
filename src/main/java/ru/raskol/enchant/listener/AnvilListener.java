package ru.raskol.enchant.listener;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import ru.raskol.enchant.RaskolEnchant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Блокирует применение ванильных зачарованных книг в наковальне:
 * иначе ваниль обходит нашу систему алтаря.
 * Ремонт и переименование остаются рабочими.
 */
public final class AnvilListener implements Listener {

    private final RaskolEnchant plugin;
    /** Анти-спам: последнее предупреждение игроку (мс). */
    private final Map<UUID, Long> lastWarn = new HashMap<>();

    public AnvilListener(RaskolEnchant plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack second = inv.getItem(1);
        if (!isEnchantedBook(second)) return;

        ItemStack result = event.getResult();
        if (result == null || result.getType() == Material.AIR) return;

        // Глушим результат: книга + предмет = невозможно
        event.setResult(null);

        for (org.bukkit.entity.HumanEntity viewer : inv.getViewers()) {
            if (!(viewer instanceof Player p)) continue;
            long now = System.currentTimeMillis();
            Long prev = lastWarn.get(p.getUniqueId());
            if (prev != null && now - prev < 3000) continue;
            lastWarn.put(p.getUniqueId(), now);
            p.sendMessage("§cЗачарованные книги в наковальне запрещены. §7Используй §eАлтарь зачарования §7(ПКМ по столу зачарований).");
        }
    }

    private boolean isEnchantedBook(ItemStack stack) {
        if (stack == null || stack.getType() != Material.ENCHANTED_BOOK) return false;
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof EnchantmentStorageMeta storage)) return false;
        Map<Enchantment, Integer> enchants = storage.getStoredEnchants();
        return enchants != null && !enchants.isEmpty();
    }
}
