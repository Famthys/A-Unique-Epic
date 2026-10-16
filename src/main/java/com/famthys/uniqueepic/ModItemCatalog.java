package com.famthys.uniqueepic;

import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import com.famthys.uniqueepic.hero.ModHeroItems;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * 本模组所有可在创造模式拿取的物品，集中在这一个类里。
 *
 * <p>新增装备时只要往 {@link #all()} 里加一行，创造模式物品栏会自动带上它。</p>
 */
public final class ModItemCatalog {

    private ModItemCatalog() {
    }

    /**
     * 全部物品，顺序即创造栏里的展示顺序。
     *
     * <p>每次调用都重新构建一个小列表——它只在开创造栏和写日志时被调用，不值得为它做缓存，
     * 而缓存反而容易让静态初始化顺序变脆。</p>
     */
    public static List<Item> all() {
        return List.of(
                // ---- 饰品 ----
                ModHeroItems.HEROES_EXPEDITION,
                ModEquipmentItems.MARBAS_FANG,

                // ---- 荣耀王 ----
                ModEquipmentItems.SWORD_IN_STONE,
                ModEquipmentItems.ROUND_TABLE_HELMET,
                ModEquipmentItems.OATH_CHESTPLATE,
                ModEquipmentItems.GLORY_LEGGINGS,
                ModEquipmentItems.EXPEDITION_BOOTS,

                // ---- 厄血 ----
                ModEquipmentItems.HEART_DEVOURER,
                ModEquipmentItems.FEROCIOUS_MASK,
                ModEquipmentItems.MUTATED_CHESTPLATE,
                ModEquipmentItems.RAMPAGE_LEGGINGS,
                ModEquipmentItems.SHATTERED_BOOTS
        );
    }

    public static int count() {
        return all().size();
    }
}
