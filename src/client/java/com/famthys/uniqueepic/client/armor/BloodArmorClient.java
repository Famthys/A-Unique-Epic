package com.famthys.uniqueepic.client.armor;

import com.famthys.uniqueepic.AUniqueEpic;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.world.item.Item;

/** 在现有 ClientModInitializer#onInitializeClient 中调用一次，不新建第二套物品。 */
public final class BloodArmorClient {
    private BloodArmorClient() {}

    public static void register(Item ferociousMask, Item mutatedChestplate,
            Item rampageLeggings, Item shatteredBoots) {
        // 显式选 Factory 重载；资源重载时重新构建模型。
        ArmorRenderer.register((ArmorRenderer.Factory) context -> new BloodArmorRenderer(),
                ferociousMask, mutatedChestplate, rampageLeggings, shatteredBoots);

        // 自检日志：日志里能直接确认注册成功，不用靠肉眼猜
        AUniqueEpic.LOGGER.info("[厄血铠甲] 已注册自定义穿戴模型（头盔/胸甲/护腿/靴子）");
    }
}
