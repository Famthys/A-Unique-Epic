package com.famthys.uniqueepic.item;

import com.famthys.uniqueepic.AUniqueEpic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

/**
 * 物品 ID 工具。
 *
 * <p>原来这个文件里塞了两个 public 类（{@code ModItemIds} 和 {@code ModItems}），
 * Java 不允许一个文件出现两个 public 类，所以 {@code ModItems} 已经被拆到单独的文件里。</p>
 */
public final class ModItemIds {

    private ModItemIds() {
    }

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, AUniqueEpic.id(name));
    }
}
