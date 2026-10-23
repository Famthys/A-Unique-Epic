package com.famthys.uniqueepic;

import com.famthys.uniqueepic.block.ModBlocks;
import com.famthys.uniqueepic.boss.ModBossEntities;
import com.famthys.uniqueepic.chaoseye.ChaosEye;
import com.famthys.uniqueepic.dog.ModDogEntities;
import com.famthys.uniqueepic.equipment.EquipmentEntry;
import com.famthys.uniqueepic.hero.HeroesExpedition;
import com.famthys.uniqueepic.secondwave.SecondWaveEntry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AUniqueEpic implements ModInitializer {
	public static final String MOD_ID = "aunique-epic";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		// 混沌之眸：P0 + P1（数据附着 + 五种情绪）
		ChaosEye.init();

		// 勇者之征：饰品栏 + 决心/不屈/重担/危机
		HeroesExpedition.init();

		// 史诗装备第一波：噬心大剑 / 石中剑 / 圆桌骑士套装 / 厄血套装 / 玛尔巴斯之噬
		EquipmentEntry.init();

		// 第二波：噬心大剑·共噬 / 湖中剑·湖光
		SecondWaveEntry.init();

		// 方块：石中剑（封印）
		ModBlocks.init();

		// Boss：猩红骑
		ModBossEntities.init();

		// 怪物：人面犬（GeckoLib 骨骼动画）
		ModDogEntities.init();

		// 创造模式物品栏：必须在所有物品注册完之后
		ModCreativeTab.init();

		// 数据包加载完成后自检武器标签。
		// 这个自检是有来历的：原版 minecraft:swords 是数据包标签，只列了原版剑，
		// 自制武器光靠 .sword(...) 注册不会被算进去 —— 结果"决心"永远不触发。
		// 与其相信标签写对了，不如让服务端自己报数。
		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			int hit = 0;
			for (var item : ModWeapons.ALL) {
				if (new ItemStack(item).is(ModTags.WEAPONS)) {
					hit++;
				}
			}
			LOGGER.info("[标签] aunique-epic:weapons 命中 {}/{} 件武器",
					hit, ModWeapons.ALL.size());

			// 猩红骑属性自检：从原版的默认属性表里**读回来**，
			// 而不是复述我写死的常量——这样才算证明注册真的生效了。
			var supplier = DefaultAttributes.getSupplier(ModBossEntities.CRIMSON_KNIGHT);
			if (supplier == null) {
				LOGGER.error("[猩红骑] 属性没有注册成功！");
			} else {
				LOGGER.info("[猩红骑] 游戏内实际数值：生命 {} / 伤害 {} / 护甲 {} / 韧性 {}",
						supplier.getValue(Attributes.MAX_HEALTH),
						supplier.getValue(Attributes.ATTACK_DAMAGE),
						supplier.getValue(Attributes.ARMOR),
						supplier.getValue(Attributes.ARMOR_TOUGHNESS));
			}
			// 结构模板自检：用 Minecraft 自己的解析器读一遍手写的 NBT。
			// 结构的 NBT 是懒加载的，只有真正生成到那个区块才会读，
			// 所以这里主动读一次——否则文件写坏了要等玩家跑到附近才会暴露。
			server.getStructureManager().get(id("deadwater_ruin")).ifPresentOrElse(
					tpl -> {
						var s = tpl.getSize();
						LOGGER.info("[结构] deadwater_ruin 载入成功：尺寸 {}x{}x{}", s.getX(), s.getY(), s.getZ());
					},
					() -> LOGGER.error("[结构] deadwater_ruin 载入失败：data/aunique-epic/structure/deadwater_ruin.nbt"));
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
