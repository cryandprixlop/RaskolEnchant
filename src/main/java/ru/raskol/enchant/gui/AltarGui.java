package ru.raskol.enchant.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/** GUI алтаря: слот 11 предмет, слот 15 материал, слот 22 кнопка. */
public final class AltarGui implements InventoryHolder {

    public static final int SLOT_TARGET = 11;
    public static final int SLOT_MATERIAL = 15;
    public static final int SLOT_BUTTON = 22;

    private final Inventory inventory;

    public AltarGui() {
        this.inventory = Bukkit.createInventory(this, 27, "§0Алтарь зачарования");
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta pm = pane.getItemMeta();
        if (pm != null) {
            pm.setDisplayName(" ");
            pane.setItemMeta(pm);
        }
        for (int i = 0; i < 27; i++) inventory.setItem(i, pane);
        inventory.setItem(SLOT_TARGET, null);
        inventory.setItem(SLOT_MATERIAL, null);
        inventory.setItem(SLOT_BUTTON, button());
    }

    private ItemStack button() {
        ItemStack b = new ItemStack(Material.NETHER_STAR);
        ItemMeta m = b.getItemMeta();
        if (m != null) {
            m.setDisplayName("§d⚡ Зачаровать");
            List<String> lore = new ArrayList<>();
            lore.add("§7Книга §e30% §7| Осколок §e1% §7| Чертёж §e100%");
            lore.add("§7Неудача сжигает материал, предмет цел");
            m.setLore(lore);
            b.setItemMeta(m);
        }
        return b;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
