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
}
