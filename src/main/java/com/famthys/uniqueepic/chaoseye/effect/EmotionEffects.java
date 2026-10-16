package com.famthys.uniqueepic.chaoseye.effect;

import com.famthys.uniqueepic.chaoseye.Emotion;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

/**
 * 把"情绪"翻译成原版状态效果。
 *
 * <p>P1 刻意不写自定义 MobEffect：用原版效果拼装，代码量最小，也不会和其他模组打架。
 * 等 P3 再考虑把"受伤+50%"这类原版没有的效果用自定义状态或 mixin 实现。</p>
 */
public final class EmotionEffects {

    private EmotionEffects() {
    }

    /** 愉悦：随机一个强力增益。 */
    private static final List<Holder<MobEffect>> PLEASED_POOL = List.of(
            MobEffects.STRENGTH,
            MobEffects.SPEED,
            MobEffects.RESISTANCE,
            MobEffects.REGENERATION
    );

    /** 失望：随机一个低级负面。P1 还没有诅咒系统，先用原版负面效果顶替。 */
    private static final List<Holder<MobEffect>> DISAPPOINTED_POOL = List.of(
            MobEffects.WEAKNESS,
            MobEffects.SLOWNESS,
            MobEffects.MINING_FATIGUE
    );

    /**
     * 按情绪给玩家上效果。持续时间直接等于情绪剩余时长，情绪结束效果自然到期，不需要额外清理。
     *
     * @param player 目标玩家
     * @param emotion 当前情绪
     * @param ticks 持续时间（tick）
     */
    public static void apply(ServerPlayer player, Emotion emotion, int ticks) {
        if (ticks <= 0) {
            return;
        }
        RandomSource random = player.getRandom();
        switch (emotion) {
            case PLEASED -> {
                Holder<MobEffect> pick = PLEASED_POOL.get(random.nextInt(PLEASED_POOL.size()));
                // 生命恢复 用 I 级就够，其余给 II 级，符合设定里"强力增益"的描述
                int amplifier = pick == MobEffects.REGENERATION ? 0 : 1;
                offer(player, pick, ticks, amplifier);
            }
            case EXCITED -> {
                // 设定：伤害 +50%。原版没有"受伤 +50%"的效果，
                // P1 先用 STRENGTH II 近似输出端加成，"受伤 +50%"留到 P3 用 mixin 实现。
                offer(player, MobEffects.STRENGTH, ticks, 1);
            }
            case DISAPPOINTED -> {
                Holder<MobEffect> pick = DISAPPOINTED_POOL.get(random.nextInt(DISAPPOINTED_POOL.size()));
                offer(player, pick, ticks, 0);
            }
            case FURIOUS -> {
                // 设定：复活后 3 分钟"仇恨翻倍"。P1 近似为攻击与移速双提升。
                offer(player, MobEffects.STRENGTH, ticks, 1);
                offer(player, MobEffects.SPEED, ticks, 0);
            }
            case NONE, BORED -> {
                // 无聊不直接给效果，P3 通过恶作剧接口表现
            }
            default -> {
            }
        }
    }

    private static void offer(ServerPlayer player, Holder<MobEffect> effect, int ticks, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, ticks, amplifier, false, true, true));
    }
}
