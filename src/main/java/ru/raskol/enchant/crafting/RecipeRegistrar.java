package ru.raskol.enchant.crafting;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import ru.raskol.enchant.RaskolEnchant;
import ru.raskol.enchant.items.EnchantItems;

/**
 * Крафт книги зачарования на верстаке (альтернатива дропу, которого пока нет).
 * Форма:
 *   LAPIS  BOOK   LAPIS
 *   BOOK   FEATHER BOOK
 *   LAPIS  BOOK   LAPIS
 */
public final class RecipeRegistrar {

    private RecipeRegistrar() {}

    public static void register(RaskolEnchant plugin, EnchantItems items) {
        if (!plugin.getConfig().getBoolean("crafting.book-enabled", true)) return;

        NamespacedKey key = new NamespacedKey(plugin, "enchant_book_craft");
        if (plugin.getServer().getRecipe(key) != null) return;

        ItemStack result = items.createBook(1);
        ShapedRecipe recipe = new ShapedRecipe(key, result);
        recipe.shape("LBL", "BFB", "LBL");
        recipe.setIngredient('L', Material.LAPIS_LAZULI);
        recipe.setIngredient('B', Material.BOOK);
        recipe.setIngredient('F', Material.FEATHER);

        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("[Enchant] рецепт зарегистрирован: enchant_book_craft");
    }
}
