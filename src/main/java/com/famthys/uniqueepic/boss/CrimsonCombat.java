package com.famthys.uniqueepic.boss;

import java.util.UUID;

/** 服务端每 tick 调用一次。纯逻辑，不依赖 Minecraft 客户端或动画库。 */
public final class CrimsonCombat {
    public enum Phase { IDLE, CHASE, ATTACK, COOLDOWN, DYING, DEAD }
    public enum Event { NONE, ATTACK_START, STRIKE, DEATH_START, REMOVE }
    public record Input(boolean alive, UUID targetId, boolean targetAlive,
                        double targetDistance, boolean lineOfSight) {}
    public record Output(Phase phase, int phaseTick, Event event, boolean move,
                         boolean trackFacing, UUID attackTarget) {}
    public static final int ATTACK_TICKS = 40;
    public static final int HIT_TICK = 18;       // 0.9 秒：和动画完全一致。
    public static final int LOCK_TICK = 12;      // 最后 0.3 秒前摇不给自动追踪。
    public static final int COOLDOWN_TICKS = 20;
    public static final int DEATH_TICKS = 52;    // 2.6 秒死亡动作播完才移除。
    public static final double START_RANGE = 3.4;
    public static final double HIT_RANGE = 4.2;
    public static final double ARC_DEGREES = 100;
    private Phase phase = Phase.IDLE;
    private int tick = 0;
    private UUID attackTarget;

    public Output tick(Input in) {
        // 死亡优先于任何攻击帧，死亡后的实体绝不产生新伤害。
        if (!in.alive() && phase != Phase.DYING && phase != Phase.DEAD) {
            phase = Phase.DYING; tick = 0; attackTarget = null;
            return out(Event.DEATH_START, false, false);
        }
        if (phase == Phase.DEAD) return out(Event.NONE, false, false);
        if (phase == Phase.DYING) {
            if (++tick >= DEATH_TICKS) {
                phase = Phase.DEAD;
                return out(Event.REMOVE, false, false);
            }
            return out(Event.NONE, false, false);
        }
        boolean target = in.targetId() != null && in.targetAlive();
        if (phase == Phase.ATTACK) {
            ++tick;
            // 事件只在此精确帧发出一次。即使命中失败，也不在后续帧补判。
            if (tick == HIT_TICK) return out(Event.STRIKE, false, false);
            if (tick >= ATTACK_TICKS) {
                phase = Phase.COOLDOWN; tick = 0; attackTarget = null;
            }
            return out(Event.NONE, false, phase == Phase.ATTACK && tick < LOCK_TICK);
        }
        if (phase == Phase.COOLDOWN) {
            if (++tick < COOLDOWN_TICKS) return out(Event.NONE, false, target);
            phase = Phase.IDLE; tick = 0;
        }
        if (!target) { phase = Phase.IDLE; tick = 0; return out(Event.NONE, false, false); }
        if (in.targetDistance() <= START_RANGE && in.lineOfSight()) {
            phase = Phase.ATTACK; tick = 0; attackTarget = in.targetId();
            return out(Event.ATTACK_START, false, true);
        }
        phase = Phase.CHASE; tick = 0;
        return out(Event.NONE, true, true);
    }

    /** 在伤害事件时使用实时位置重新判定；前摇锁定后可以侧闪或后退躲避。 */
    public static boolean canHit(UUID locked, UUID current, boolean alive, boolean visible,
            double horizontalDistance, double verticalDistance, double relativeYawDegrees) {
        return locked != null && locked.equals(current) && alive && visible
                && horizontalDistance >= 0 && horizontalDistance <= HIT_RANGE
                && Math.abs(verticalDistance) <= 3.0
                && Math.abs(wrap(relativeYawDegrees)) <= ARC_DEGREES / 2;
    }
    private static double wrap(double v) { return ((v + 180) % 360 + 360) % 360 - 180; }
    private Output out(Event e, boolean move, boolean face) {
        return new Output(phase, tick, e, move, face, attackTarget);
    }
    public Phase phase() { return phase; }
    public int phaseTick() { return tick; }
    public UUID attackTarget() { return attackTarget; }
}
