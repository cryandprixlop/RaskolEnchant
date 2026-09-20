package ru.raskol.enchant.enchant;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

/** Пулы зачарований по типу предмета + дополнительные конфликты. */
public final class EnchantPools {

    private EnchantPools() {}

    public static List<Enchantment> poolFor(Material m) {
        String n = m.name();
        List<Enchantment> p = new ArrayList<>();

        if (n.endsWith("_SWORD")) {
            add(p, Enchantment.SHARPNESS, Enchantment.SMITE, Enchantment.BANE_OF_ARTHROPODS,
                    Enchantment.KNOCKBACK, Enchantment.FIRE_ASPECT, Enchantment.LOOTING,
                    Enchantment.SWEEPING_EDGE, Enchantment.UNBREAKING, Enchantment.MENDING);
        } else if (n.equals("BOW")) {
            add(p, Enchantment.POWER, Enchantment.PUNCH, Enchantment.FLAME,
                    Enchantment.INFINITY, Enchantment.UNBREAKING, Enchantment.MENDING);
        } else if (n.equals("CROSSBOW")) {
            add(p, Enchantment.QUICK_CHARGE, Enchantment.MULTISHOT, Enchantment.PIERCING,
                    Enchantment.UNBREAKING, Enchantment.MENDING);
        } else if (n.endsWith("_HELMET")) {
            armor(p);
            add(p, Enchantment.RESPIRATION, Enchantment.AQUA_AFFINITY);
        } else if (n.endsWith("_CHESTPLATE") || n.endsWith("_LEGGINGS")) {
            armor(p);
        } else if (n.endsWith("_BOOTS")) {
            armor(p);
            add(p, Enchantment.FEATHER_FALLING, Enchantment.DEPTH_STRIDER, Enchantment.FROST_WALKER);
        } else if (n.endsWith("_PICKAXE") || n.endsWith("_AXE")
                || n.endsWith("_SHOVEL") || n.endsWith("_HOE")) {
            add(p, Enchantment.EFFICIENCY, Enchantment.SILK_TOUCH, Enchantment.FORTUNE,
                    Enchantment.UNBREAKING, Enchantment.MENDING);
        }
        return p;
    }

    private static void armor(List<Enchantment> p) {
        add(p, Enchantment.PROTECTION, Enchantment.FIRE_PROTECTION,
                Enchantment.BLAST_PROTECTION, Enchantment.PROJECTILE_PROTECTION,
                Enchantment.THORNS, Enchantment.UNBREAKING, Enchantment.MENDING);
    }

    private static void add(List<Enchantment> list, Enchantment... es) {
        for (Enchantment e : es) {
            if (e != null) list.add(e);
        }
    }

    /** Доп. конфликты сверх vanilla conflictsWith(). */
    public static boolean extraConflict(Enchantment a, Enchantment b) {
        return (is(a, Enchantment.INFINITY) && is(b, Enchantment.MENDING))
                || (is(a, Enchantment.MENDING) && is(b, Enchantment.INFINITY));
    }

    private static boolean is(Enchantment e, Enchantment other) {
        return e != null && other != null && e.getKey().equals(other.getKey());
    }
}
