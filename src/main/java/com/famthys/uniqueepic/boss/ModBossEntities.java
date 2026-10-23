package com.famthys.uniqueepic.boss;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.item.ModItemIds;
import com.famthys.uniqueepic.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.TypedEntityData;

/**
 * 猩红骑的实体注册。
 *
 * <p>规格：生命 520 / 单次伤害 50 / 护甲 10 / 护甲韧性 6。</p>
 */
public final class ModBossEntities {

    private ModBossEntities() {
    }

    public static final ResourceKey<EntityType<?>> CRIMSON_KNIGHT_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, AUniqueEpic.id("crimson_knight"));

    public static final EntityType<CrimsonKnight> CRIMSON_KNIGHT = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            CRIMSON_KNIGHT_KEY,
            EntityType.Builder.of(CrimsonKnight::new, MobCategory.MONSTER)
                    .sized(1.4F, 3.2F)          // 高大：给模型留出体积
                    .clientTrackingRange(12)
                    .build(CRIMSON_KNIGHT_KEY));

    /**
     * 刷怪蛋。
     *
     * <p>26.2 的 {@code SpawnEggItem} 构造器不再接收实体类型，改成挂
     * {@link DataComponents#ENTITY_DATA} 组件。</p>
     */
    public static final Item CRIMSON_KNIGHT_SPAWN_EGG = ModItems.register(
            ModItemIds.create("crimson_knight_spawn_egg"),
            SpawnEggItem::new,
            new Item.Properties()
                    .rarity(Rarity.EPIC)
                    .component(DataComponents.ENTITY_DATA,
                            TypedEntityData.of(CRIMSON_KNIGHT, new CompoundTag())));

    public static void init() {
        FabricDefaultAttributeRegistry.register(CRIMSON_KNIGHT, CrimsonKnight.createAttributes());
        AUniqueEpic.LOGGER.info("[猩红骑] 已注册：生命 {} / 伤害 {} / 护甲 {} / 韧性 {}",
                CrimsonKnight.MAX_HEALTH, CrimsonKnight.ATTACK_DAMAGE,
                CrimsonKnight.ARMOR, CrimsonKnight.ARMOR_TOUGHNESS);
    }
}
