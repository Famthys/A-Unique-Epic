package com.famthys.uniqueepic;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * 本模组自己维护的物品标签。
 */
public final class ModTags {

    private ModTags() {
    }

    /**
     * 本模组认定的"武器"。
     *
     * <p>为什么要自己建一个：原版 {@code minecraft:swords} 是<b>数据包标签</b>，里面只列了 7 把
     * 原版剑——用 {@code Item.Properties.sword(...)} 注册只会设置属性和耐久，
     * <b>不会把物品加进标签</b>。所以只判断原版标签的话，自制武器会被当成"不是武器"。</p>
     *
     * <p>对应文件：{@code data/aunique-epic/tags/item/weapons.json}。以后新增武器只要往那里加一行。</p>
     */
    public static final TagKey<Item> WEAPONS =
            TagKey.create(Registries.ITEM, AUniqueEpic.id("weapons"));
}
