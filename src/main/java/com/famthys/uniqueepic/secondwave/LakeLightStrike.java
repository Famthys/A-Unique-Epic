package com.famthys.uniqueepic.secondwave;

import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 「湖光斩」：3 格宽 × 64 格长的光剑斩击 + 巨型光剑落下的粒子表现。
 */
public final class LakeLightStrike {

    private LakeLightStrike() {
    }

    /** 光剑的浅蓝白。 */
    private static final int BLADE_COLOR = 0x9FD8FF;
    /** 斩击轨迹的金。 */
    private static final int TRAIL_COLOR = 0xE8C15A;

    /**
     * 释放一次斩击。
     */
    public static void release(ServerPlayer player, ItemStack stack) {
        ServerLevel level = player.level();

        // 朝向只取水平分量：光束沿地面向前推，不受俯仰影响，结果更可预期
        Vec3 look = player.getLookAngle();
        Vec3 dir = new Vec3(look.x, 0.0D, look.z);
        if (dir.lengthSqr() < 1.0E-4D) {
            dir = new Vec3(0.0D, 0.0D, 1.0D);
        }
        dir = dir.normalize();

        double range = SecondWaveConfig.lakeLightRange;
        Vec3 start = player.position();
        Vec3 end = start.add(dir.scale(range));

        // ---- 伤害 ----
        double halfWidth = SecondWaveConfig.lakeLightWidth / 2.0D;
        double halfHeight = SecondWaveConfig.lakeLightHeight / 2.0D;
        AABB box = new AABB(start, end).inflate(halfWidth, halfHeight, halfWidth);

        boolean dualWield = player.getOffhandItem().is(ModEquipmentItems.SWORD_IN_STONE)
                || player.getMainHandItem().is(ModEquipmentItems.SWORD_IN_STONE);
        float damage = SecondWaveConfig.lakeLightDamage;
        if (dualWield) {
            damage *= 1.0F + SecondWaveConfig.lakeLightDualWieldBonus;
        }

        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                candidate -> candidate != player
                        && candidate.isAlive()
                        && !(candidate instanceof Player)
                        && !BloodBurst.isPlayersPet(player, candidate));

        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;
            target.hurtServer(level, level.damageSources().indirectMagic(player, player), damage);
        }

        // ---- 表现 ----
        spawnBeam(level, start, dir, range);
        spawnGiantSword(level, end);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.5F, 0.8F);

        player.getCooldowns().addCooldown(stack, SecondWaveConfig.lakeLightCooldown);
    }

    /** 地面上的斩击轨迹：64 格长、3 格宽。 */
    private static void spawnBeam(ServerLevel level, Vec3 start, Vec3 dir, double range) {
        Vec3 side = new Vec3(-dir.z, 0.0D, dir.x);
        double halfWidth = SecondWaveConfig.lakeLightWidth / 2.0D;
        double density = SecondWaveConfig.lakeLightParticleDensity;

        int steps = (int) Math.round(range * density);
        for (int i = 0; i < steps; i++) {
            Vec3 along = start.add(dir.scale(i));
            // 横向扫 3 格宽
            for (double offset = -halfWidth; offset <= halfWidth; offset += 0.75D) {
                Vec3 point = along.add(side.scale(offset));
                level.sendParticles(new DustParticleOptions(TRAIL_COLOR, 1.2F),
                        point.x, start.y + 0.2D, point.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            // 每隔几格加一道向上的光柱，强化"剑锋掠过"
            if (i % 6 == 0) {
                for (double h = 0.0D; h < 2.0D; h += 0.5D) {
                    level.sendParticles(new DustParticleOptions(BLADE_COLOR, 1.0F),
                            along.x, start.y + h, along.z, 1, 0.0D, 0.05D, 0.0D, 0.0D);
                }
            }
        }
    }

    /**
     * 巨型光剑落下的表现。
     *
     * <p>在落点竖着立起一柄高约 24 格的"剑"：剑身是竖直光柱，上方横一道护手，顶端一颗亮点。
     * 理想效果是"从天上砸下来"，但粒子是瞬发的、没法自己播放动画——要真正做出下落过程，
     * 需要按 tick 分帧生成（后续可以加）。现在这是静止的"巨剑插在落点"。</p>
     */
    private static void spawnGiantSword(ServerLevel level, Vec3 impact) {
        double bladeHeight = 24.0D;

        // 剑身：竖直线
        for (double y = 0.0D; y < bladeHeight; y += 0.5D) {
            level.sendParticles(new DustParticleOptions(BLADE_COLOR, 2.2F),
                    impact.x, impact.y + y, impact.z, 1, 0.02D, 0.0D, 0.02D, 0.0D);
        }

        // 护手：顶端往下 4 格处横一道
        double guardY = impact.y + bladeHeight - 4.0D;
        for (double x = -3.0D; x <= 3.0D; x += 0.5D) {
            level.sendParticles(new DustParticleOptions(TRAIL_COLOR, 1.6F),
                    impact.x + x, guardY, impact.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }

        // 剑尖：顶端一束强光
        for (int i = 0; i < 12; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    impact.x, impact.y + bladeHeight, impact.z,
                    1, 0.1D, 0.1D, 0.1D, 0.02D);
        }

        // 落点冲击：向四周炸开一圈 + 中心闪光
        net.minecraft.util.RandomSource random = level.getRandom();
        for (int i = 0; i < 40; i++) {
            double theta = random.nextDouble() * Math.PI * 2;
            double speed = 0.4D + random.nextDouble() * 0.6D;
            level.sendParticles(new DustParticleOptions(TRAIL_COLOR, 1.8F),
                    impact.x, impact.y + 0.3D, impact.z,
                    1, Math.cos(theta) * 0.3D, 0.15D, Math.sin(theta) * 0.3D, speed);
        }
        // 中心闪光：用 END_ROD 堆一簇，比 FLASH 省事（FLASH 要构造 ColorParticleOption）
        level.sendParticles(ParticleTypes.END_ROD,
                impact.x, impact.y + 8.0D, impact.z, 30, 0.4D, 3.0D, 0.4D, 0.12D);
    }
}
