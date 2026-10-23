package com.famthys.uniqueepic.block;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.item.ModItemIds;
import com.famthys.uniqueepic.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

/**
 * 本模组的方块。
 *
 * <p>目前只有石中剑（封印）——把 GPT 交付的那个体素模型做成可放置的方块。</p>
 */
public final class ModBlocks {

    private ModBlocks() {
    }

    /** 石中剑（封印）。 */
    public static final Block SWORD_IN_STONE_SEALED = register(
            "sword_in_stone_sealed",
            SwordInStoneBlock::new,
            BlockBehaviour.Properties.of()
                    .noOcclusion()                    // 模型不是整块，必须关掉遮挡剔除
                    .strength(3.0F, 6.0F)
                    .sound(SoundType.STONE)
                    .pushReaction(PushReaction.BLOCK));   // 别被活塞推走

    /** 对应的物品（可放置）。 */
    public static final Item SWORD_IN_STONE_SEALED_ITEM = ModItems.register(
            ModItemIds.create("sword_in_stone_sealed"),
            properties -> new BlockItem(SWORD_IN_STONE_SEALED, properties),
            new Item.Properties().rarity(Rarity.RARE));

    public static void init() {
        AUniqueEpic.LOGGER.info("[方块] 已注册：石中剑（封印）");
    }

    private static Block register(String name,
                                  Function<BlockBehaviour.Properties, Block> factory,
                                  BlockBehaviour.Properties properties) {
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, AUniqueEpic.id(name));
        Block block = factory.apply(properties.setId(key));
        return Registry.register(BuiltInRegistries.BLOCK, key, block);
    }
}
