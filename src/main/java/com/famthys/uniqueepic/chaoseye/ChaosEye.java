package com.famthys.uniqueepic.chaoseye;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.chaoseye.command.ChaosEyeCommand;
import com.famthys.uniqueepic.chaoseye.dialogue.EyeDialogue;
import com.famthys.uniqueepic.chaoseye.trigger.CombatTriggers;
import com.famthys.uniqueepic.chaoseye.trigger.DamageTriggers;
import com.famthys.uniqueepic.chaoseye.trigger.IdleTriggers;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;

/**
 * 混沌之眸的总入口：注册数据附着，然后把所有事件挂上。
 *
 * <p>这里是 P0 + P1 的范围：寄生、五种情绪、情绪效果、台词、调试命令。
 * P2 的诅咒骰子 / 混沌能量 / 混沌牌局，P3 的恶作剧与 HUD，都在后续阶段补。</p>
 */
public final class ChaosEye {

    private ChaosEye() {
    }

    public static void init() {
        // 类加载即注册 attachment
        ChaosEyeAttachments.init();

        ServerPlayerEvents.JOIN.register(ChaosEye::onPlayerJoin);
        ServerPlayerEvents.AFTER_RESPAWN.register(CombatTriggers::afterRespawn);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(DamageTriggers::afterDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(CombatTriggers::afterDeath);
        ServerTickEvents.END_SERVER_TICK.register(IdleTriggers::endServerTick);
        CommandRegistrationCallback.EVENT.register(ChaosEyeCommand::register);

        AUniqueEpic.LOGGER.info("[混沌之眸] 已注册：数据附着 + 情绪系统（P1）");
    }

    /**
     * 玩家进入世界：首次强制寄生。
     *
     * <p>已经寄生的、或者玩家主动用 /chaoseye cure 拒绝过的，都不再重复寄生。</p>
     */
    private static void onPlayerJoin(ServerPlayer player) {
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        if (state.infected || state.cured || !ChaosEyeConfig.infectOnFirstJoin) {
            return;
        }

        long gameTime = player.level().getGameTime();
        state.infected = true;
        state.infectedAt = gameTime;
        state.clearCombatMemory(gameTime);

        EyeDialogue.sayInfect(player);
        AUniqueEpic.LOGGER.info("[混沌之眸] {} 已被寄生", player.getName().getString());
    }
}
