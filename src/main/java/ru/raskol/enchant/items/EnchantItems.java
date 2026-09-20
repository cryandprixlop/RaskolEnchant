package ru.raskol.enchant.items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import ru.raskol.enchant.RaskolEnchant;

import java.util.ArrayList;
import java.util.List;

/**
 * Фабрика и распознаватель материалов зачарования.
 * Книга и чертёж — кастомные предметы с PDC-тегом, осколок — ванильный PRISMARINE_SHARD.
 */
public final class EnchantItems {

    public enum Type { BOOK, SHARD, BLUEPRINT, NONE }

    private final RaskolEnchant plugin;
    private final NamespacedKey key;

    public EnchantItems(RaskolEnchant plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "enchant_material");
    }

    /* ================= СОЗДАНИЕ ================= */

    public ItemStack createBook(int amount) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(plugin.getConfig().getString(
                    "materials.book.name", "&5Книга зачарования")));
            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("materials.book.lore")) {
                lore.add(color(line));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "book");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createBlueprint(int amount) {
        ItemStack item = new ItemStack(Material.PAPER, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(color(plugin.getConfig().getString(
                    "materials.blueprint.name", "&6Чертёж зачарования")));
            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("materials.blueprint.lore")) {
                lore.add(color(line));
            }
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "blueprint");
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createShard(int amount) {
        return new ItemStack(Material.PRISMARINE_SHARD, amount);
    }

    /* ================= РАСПОЗНАВАНИЕ ================= */

    public Type detect(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return Type.NONE;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String tag = pdc.get(key, PersistentDataType.STRING);
            if ("book".equals(tag)) return Type.BOOK;
            if ("blueprint".equals(tag)) return Type.BLUEPRINT;
        }
        if (item.getType() == Material.PRISMARINE_SHARD) return Type.SHARD;
        return Type.NONE;
    }

    /** Шанс успеха материала в процентах (из конфига). */
    public double chanceOf(Type type) {
        switch (type) {
            case BOOK: return plugin.getConfig().getDouble("materials.book.chance", 30.0);
            case SHARD: return plugin.getConfig().getDouble("materials.shard.chance", 1.0);
            case BLUEPRINT: return plugin.getConfig().getDouble("materials.blueprint.chance", 100.0);
            default: return 0.0;
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
