package ru.raskol.enchant;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ru.raskol.enchant.command.RenchantCommand;
import ru.raskol.enchant.enchant.EnchantLogic;
import ru.raskol.enchant.items.EnchantItems;
import ru.raskol.enchant.listener.AltarListener;

public final class RaskolEnchant extends JavaPlugin {

    private EnchantItems enchantItems;
    private EnchantLogic enchantLogic;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        enchantItems = new EnchantItems(this);
        enchantLogic = new EnchantLogic(enchantItems);

        PluginCommand cmd = getCommand("renchant");
        if (cmd != null) {
            RenchantCommand executor = new RenchantCommand(this, enchantItems);
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(
                new AltarListener(this, enchantItems, enchantLogic), this);

        getLogger().info("RaskolEnchant v" + getDescription().getVersion()
                + " включён. Книга=" + getConfig().getDouble("materials.book.chance", 30.0)
                + "%, Осколок=" + getConfig().getDouble("materials.shard.chance", 1.0)
                + "%, Чертёж=" + getConfig().getDouble("materials.blueprint.chance", 100.0) + "%");
    }

    @Override
    public void onDisable() {
        getLogger().info("RaskolEnchant выключен.");
    }

    public EnchantItems getEnchantItems() { return enchantItems; }
    public EnchantLogic getEnchantLogic() { return enchantLogic; }
}
