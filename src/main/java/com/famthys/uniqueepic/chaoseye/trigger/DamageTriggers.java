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
 * 受伤相关的事件。
 *
 * <p>一个事件顺手干两件事：被打的玩家进入"愉悦"，如果是玩家打的，就记被打者/打人者的"还手时间"。
 * 这样不需要额外注册攻击事件。</p>
 */
public final class DamageTriggers {

    private DamageTriggers() {
    }

    public static void afterDamage(LivingEntity entity, DamageSource source, float baseDamage,
                                   float damageTaken, boolean blocked) {
        if (blocked || damageTaken <= 0.0F) {
            return;
        }
        long gameTime = entity.level().getGameTime();

        // 打人的是玩家 -> 记一笔"还手了"，用于逃跑判定
        if (source.getEntity() instanceof ServerPlayer attacker) {
            ChaosEyeState attackerState = ChaosEyeAttachments.get(attacker);
            if (attackerState.infected) {
                attackerState.lastDealtAt = gameTime;
            }
        }

        // 被打的是玩家 -> 愉悦
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }
        ChaosEyeState state = ChaosEyeAttachments.get(player);
        if (!state.infected) {
            return;
        }

        state.lastHurtAt = gameTime;
        EmotionEngine.setEmotion(player, state, Emotion.PLEASED, ChaosEyeConfig.pleasedTicks, gameTime, true);
    }
}
