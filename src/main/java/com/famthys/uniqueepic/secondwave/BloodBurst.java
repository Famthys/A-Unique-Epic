package com.famthys.uniqueepic.secondwave;

import com.famthys.uniqueepic.equipment.EquipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 「厄血爆发」：共噬触发免死时，以玩家为中心炸开一圈真实伤害 + 红色粒子。
 */
public final class BloodBurst {

    private BloodBurst() {
    }

    /** 粒子颜色：暗红。 */
    private static final int BLOOD_COLOR = 0xB01818;

    /**
     * 结算一次爆发。
     */
    public static void detonate(ServerPlayer player) {
        ServerLevel level = player.level();

        // ---- 伤害：真实伤害，用 indirect_magic（在 bypasses_armor 标签里，且能带上攻击者）----
        double radius = SecondWaveConfig.communionBurstRadius;
        AABB box = player.getBoundingBox().inflate(radius);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, box,
                candidate -> candidate != player
                        && candidate.isAlive()
                        && !(candidate instanceof Player)
                        && !isPlayersPet(player, candidate));

        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;   // 爆发应当必定命中，不受无敌帧影响
            target.hurtServer(level, level.damageSources().indirectMagic(player, player),
                    SecondWaveConfig.communionBurstDamage);
        }

        // ---- 粒子：以玩家为中心铺散开来 ----
        spawnParticles(level, player);

        // ---- 音效 ----
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.2F, 0.6F);
    }

    /**
     * 红色粒子铺散效果。
     *
     * <p>做法是沿多个同心球壳撒点：内圈密、外圈疏，看起来就是从中心扩散开后自然消散。
     * 粒子本身带少量向外速度，进一步强化"铺开"的感觉。</p>
     */
    private static void spawnParticles(ServerLevel level, ServerPlayer player) {
        double radius = SecondWaveConfig.communionBurstRadius;
        double density = SecondWaveConfig.communionParticleDensity;
        Vec3 center = player.position().add(0, 0.6, 0);

        int shells = 5;
        net.minecraft.util.RandomSource random = level.getRandom();
        for (int shell = 1; shell <= shells; shell++) {
            double shellRadius = radius * shell / shells;
            int count = (int) Math.round(shellRadius * 6 * density);
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2;
                // 压扁成盘状：主要是地面扩散，少量向上
                double y = random.nextDouble() * 1.6 - 0.2;
                double x = Math.cos(theta) * shellRadius;
                double z = Math.sin(theta) * shellRadius;

                // 向外的小速度
                double speed = 0.15 + shell * 0.05;
                level.sendParticles(
                        new net.minecraft.core.particles.DustParticleOptions(BLOOD_COLOR, 1.6F),
                        center.x + x, center.y + y, center.z + z,
                        1,
                        Math.cos(theta) * 0.35, 0.05, Math.sin(theta) * 0.35,
                        speed);
            }
        }
    }

    /** 触发免死的那一套：保命 + 生命恢复 + 爆发。 */
    public static void revive(ServerPlayer player) {
        player.setHealth(SecondWaveConfig.communionReviveHealth);
        player.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                SecondWaveConfig.communionRegenerationTicks,
                SecondWaveConfig.communionRegenerationAmplifier,
                false, true, true));
        detonate(player);
    }

    /** 判断某个生物是不是玩家的宠物（不打自己的狗）。 */
    static boolean isPlayersPet(ServerPlayer player, LivingEntity candidate) {
        return candidate instanceof net.minecraft.world.entity.TamableAnimal pet
                && pet.isTame()
                && pet.getOwner() == player;
    }

    /** 玩家是否带着共噬。 */
    static boolean carriesCommunion(ServerPlayer player) {
        return EquipUtil.carries(player, ModSecondWaveItems.HEART_DEVOURER_COMMUNION);
    }
}
