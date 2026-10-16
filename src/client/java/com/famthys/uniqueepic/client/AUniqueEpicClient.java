package com.famthys.uniqueepic.client;

import net.fabricmc.api.ClientModInitializer;

public class AUniqueEpicClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// 玛尔巴斯之噬的层数 HUD
		MarbasHud.register();
	}
}