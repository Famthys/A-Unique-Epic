package com.famthys.uniqueepic;

import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import com.famthys.uniqueepic.secondwave.ModSecondWaveItems;
import net.minecraft.world.item.Item;

import java.util.List;

/**
 * 本模组所有"武器"。
 *
 * <p>这个列表要同时对应 {@code data/aunique-epic/tags/item/weapons.json}：
 * 加武器时两边都要加。启动时的标签自检会比对这两处，漏了会当场报出来。</p>
 */
public final class ModWeapons {

    private ModWeapons() {
    }

    public static final List<Item> ALL = List.of(
            ModEquipmentItems.HEART_DEVOURER,
            ModEquipmentItems.SWORD_IN_STONE,
            ModSecondWaveItems.HEART_DEVOURER_COMMUNION,
            ModSecondWaveItems.LAKE_LIGHT
    );
}
