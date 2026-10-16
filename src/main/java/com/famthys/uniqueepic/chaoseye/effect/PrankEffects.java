package com.famthys.uniqueepic.chaoseye.effect;

import com.famthys.uniqueepic.chaoseye.ChaosEyeConfig;
import net.minecraft.server.level.ServerPlayer;

/**
 * 恶作剧接口。
 *
 * <p>设定里"无聊"时混沌之眸会随机恶作剧：召唤怪物、传送、打乱快捷栏。这三种都有破坏性
 * （传送可能卡墙卡虚空、召唤可能刷爆实体），所以 P1 只留骨架，默认关闭。</p>
 *
 * <p>P3 实现时注意：传送前做安全落点检测，召唤要做数量上限，打乱快捷栏只动服务端可同步的格子。</p>
 */
public final class PrankEffects {

    private PrankEffects() {
    }

    /** 可选的恶作剧种类。 */
    public enum Prank {
        SUMMON_MONSTER,
        TELEPORT,
        SHUFFLE_HOTBAR
    }

    public static boolean isEnabled() {
        return ChaosEyeConfig.pranksEnabled;
    }

    /**
     * 触发一次恶作剧。P1 阶段即使被调用也什么都不会发生。
     */
    public static void trigger(ServerPlayer player, Prank prank) {
        if (!ChaosEyeConfig.pranksEnabled) {
            return;
        }
        // TODO(P3): 按 prank 分支实现
    }
}
