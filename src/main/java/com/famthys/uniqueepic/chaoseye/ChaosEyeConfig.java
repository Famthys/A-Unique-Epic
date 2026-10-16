package com.famthys.uniqueepic.chaoseye;

/**
 * 混沌之眸的全部可调数值。
 *
 * <p>P1 阶段先集中放在这个类里，字段刻意不用 final——后续接 JSON 配置文件时，
 * 只要在读取配置后给这些字段赋值即可，业务代码一行都不用改。</p>
 */
public final class ChaosEyeConfig {

    private ChaosEyeConfig() {
    }

    /* ---------------- 寄生 ---------------- */

    /** 玩家首次进入世界时是否强制寄生。调试时可以关掉。 */
    public static boolean infectOnFirstJoin = true;

    /* ---------------- 情绪持续时间（单位：tick，20 tick = 1 秒） ---------------- */

    /** 愉悦：承受痛苦时触发，给予随机强力增益 30 秒。 */
    public static int pleasedTicks = 30 * 20;

    /** 兴奋：挑战强敌或击杀时触发。 */
    public static int excitedTicks = 30 * 20;

    /** 失望：受伤后不还手（逃跑）时触发。 */
    public static int disappointedTicks = 60 * 20;

    /** 暴怒：玩家死亡后触发 3 分钟。 */
    public static int furiousTicks = 3 * 60 * 20;

    /** 无聊：长时间无所事事时触发。 */
    public static int boredTicks = 2 * 60 * 20;

    /* ---------------- 触发阈值 ---------------- */

    /** 受伤后多少 tick 内没有还手，就判定为"逃跑"，转为失望。 */
    public static int revengeWindowTicks = 8 * 20;

    /** 多久没有任何战斗行为就判定为"无聊"。 */
    public static int boredAfterCalmTicks = 3 * 60 * 20;

    /** 附近有多少只敌对生物算"挑战强敌"。 */
    public static int excitedNearbyMonsters = 3;

    /** "挑战强敌"的检测半径（格）。 */
    public static double excitedMonsterRadius = 16.0D;

    /* ---------------- 节奏 ---------------- */

    /** 情绪判定的间隔（tick）。 */
    public static int evaluateIntervalTicks = 20;

    /** 两次台词的冷却（tick），防止刷屏。 */
    public static int dialogueCooldownTicks = 10 * 20;

    /* ---------------- 恶作剧（P3 才实现，P1 只留开关） ---------------- */

    /** 恶作剧总开关。P1 默认关闭：召唤怪物、传送、打乱快捷栏都有破坏性。 */
    public static boolean pranksEnabled = false;
}
