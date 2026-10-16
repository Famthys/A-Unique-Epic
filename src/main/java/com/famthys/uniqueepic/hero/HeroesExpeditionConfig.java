package com.famthys.uniqueepic.hero;

/**
 * 勇者之征的可调数值。
 *
 * <p>和混沌之眸一样：字段刻意不用 final，以后接 JSON 配置时只要赋值即可，业务代码不用动。</p>
 */
public final class HeroesExpeditionConfig {

    private HeroesExpeditionConfig() {
    }

    /** 效果判定的间隔（tick）。和混沌之眸共用 20 tick 的节奏。 */
    public static int tickInterval = 20;

    /* ---------------- 一、决心 ---------------- */

    /** 持武器时授予的力量等级（0 = 力量 I）。 */
    public static int resolveBaseAmplifier = 0;

    /** 若玩家另有力量来源，额外叠加的层数上限。 */
    public static int resolveStackBonus = 1;

    /** 授予力量时的效果时长（tick），需要大于判定间隔，否则会断档。 */
    public static int resolveEffectTicks = 60;

    /** 持武器时每秒额外消耗的饥饿值（饥饿值总量 20）。 */
    public static float resolveExhaustionPerSecond = 0.5F;

    /** 饥饿值低于等于这个数就附加迟缓与挖掘疲劳。 */
    public static int resolveLowFoodLevel = 6;

    /* ---------------- 二、不屈 ---------------- */

    /** 触发所需的血量比例上限：生命值 <= 最大生命值 * 此值 时才算"仅剩一半"。 */
    public static float unbrokenHealthRatio = 0.5F;

    /** 生命恢复的持续时间（tick）：5 秒。 */
    public static int unbrokenTicks = 5 * 20;

    /** 敌人最大生命值达到这个数，生命恢复提升到 II 级。 */
    public static float unbrokenTier2Health = 40.0F;

    /** 敌人最大生命值达到这个数，生命恢复提升到 III 级。 */
    public static float unbrokenTier3Health = 100.0F;

    /* ---------------- 三、重担 ---------------- */

    /** 护甲效力系数：0.6 表示护甲值按 60% 生效（即减 40%）。 */
    public static double burdenArmorMultiplier = 0.6D;

    /** 受到的伤害倍数：2.0 = 双倍。 */
    public static float burdenDamageMultiplier = 2.0F;

    /** 每次受伤时附加随机负面效果的概率。 */
    public static float burdenDebuffChance = 0.25F;

    /** 附加的负面效果持续时间（tick）。 */
    public static int burdenDebuffTicks = 8 * 20;

    /* ---------------- 四、危机 ---------------- */

    /** 负面效果时长的削减比例：0.25 = 减少 25%。 */
    public static float crisisNegativeDurationReduction = 0.25F;

    /** 火焰伤害的额外倍数（"火焰足以致命"）。 */
    public static float crisisFireDamageMultiplier = 2.0F;

    /** 允许中毒在低血量时真的打死你（"毒液足以致命"）。 */
    public static boolean crisisLethalPoison = true;
}
