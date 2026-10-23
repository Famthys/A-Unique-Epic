package com.famthys.uniqueepic.client;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.boss.ModBossEntities;
import com.famthys.uniqueepic.client.armor.BloodArmorClient;
import com.famthys.uniqueepic.client.boss.CrimsonKnightModel;
import com.famthys.uniqueepic.client.boss.CrimsonKnightRenderer;
import com.famthys.uniqueepic.dog.ModDogEntities;
import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import com.geckolib.renderer.GeoEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class AUniqueEpicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 玛尔巴斯之噬的层数 HUD
		MarbasHud.register();

		// 厄血套装的立体穿戴模型（自定义 ArmorRenderer）
		// 参数顺序固定为 HEAD / CHEST / LEGS / FEET
		BloodArmorClient.register(
				ModEquipmentItems.FEROCIOUS_MASK,
				ModEquipmentItems.MUTATED_CHESTPLATE,
				ModEquipmentItems.RAMPAGE_LEGGINGS,
				ModEquipmentItems.SHATTERED_BOOTS);

		// 猩红骑：自定义模型层 + 渲染器
		ModelLayerRegistry.registerModelLayer(CrimsonKnightRenderer.LAYER,
				CrimsonKnightModel::createBodyLayer);
		EntityRenderers.register(ModBossEntities.CRIMSON_KNIGHT, CrimsonKnightRenderer::new);

		// 人面犬：GeckoLib 渲染器。模型/动画/贴图路径由实体 id 自动推导，无需显式指定。
		EntityRenderers.register(ModDogEntities.HUMAN_FACED_DOG,
				context -> new GeoEntityRenderer<>(context, ModDogEntities.HUMAN_FACED_DOG));

		AUniqueEpic.LOGGER.info("[猩红骑] 客户端渲染器与模型层已注册");
		AUniqueEpic.LOGGER.info("[人面犬] GeckoLib 渲染器已注册");
	}
}
