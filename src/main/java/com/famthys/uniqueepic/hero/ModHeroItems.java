package com.famthys.uniqueepic.hero;

import com.famthys.uniqueepic.item.ModItemIds;
import com.famthys.uniqueepic.item.ModItems;
import eu.pb4.trinkets.api.DefaultTrinketSlots;
import eu.pb4.trinkets.api.component.TrinketDataComponents;
import eu.pb4.trinkets.api.component.TrinketEquippable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

/**
 * 勇者之征的物品注册。
 *
 * <p>装备栏位用的是 Trinkets 内置的 <b>chest/necklace（项链）</b>——这件东西本质是一枚悬在胸前的
 * 眼状徽记，放项链栏最贴切。</p>
 *
 * <p>Trinkets 4.x 是数据组件驱动的：物品只要挂上 {@code TrinketDataComponents.EQUIPMENT} 组件，
 * 声明允许的栏位，就能被塞进对应饰品槽，不需要额外注册栏位类型。</p>
 */
public final class ModHeroItems {

    private ModHeroItems() {
    }

    public static final ResourceKey<Item> HEROES_EXPEDITION_KEY =
            ModItemIds.create("heroes_expedition");

    /** 勇者之征：戴上后不受混沌之眸影响，并获得决心 / 不屈 / 重担 / 危机四条效果。 */
    public static final Item HEROES_EXPEDITION = ModItems.register(
            HEROES_EXPEDITION_KEY,
            HeroesExpeditionItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(
                            TrinketDataComponents.EQUIPMENT,
                            TrinketEquippable.DEFAULT
                                    .withSlots(DefaultTrinketSlots.CHEST_NECKLACE)
                                    // 右键直接戴上：省得玩家非得先找到 Trinkets 的饰品界面
                                    .withEquipOnInteract(true)
                                    // 允许在饰品槽之间互换
                                    .withSwappable(true)
                    ));

    public static void init() {
    }
}
