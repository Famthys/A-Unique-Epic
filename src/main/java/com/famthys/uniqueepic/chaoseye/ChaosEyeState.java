package com.famthys.uniqueepic.chaoseye;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 混沌之眸挂在玩家身上的全部状态。
 *
 * <p>可变对象：接管在玩家身上之后就地修改，不需要每次重建。序列化时所有字段都带默认值
 * （{@code optionalFieldOf}），所以以后加字段、删字段都不会让旧存档读不出来。</p>
 */
public final class ChaosEyeState {

    /** 是否已经被混沌之眸寄生。 */
    public boolean infected;

    /** 是否已经主动"拒绝"过寄生（用 /chaoseye cure 卸除后设为 true，避免重进世界又被寄生）。 */
    public boolean cured;

    /** 当前情绪。 */
    public Emotion emotion = Emotion.NONE;

    /** 当前情绪剩余时长（tick）。 */
    public int emotionTicks;

    /** 混沌能量 0~100（P2 使用，P1 先占位持久化）。 */
    public int energy;

    /** 首次被寄生的游戏刻。 */
    public long infectedAt;

    /** 最近一次受伤的游戏刻。 */
    public long lastHurtAt;

    /** 最近一次造成伤害 / 击杀的游戏刻。 */
    public long lastDealtAt;

    /** 最近一次击杀的游戏刻。 */
    public long lastKillAt;

    /** 累计死亡次数（暴怒情绪用）。 */
    public int deaths;

    /** 上一句台词的游戏刻，用来做刷屏冷却。 */
    public long lastDialogueAt;

    /**
     * 上一次触发"无聊"的游戏刻。
     *
     * <p>没有这个字段的话，无聊会自我刷新：只要玩家一直没动静，"距上次活动超过阈值"这个条件
     * 就永远成立，情绪被无限续期，玩家会被永久卡在无聊里出不来。</p>
     */
    public long lastBoredAt;

    public ChaosEyeState() {
    }

    /** Codec 反序列化用的构造器，参数顺序必须和下面的 group 一致。 */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public ChaosEyeState(boolean infected,
                         boolean cured,
                         String emotion,
                         int emotionTicks,
                         int energy,
                         long infectedAt,
                         long lastHurtAt,
                         long lastDealtAt,
                         long lastKillAt,
                         int deaths,
                         long lastDialogueAt,
                         long lastBoredAt) {
        this.infected = infected;
        this.cured = cured;
        this.emotion = Emotion.byId(emotion);
        this.emotionTicks = emotionTicks;
        this.energy = energy;
        this.infectedAt = infectedAt;
        this.lastHurtAt = lastHurtAt;
        this.lastDealtAt = lastDealtAt;
        this.lastKillAt = lastKillAt;
        this.deaths = deaths;
        this.lastDialogueAt = lastDialogueAt;
        this.lastBoredAt = lastBoredAt;
    }

    public static final Codec<ChaosEyeState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("infected", false).forGetter(s -> s.infected),
            Codec.BOOL.optionalFieldOf("cured", false).forGetter(s -> s.cured),
            Codec.STRING.optionalFieldOf("emotion", Emotion.NONE.id()).forGetter(s -> s.emotion.id()),
            Codec.INT.optionalFieldOf("emotion_ticks", 0).forGetter(s -> s.emotionTicks),
            Codec.INT.optionalFieldOf("energy", 0).forGetter(s -> s.energy),
            Codec.LONG.optionalFieldOf("infected_at", 0L).forGetter(s -> s.infectedAt),
            Codec.LONG.optionalFieldOf("last_hurt_at", 0L).forGetter(s -> s.lastHurtAt),
            Codec.LONG.optionalFieldOf("last_dealt_at", 0L).forGetter(s -> s.lastDealtAt),
            Codec.LONG.optionalFieldOf("last_kill_at", 0L).forGetter(s -> s.lastKillAt),
            Codec.INT.optionalFieldOf("deaths", 0).forGetter(s -> s.deaths),
            Codec.LONG.optionalFieldOf("last_dialogue_at", 0L).forGetter(s -> s.lastDialogueAt),
            Codec.LONG.optionalFieldOf("last_bored_at", 0L).forGetter(s -> s.lastBoredAt)
    ).apply(instance, ChaosEyeState::new));

    /** 清空情绪与战斗记忆（保留寄生状态）。 */
    public void clearCombatMemory(long gameTime) {
        emotion = Emotion.NONE;
        emotionTicks = 0;
        lastHurtAt = gameTime;
        lastDealtAt = gameTime;
        lastKillAt = gameTime;
        lastDialogueAt = 0L;
        lastBoredAt = 0L;
    }

    /** 最近一次"干了点什么"的游戏刻。 */
    public long lastActivity() {
        return Math.max(lastHurtAt, Math.max(lastDealtAt, lastKillAt));
    }
}
