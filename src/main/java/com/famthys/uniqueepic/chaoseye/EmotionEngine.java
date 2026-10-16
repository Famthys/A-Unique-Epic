package com.famthys.uniqueepic.chaoseye;

import com.famthys.uniqueepic.chaoseye.dialogue.EyeDialogue;
import com.famthys.uniqueepic.chaoseye.effect.EmotionEffects;
import com.famthys.uniqueepic.hero.HeroesExpeditionEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.AABB;

/**
 * 情绪状态机。
 *
 * <p>被动的受伤 / 击杀事件负责"立刻"切情绪，这个类负责"随时间变化"的部分：
 * 情绪倒计时、逃跑判定、挑战强敌判定、无聊判定。</p>
 */
public final class EmotionEngine {

    private EmotionEngine() {
    }

    /**
     * 切换情绪。同一种情绪重复触发时只续时长，不重复上效果、不重复说话。
     *
     * @param announce 是否允许说一句台词（受刷屏冷却限制）
     */
    public static void setEmotion(ServerPlayer player, ChaosEyeState state, Emotion emotion, int ticks,
                                  long gameTime, boolean announce) {
        if (!state.infected) {
            return;
        }
        // 勇者之征：只要带在身上就完全静默——情绪不触发、不上效果、不说话
        if (HeroesExpeditionEffects.isActive(player)) {
            state.emotion = Emotion.NONE;
            state.emotionTicks = 0;
            return;
        }
        if (state.emotion == emotion && state.emotionTicks > 0) {
            state.emotionTicks = Math.max(state.emotionTicks, ticks);
            return;
        }

        state.emotion = emotion;
        state.emotionTicks = ticks;

        if (emotion == Emotion.NONE) {
            return;
        }

        EmotionEffects.apply(player, emotion, ticks);

        if (announce) {
            EyeDialogue.say(player, state, emotion, gameTime, false);
        }
    }

    /** 每秒（默认）跑一次的判定。 */
    public static void tick(ServerPlayer player, ChaosEyeState state, long gameTime) {
        if (!state.infected) {
            return;
        }
        // 勇者之征：只要带在身上就完全静默
        if (HeroesExpeditionEffects.isActive(player)) {
            state.emotion = Emotion.NONE;
            state.emotionTicks = 0;
            return;
        }

        // 情绪倒计时
        if (state.emotionTicks > 0) {
            state.emotionTicks--;
            if (state.emotionTicks <= 0) {
                state.emotionTicks = 0;
                state.emotion = Emotion.NONE;
            }
        }

        // 逃跑判定：受伤之后一直在挨打却没还手
        if (state.emotion == Emotion.PLEASED
                && state.lastHurtAt > state.lastDealtAt
                && gameTime - state.lastHurtAt >= ChaosEyeConfig.revengeWindowTicks) {
            setEmotion(player, state, Emotion.DISAPPOINTED, ChaosEyeConfig.disappointedTicks, gameTime, true);
            return;
        }

        // 挑战强敌判定
        if (countNearbyMonsters(player) >= ChaosEyeConfig.excitedNearbyMonsters) {
            setEmotion(player, state, Emotion.EXCITED, ChaosEyeConfig.excitedTicks, gameTime, true);
            return;
        }

        // 无聊判定：很长时间没有任何战斗行为。
        // 三道闸门缺一不可，否则无聊会自我续期、把玩家永久卡在无聊里：
        //   1) 当前没有别的情绪；
        //   2) 距上次"干了点什么"已经够久；
        //   3) 距上次"无聊过"也已经够久（给玩家一个喘息的间隔）。
        if (state.emotion == Emotion.NONE
                && gameTime - state.lastActivity() >= ChaosEyeConfig.boredAfterCalmTicks
                && gameTime - state.lastBoredAt >= ChaosEyeConfig.boredAfterCalmTicks) {
            state.lastBoredAt = gameTime;
            setEmotion(player, state, Emotion.BORED, ChaosEyeConfig.boredTicks, gameTime, true);
        }
    }

    private static int countNearbyMonsters(ServerPlayer player) {
        double radius = ChaosEyeConfig.excitedMonsterRadius;
        AABB box = player.getBoundingBox().inflate(radius);
        return player.level().getEntitiesOfClass(Monster.class, box, Monster::isAlive).size();
    }
}
