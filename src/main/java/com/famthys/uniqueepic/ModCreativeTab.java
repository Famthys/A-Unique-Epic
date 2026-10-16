package com.famthys.uniqueepic;

import com.famthys.uniqueepic.hero.ModHeroItems;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 本模组的创造模式物品栏。
 *
 * <p>刻意自建一栏而不是往原版栏里塞：本模组的物品会越来越多（第一波 11 件，后面还有三波），
 * 混进原版战斗栏会很难找。</p>
 */
public final class ModCreativeTab {

    private ModCreativeTab() {
    }

    public static final ResourceKey<CreativeModeTab> TAB_KEY =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, AUniqueEpic.id("main"));

    public static final CreativeModeTab TAB = FabricCreativeModeTab.builder()
            .title(Component.translatable("itemGroup.aunique-epic.main"))
            .icon(() -> new ItemStack(ModHeroItems.HEROES_EXPEDITION))
            .displayItems((parameters, output) -> {
                for (Item item : ModItemCatalog.all()) {
                    output.accept(item);
                }
            })
            .build();

    /** 必须在所有物品注册完之后调用。 */
    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, TAB_KEY, TAB);
        AUniqueEpic.LOGGER.info("[创造模式物品栏] 已注册，共 {} 件物品", ModItemCatalog.count());
    }
}
