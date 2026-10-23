package com.famthys.uniqueepic.secondwave;

/**
 * 第二波装备的可调数值。
 */
public final class SecondWaveConfig {

    private SecondWaveConfig() {
    }

    /** 效果判定间隔（tick）。 */
    public static int tickInterval = 20;

    /* ==================== 噬心大剑·共噬 ==================== */

    /**
     * 免死的冷却（tick）。
     *
     * <p>设定里没写冷却，但没有冷却就等于"永久不死"——死亡会无限触发。这里加了冷却，
     * 数值可调，想做成无限触发把它设成 0 即可。</p>
     */
    public static int communionCooldown = 60 * 20;

    /** 触发免死后保留的血量。 */
    public static float communionReviveHealth = 6.0F;

    /** 免死时给予的生命恢复等级（1 = 生命恢复 II，对应设定里的"生命恢复2"）。 */
    public static int communionRegenerationAmplifier = 1;

    /** 免死时生命恢复的持续时间（tick）。 */
    public static int communionRegenerationTicks = 15 * 20;

    /** 厄血爆发的半径（格）。设定：15 格。 */
    public static double communionBurstRadius = 15.0D;

    /** 厄血爆发的伤害。设定只写了"真实伤害"没给数值，这个是暂定值。 */
    public static float communionBurstDamage = 30.0F;

    /** 粒子密度系数：1.0 为标准，越大越密。 */
    public static double communionParticleDensity = 1.0D;

    /* ==================== 湖中剑·湖光 ==================== */

    /** 吟唱时长（tick）。60 = 3 秒，正好够念三行。 */
    public static int lakeLightChantTicks = 60;

    /** 吟唱期间每隔多少 tick 念一行。 */
    public static int lakeLightLineInterval = 20;

    /** 斩击长度（格）。设定：64 格。 */
    public static double lakeLightRange = 64.0D;

    /** 斩击宽度（格）。设定：3 格宽。 */
    public static double lakeLightWidth = 3.0D;

    /** 斩击的高度（格）。 */
    public static double lakeLightHeight = 5.0D;

    /** 斩击伤害。设定：64。 */
    public static float lakeLightDamage = 64.0F;

    /** 斩击冷却（tick）。 */
    public static int lakeLightCooldown = 15 * 20;

    /** 与石中剑双持时的伤害加成比例。 */
    public static float lakeLightDualWieldBonus = 0.5F;

    /** 粒子密度系数。 */
    public static double lakeLightParticleDensity = 1.0D;

    /* ==================== 湖光 · 补强 ==================== */

    /** 吟唱期间的受伤倍率（0.5 = 减半）。 */
    public static float lakeLightChantDamageMultiplier = 0.5F;

    /** 吟唱期间是否锁死移动。 */
    public static boolean lakeLightChantImmobile = true;

    /* ==================== 共噬 · 补强 ==================== */

    /** 免死爆发时，从每个敌人身上抽取的最大生命比例。 */
    public static double communionDrainRatio = 0.10D;

    /** 单次抽取的上限（点）。 */
    public static double communionDrainCap = 20.0D;

    /** 单个目标被抽取的总上限（占其基础生命上限的比例）。 */
    public static double communionDrainTotalCap = 0.30D;

    /** 抽取到的生命值有多少转化为你的治疗。 */
    public static float communionDrainHealRatio = 0.5F;

    /** 被抽取的生命上限持续多久（tick）。 */
    public static int communionDrainDuration = 30 * 20;
}
