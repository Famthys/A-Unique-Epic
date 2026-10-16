package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;

/**
 * 本模组自定义的物品数据组件。
 */
public final class ModComponents {

    private ModComponents() {
    }

    /**
     * 玛尔巴斯之噬的叠加层数。
     *
     * <p>刻意存在<b>物品的数据组件</b>上，而不是玩家身上的 attachment：组件会随物品栏
     * 自动同步到客户端，所以 tooltip 和 HUD 都能直接读到层数，一行网络代码都不用写。
     * 用玩家 attachment 的话，客户端拿不到这个数字，HUD 就显示不出来。</p>
     */
    public static final DataComponentType<Integer> MARBAS_STACKS = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            AUniqueEpic.id("marbas_stacks"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    /**
     * 噬心大剑·共噬的免死冷却，单位是<b>剩余 tick</b>。
     *
     * <p>存"剩余"而不是"到期时刻"：客户端拿不到游戏刻，存剩余时间的话 tooltip 才能显示出
     * 有意义的秒数。</p>
     */
    public static final DataComponentType<Integer> COMMUNION_COOLDOWN = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            AUniqueEpic.id("communion_cooldown"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    public static void init() {
        AUniqueEpic.LOGGER.debug("[组件] marbas_stacks / communion_cooldown 已注册");
    }
}
