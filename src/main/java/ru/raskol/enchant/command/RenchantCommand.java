package ru.raskol.enchant.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.raskol.enchant.RaskolEnchant;
import ru.raskol.enchant.items.EnchantItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public final class RenchantCommand implements CommandExecutor, TabCompleter {

    private final RaskolEnchant plugin;
    private final EnchantItems items;

    public RenchantCommand(RaskolEnchant plugin, EnchantItems items) {
        this.plugin = plugin;
        this.items = items;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("give")) {
            handleGive(sender, args);
        } else {
            sendHelp(sender);
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== RaskolEnchant ===");
        sender.sendMessage("§e/renchant give <игрок> <book|shard|blueprint> [кол-во] §7— выдать материал");
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("renchant.admin")) {
            sender.sendMessage("§cНет прав.");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage("§cИспользуй: /renchant give <игрок> <book|shard|blueprint> [кол-во]");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cИгрок не найден: " + args[1]);
            return;
        }

        String matName = args[2].toLowerCase();
        int amount = 1;
        if (args.length >= 4) {
            try {
                amount = Integer.parseInt(args[3]);
                if (amount <= 0) amount = 1;
            } catch (NumberFormatException e) {
                sender.sendMessage("§cНекорректное количество: " + args[3]);
                return;
            }
        }

        ItemStack stack;
        String pretty;
        switch (matName) {
            case "book" -> { stack = items.createBook(amount); pretty = "Книга зачарования"; }
            case "shard" -> { stack = items.createShard(amount); pretty = "Осколок призмарина"; }
            case "blueprint" -> { stack = items.createBlueprint(amount); pretty = "Чертёж зачарования"; }
            default -> {
                sender.sendMessage("§cНеизвестный материал: " + matName
                        + " §7(book | shard | blueprint)");
                return;
            }
        }

        HashMap<Integer, ItemStack> overflow = target.getInventory().addItem(stack);
        for (ItemStack drop : overflow.values()) {
            target.getWorld().dropItem(target.getLocation(), drop);
        }

        target.sendMessage("§aПолучено: §e" + pretty + " §7x" + amount);
        if (!sender.equals(target)) {
            sender.sendMessage("§aВыдано игроку §e" + target.getName() + "§a: " + pretty + " x" + amount);
        }
        plugin.getLogger().info("[Enchant] give: " + sender.getName() + " -> "
                + target.getName() + " " + matName + " x" + amount);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("give", "help"), args[0]);
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return filter(Arrays.asList("book", "shard", "blueprint"), args[2]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("give") && sender.hasPermission("renchant.admin")) {
            List<String> names = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) names.add(p.getName());
            return filter(names, args[1]);
        }
        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String prefix) {
        String lower = prefix.toLowerCase();
        List<String> out = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(lower)) out.add(s);
        }
        return out;
    }
}
