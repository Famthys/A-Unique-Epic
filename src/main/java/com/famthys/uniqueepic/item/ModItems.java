package com.famthys.uniqueepic.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

/**
 * 物品注册工具。
 *
 * <p>从 {@code ModItemIds.java} 里拆出来的：一个文件只能有一个 public 类。</p>
 */
public final class ModItems {

    private ModItems() {
    }

    public static Item register(ResourceKey<Item> itemKey,
                                Function<Item.Properties, Item> itemFactory,
                                Item.Properties settings) {
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);
        return item;
    }
}
