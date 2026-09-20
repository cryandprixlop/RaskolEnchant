package ru.raskol.enchant.enchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import ru.raskol.enchant.items.EnchantItems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Рулон шанса и применение зачарования к предмету. */
public final class EnchantLogic {

    public enum Outcome { SUCCESS, FAIL, NOT_ENCHANTABLE, FULL }

    public static final class Attempt {
        public final Outcome outcome;
        public final String message;
        public Attempt(Outcome outcome, String message) {
            this.outcome = outcome;
            this.message = message;
        }
    }

    private final EnchantItems items;
    private final Random random = new Random();

    public EnchantLogic(EnchantItems items) {
        this.items = items;
    }

    public Attempt attempt(ItemStack target, EnchantItems.Type type) {
        if (target == null) {
            return new Attempt(Outcome.NOT_ENCHANTABLE, "§cПоложи предмет для зачарования.");
        }
        List<Enchantment> pool = EnchantPools.poolFor(target.getType());
        if (pool.isEmpty()) {
            return new Attempt(Outcome.NOT_ENCHANTABLE, "§cЭтот предмет нельзя зачаровать на алтаре.");
        }

        double chance = items.chanceOf(type);
        boolean roll = random.nextDouble() * 100.0 < chance;
        if (!roll) {
            return new Attempt(Outcome.FAIL,
                    "§cНеудача (шанс был " + (int) chance + "%): материал истлел, предмет цел.");
        }

        Map<Enchantment, Integer> current = new HashMap<>(target.getEnchantments());
        List<Enchantment> candidates = new ArrayList<>();
        for (Enchantment e : pool) {
            Integer lvl = current.get(e);
            if (lvl == null || lvl < e.getMaxLevel()) {
                candidates.add(e);
            }
        }
        candidates.removeIf(e -> {
            for (Enchantment have : current.keySet()) {
                if (have.getKey().equals(e.getKey())) continue;
                if (have.conflictsWith(e) || EnchantPools.extraConflict(have, e)) return true;
            }
            return false;
        });
        if (candidates.isEmpty()) {
            return new Attempt(Outcome.FULL,
                    "§cПредмет исчерпан: нет совместимых зачарований для улучшения.");
        }

        Enchantment pick = candidates.get(random.nextInt(candidates.size()));
        int newLevel = current.getOrDefault(pick, 0) + 1;
        target.addUnsafeEnchantment(pick, newLevel);
        return new Attempt(Outcome.SUCCESS,
                "§aУспех! §f" + pretty(pick) + " " + roman(newLevel)
                        + " §a(шанс был " + (int) chance + "%).");
    }

    public static String pretty(Enchantment e) {
        String key = e.getKey().getKey().replace('_', ' ');
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

    private static String roman(int n) {
        switch (n) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(n);
        }
    }
}
