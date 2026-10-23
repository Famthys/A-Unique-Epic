package com.famthys.uniqueepic.equipment;

/**
 * 第一波装备的可调数值。
 *
 * <p>和其它系统一样：字段非 final，方便后续接 JSON 配置。</p>
 */
public final class EquipConfig {

    private EquipConfig() {
    }

    /** 所有效果判定的间隔（tick）。 */
    public static int tickInterval = 20;

    /* ==================== 噬心大剑 ==================== */

    /** 攻击回复的生命比例（相对造成的伤害）。 */
    public static float greatswordLifesteal = 0.25F;

    /** 每次攻击最多回多少血，避免一刀回满。 */
    public static float greatswordLifestealCap = 6.0F;

    /** 反噬间隔（tick）：每隔这么久掉一次血。 */
    public static int greatswordBacklashInterval = 80;

    /** 每次反噬的伤害。 */
    public static float greatswordBacklashDamage = 1.0F;

    /* ==================== 石中剑 ==================== */

    /** 王威的作用半径（格）。 */
    public static double sacredSwordRadius = 8.0D;

    /** 王威的冷却（tick）。 */
    public static int sacredSwordCooldown = 20 * 20;

    /** 王威的震慑时长（tick）。 */
    public static int sacredSwordDuration = 8 * 20;

    /* ==================== 圆桌骑士套装 ==================== */

    /** 每名友方生物提供的护甲加成比例。 */
    public static double roundTablePerAlly = 0.10D;

    /** 护甲加成上限。 */
    public static double roundTableMax = 0.80D;

    /** 友方检测半径（格）。 */
    public static double roundTableRadius = 12.0D;

    /* ==================== 厄血套装 ==================== */

    /** 空血时最多提升多少攻击力（比例）。 */
    public static double bloodSetMaxDamageBonus = 1.00D;

    /** 满血时的护甲加成比例。 */
    public static double bloodSetFullHealthArmor = 1.00D;

    /** 判定"满血"的容差（血量的比例）。 */
    public static float bloodSetFullHealthEpsilon = 0.02F;

    /* ==================== 玛尔巴斯之噬 ==================== */

    /** 每层提供的攻击力比例。 */
    public static double marbasPerStack = 0.05D;

    /** 最大层数。 */
    public static int marbasMaxStacks = 10;

    /* ==================== 噬心大剑 · 饥渴 ==================== */

    /** 每缺 1 格饥饿（2 点饱食度）提供的伤害加成。 */
    public static double hungerPerDrumstickBonus = 0.06D;

    /** 每次命中额外消耗的饥饿值（消耗度，4.0 约等于 1 点饱食度）。 */
    public static float hungerCostPerHit = 3.0F;

    /** 满饱食时的伤害惩罚（负数）。 */
    public static double hungerFullPenalty = -0.20D;

    /** 满饱食时剑"咬你"的概率。 */
    public static float hungerBiteChance = 0.25F;

    /** 被咬的伤害。 */
    public static float hungerBiteDamage = 1.0F;

    /* ==================== 玛尔巴斯之噬 · 寄生 ==================== */

    /** 饱食度上限。 */
    public static int marbasSatietyMax = 100;

    /** 每击杀一名敌人回复的饱食度。 */
    public static int marbasSatietyPerKill = 18;

    /** 每秒自然消耗的饱食度（它一直在饿）。 */
    public static int marbasSatietyDecayPerSecond = 1;

    /** 替玩家挡下一次致命伤害所需的饱食度。 */
    public static int marbasSatietyBlockCost = 60;

    /** 挡刀后保留的血量。 */
    public static float marbasBlockReviveHealth = 4.0F;

    /** 饱食度为 0 时每秒咬玩家的伤害。 */
    public static float marbasStarvingBiteDamage = 1.0F;

    /* ==================== 厄血套装 · 血债 ==================== */

    /** 血债是否启用（想关掉这个机制就设 false，套装退回普通属性加成）。 */
    public static boolean debtEnabled = true;

    /** 血债上限：达到最大生命值的这个比例就一次性结算。 */
    public static float debtLimitRatio = 0.40F;

    /** 你造成 1 点伤害能抵掉多少血债。 */
    public static float debtPayoffRatio = 1.0F;

    /** 每秒自然消退的血债（给个下限，否则打不出伤害就是必死）。 */
    public static float debtDecayPerSecond = 0.5F;

    /** 还清血债时回复的生命。 */
    public static float debtClearedHeal = 2.0F;
}
