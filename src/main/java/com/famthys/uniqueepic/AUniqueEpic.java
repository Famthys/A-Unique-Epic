package com.famthys.uniqueepic;

import com.famthys.uniqueepic.chaoseye.ChaosEye;
import com.famthys.uniqueepic.equipment.EquipmentEntry;
import com.famthys.uniqueepic.hero.HeroesExpedition;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.Identifier;

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

		// 创造模式物品栏：必须在所有物品注册完之后
		ModCreativeTab.init();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
