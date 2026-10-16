package com.famthys.uniqueepic.secondwave;

import com.famthys.uniqueepic.item.ModItemIds;
import com.famthys.uniqueepic.item.ModItems;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Function;

/**
 * 第二波的物品注册。
 *
 * <p>两件都是第一波装备的"进阶版"，贴图先复用各自的前身（见 assets 里的模型定义），
 * 等专用美术到位再把模型指向新贴图即可。</p>
 */
public final class ModSecondWaveItems {

    private ModSecondWaveItems() {
    }

    /** 噬心大剑·共噬：死亡时免死 + 爆发厄血。 */
    public static final Item HEART_DEVOURER_COMMUNION =
            registerSword("heart_devourer_communion", 8.0F, -3.0F, CommunionItem::new);

    /** 湖中剑·湖光：长按吟唱后释放巨型光剑。 */
    public static final Item LAKE_LIGHT =
            registerSword("lake_light", 7.0F, -2.4F, LakeLightItem::new);

    public static void init() {
    }

    private static Item registerSword(String name, float attackDamage, float attackSpeed,
                                      Function<Item.Properties, Item> factory) {
        ResourceKey<Item> key = ModItemIds.create(name);
        return ModItems.register(key, factory,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .sword(ToolMaterial.NETHERITE, attackDamage, attackSpeed));
    }
}
