package com.famthys.uniqueepic.dog;

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
 * 人面犬的实体注册。
 *
 * <p>实体 id 必须是 {@code aunique-epic:human_faced_dog}——GeckoLib 的
 * {@code DefaultedEntityGeoModel} 会按 id 自动推导资源路径：</p>
 * <pre>
 *   assets/aunique-epic/geckolib/models/human_faced_dog.geo.json
 *   assets/aunique-epic/geckolib/animations/human_faced_dog.animation.json
 *   assets/aunique-epic/textures/entity/human_faced_dog.png
 * </pre>
 */
public final class ModDogEntities {

    private ModDogEntities() {
    }

    public static final ResourceKey<EntityType<?>> HUMAN_FACED_DOG_KEY =
            ResourceKey.create(Registries.ENTITY_TYPE, AUniqueEpic.id("human_faced_dog"));

    public static final EntityType<HumanFacedDogEntity> HUMAN_FACED_DOG = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            HUMAN_FACED_DOG_KEY,
            EntityType.Builder.of(HumanFacedDogEntity::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.1F)
                    .clientTrackingRange(10)
                    .build(HUMAN_FACED_DOG_KEY));

    public static final Item HUMAN_FACED_DOG_SPAWN_EGG = ModItems.register(
            ModItemIds.create("human_faced_dog_spawn_egg"),
            SpawnEggItem::new,
            new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
                    .component(DataComponents.ENTITY_DATA,
                            TypedEntityData.of(HUMAN_FACED_DOG, new CompoundTag())));

    public static void init() {
        FabricDefaultAttributeRegistry.register(HUMAN_FACED_DOG, HumanFacedDogEntity.createAttributes());
        AUniqueEpic.LOGGER.info("[人面犬] 已注册实体与刷怪蛋");
    }
}
