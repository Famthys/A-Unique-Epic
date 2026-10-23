package com.famthys.uniqueepic.boss;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

/**
 * 26.2 官方命名接入层。在 Boss 服务端 AI tick 调用一次；死亡阶段也须继续 tick。
 * 核心类已单独测试，本接入层须合入你的实体工程后用 Java 25 编译验证。
 */
public final class CrimsonCombatBridge {
    public interface Hooks {
        // 实体把这两项写入 SynchedEntityData，客户端据此计算动画播放时间。
        void phaseChanged(CrimsonCombat.Phase phase, long startGameTick);
        // 死亡动画结束；在实体中执行一次原有的移除/经验收尾。
        void deathFinished();
    }
    private final CrimsonCombat combat = new CrimsonCombat();
    private CrimsonCombat.Phase lastPhase;
    private float lockedYaw;

    public void tick(Monster boss, Hooks hooks) {
        if (!(boss.level() instanceof ServerLevel level)) return;
        LivingEntity target = boss.getTarget();
        boolean valid = target != null && target.isAlive();
        var out = combat.tick(new CrimsonCombat.Input(boss.isAlive(),
                valid ? target.getUUID() : null, valid,
                valid ? boss.distanceTo(target) : Double.POSITIVE_INFINITY,
                valid && boss.hasLineOfSight(target)));
        if (out.phase() != lastPhase) {
            hooks.phaseChanged(out.phase(), level.getGameTime());
            lastPhase = out.phase();
        }
        if (out.move() && valid) {
            boss.getNavigation().moveTo(target, 1.0);
        } else {
            boss.getNavigation().stop();
            Vec3 velocity = boss.getDeltaMovement();
            boss.setDeltaMovement(0, velocity.y, 0);
        }
        if (out.trackFacing() && valid) {
            boss.getLookControl().setLookAt(target, 30, 30);
            double dx = target.getX() - boss.getX(), dz = target.getZ() - boss.getZ();
            lockedYaw = (float)Math.toDegrees(Math.atan2(-dx, dz));
            boss.setYRot(lockedYaw);
            boss.yBodyRot = lockedYaw;
        } else if (out.phase() == CrimsonCombat.Phase.ATTACK) {
            boss.setYRot(lockedYaw);
            boss.yBodyRot = lockedYaw;
        }
        if (out.event() == CrimsonCombat.Event.STRIKE && valid) {
            double dx = target.getX() - boss.getX(), dz = target.getZ() - boss.getZ();
            double yaw = Math.toDegrees(Math.atan2(-dx, dz)) - lockedYaw;
            if (CrimsonCombat.canHit(out.attackTarget(), target.getUUID(), target.isAlive(),
                    boss.hasLineOfSight(target), Math.hypot(dx, dz),
                    target.getY() - boss.getY(), yaw)) {
                // 只有服务端调用伤害；伤害值从实体属性读取，不写死在模型或客户端。
                float damage = (float)boss.getAttributeValue(Attributes.ATTACK_DAMAGE);
                var source = boss.damageSources().mobAttack(boss);
                if (target.hurtServer(level, source, damage)) {
                    // 26.2 的 knockback 要带 DamageSource（旧的 3 参数重载已移除）
                    target.knockback(0.6, -dx, -dz, source, damage);
                }
            }
        }
        if (out.event() == CrimsonCombat.Event.REMOVE) hooks.deathFinished();
    }
    public CrimsonCombat.Phase phase() { return combat.phase(); }
    public int phaseTick() { return combat.phaseTick(); }
}
