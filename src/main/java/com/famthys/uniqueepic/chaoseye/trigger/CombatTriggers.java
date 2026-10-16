package com.famthys.uniqueepic.chaoseye.trigger;

import com.famthys.uniqueepic.chaoseye.ChaosEyeAttachments;
import com.famthys.uniqueepic.chaoseye.ChaosEyeConfig;
import com.famthys.uniqueepic.chaoseye.ChaosEyeState;
import com.famthys.uniqueepic.chaoseye.Emotion;
import com.famthys.uniqueepic.chaoseye.EmotionEngine;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 击杀与死亡相关的事件。
 */
public final class CombatTriggers {

    private CombatTriggers() {
    }

    /** 有生物死亡：如果凶手是玩家，玩家进入"兴奋"。 */
    public static void afterDeath(LivingEntity entity, DamageSource source) {
        if (!(source.getEntity() instanceof ServerPlayer killer) || killer == entity) {
            return;
        }
        long gameTime = entity.level().getGameTime();
        ChaosEyeState state = ChaosEyeAttachments.get(killer);
        if (!state.infected) {
            return;
        }
        state.lastDealtAt = gameTime;
        state.lastKillAt = gameTime;
        EmotionEngine.setEmotion(killer, state, Emotion.EXCITED, ChaosEyeConfig.excitedTicks, gameTime, true);
    }

    /** 玩家重生：设定里"玩家死亡 -> 暴怒"，复活后 3 分钟。 */
    public static void afterRespawn(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        long gameTime = newPlayer.level().getGameTime();
        ChaosEyeState state = ChaosEyeAttachments.get(newPlayer);
        if (!state.infected) {
            return;
        }
        state.deaths++;
        state.lastHurtAt = gameTime;
        state.lastDealtAt = gameTime;
        state.lastKillAt = gameTime;
        EmotionEngine.setEmotion(newPlayer, state, Emotion.FURIOUS, ChaosEyeConfig.furiousTicks, gameTime, true);
    }
}
