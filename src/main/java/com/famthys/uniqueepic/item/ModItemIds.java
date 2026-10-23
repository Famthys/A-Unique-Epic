package com.famthys.uniqueepic.item;

import com.famthys.uniqueepic.AUniqueEpic;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;


public final class ModItemIds {

    private ModItemIds() {
    }

    public static ResourceKey<Item> create(String name) {
        return ResourceKey.create(Registries.ITEM, AUniqueEpic.id(name));

    }
}
