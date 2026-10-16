package com.famthys.uniqueepic.chaoseye;

/**
 * 混沌之眸的五种情绪（对应设定总纲的"情绪系统"）。
 *
 * <p>存储时只序列化 {@link #id()}，这样以后调换枚举顺序或者插入新情绪都不会破坏旧存档。</p>
 */
public enum Emotion {

    /** 未被寄生 / 没有情绪。 */
    NONE("none", 0x555555),

    /** 愉悦：玩家承受痛苦。 */
    PLEASED("pleased", 0xFF55FF),

    /** 无聊：玩家长时间安全。 */
    BORED("bored", 0x9E9E9E),

    /** 兴奋：玩家挑战强敌。 */
    EXCITED("excited", 0xFFAA00),

    /** 失望：玩家逃跑、回避战斗。 */
    DISAPPOINTED("disappointed", 0x5577FF),

    /** 暴怒：玩家死亡。 */
    FURIOUS("furious", 0xFF3B30);

    private final String id;
    private final int color;

    Emotion(String id, int color) {
        this.id = id;
        this.color = color;
    }

    public String id() {
        return id;
    }

    /** 用于 HUD / 命令显示的 RGB 颜色。 */
    public int color() {
        return color;
    }

    /** 本地化键，例如 chaose_eye.emotion.pleased。 */
    public String translationKey() {
        return "chaos_eye.emotion." + id;
    }

    /** 某条台词的本地化键，例如 chaose_eye.line.pleased.2。 */
    public String lineKey(int index) {
        return "chaos_eye.line." + id + "." + index;
    }

    /** 宽容解析：遇到不认识的字符串一律回落到 NONE，避免旧存档把游戏读崩。 */
    public static Emotion byId(String id) {
        for (Emotion emotion : values()) {
            if (emotion.id.equals(id)) {
                return emotion;
            }
        }
        return NONE;
    }
}
