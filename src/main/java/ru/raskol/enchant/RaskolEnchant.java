package ru.raskol.enchant;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.raskol.enchant.command.RenchantCommand;
import ru.raskol.enchant.items.EnchantItems;

public final class RaskolEnchant extends JavaPlugin {

    private EnchantItems enchantItems;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        enchantItems = new EnchantItems(this);

        PluginCommand cmd = getCommand("renchant");
        if (cmd != null) {
            RenchantCommand executor = new RenchantCommand(this, enchantItems);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getLogger().info("RaskolEnchant v" + getDescription().getVersion()
                + " включён. Книга=" + getConfig().getDouble("materials.book.chance", 30.0)
                + "%, Осколок=" + getConfig().getDouble("materials.shard.chance", 1.0)
                + "%, Чертёж=" + getConfig().getDouble("materials.blueprint.chance", 100.0) + "%");
    }

    @Override
    public void onDisable() {
        getLogger().info("RaskolEnchant выключен.");
    }

    public EnchantItems getEnchantItems() {
        return enchantItems;
    }
}
