package com.famthys.uniqueepic.chaoseye.trigger;

import com.famthys.uniqueepic.chaoseye.ChaosEyeAttachments;
import com.famthys.uniqueepic.chaoseye.ChaosEyeConfig;
import com.famthys.uniqueepic.chaoseye.ChaosEyeState;
import com.famthys.uniqueepic.chaoseye.EmotionEngine;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/**
 * 全局定时器：把所有在线玩家的情绪往前推一格。
 *
 * <p>刻意不在玩家 tick 里做，而是挂在服务器 tick 上按固定间隔跑，避免每 tick 遍历实体。</p>
 */
public final class IdleTriggers {

    private IdleTriggers() {
    }

    public static void endServerTick(MinecraftServer server) {
        int interval = ChaosEyeConfig.evaluateIntervalTicks;
        if (interval <= 0) {
            return;
        }
        long gameTime = server.overworld().getGameTime();
        if (gameTime % interval != 0L) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ChaosEyeState state = ChaosEyeAttachments.get(player);
            if (!state.infected) {
                continue;
            }
            EmotionEngine.tick(player, state, gameTime);
        }
    }
}
