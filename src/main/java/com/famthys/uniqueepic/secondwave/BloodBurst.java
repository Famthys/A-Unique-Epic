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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

        double drained = 0.0D;
        for (LivingEntity target : targets) {
            target.invulnerableTime = 0;   // 爆发应当必定命中，不受无敌帧影响
            target.hurtServer(level, level.damageSources().indirectMagic(player, player),
                    SecondWaveConfig.communionBurstDamage);
            drained += drainMaxHealth(target);
        }

        // ---- 抽来的生命喂给自己（"共噬"的字面意思）----
        if (drained > 0.0D) {
            float heal = (float) (drained * SecondWaveConfig.communionDrainHealRatio);
            if (heal > 0.0F) {
                player.heal(heal);
            }
        }

        // ---- 粒子：以玩家为中心铺散开来 ----
        spawnParticles(level, player);

        // ---- 音效 ----
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 1.2F, 0.6F);
    }

    /** 抽取生命上限用的固定修饰符 id。 */
    private static final net.minecraft.resources.Identifier DRAIN_ID =
            com.famthys.uniqueepic.AUniqueEpic.id("communion_drain");

    /**
     * 从一个目标身上抽走一部分生命上限，喂给玩家。
     *
     * <p>抽取是<b>永久</b>的（直到它死），所以加了双重封顶：单次抽取有上限，
     * 单个目标被抽的总量也不超过其基础生命上限的 {@code communionDrainTotalCap}。
     * 否则对 Boss 反复使用会把它的血条抽没。</p>
     *
     * @return 实际抽到的点数
     */
    private static double drainMaxHealth(LivingEntity target) {
        AttributeInstance instance = target.getAttribute(Attributes.MAX_HEALTH);
        if (instance == null) {
            return 0.0D;
        }

        double base = Math.max(1.0D, instance.getBaseValue());
        double totalCap = base * SecondWaveConfig.communionDrainTotalCap;
        AttributeModifier existing = instance.getModifier(DRAIN_ID);
        double alreadyDrained = existing == null ? 0.0D : -existing.amount();

        double want = Math.min(target.getMaxHealth() * SecondWaveConfig.communionDrainRatio,
                SecondWaveConfig.communionDrainCap);
        double newTotal = Math.min(alreadyDrained + want, totalCap);
        double gained = newTotal - alreadyDrained;
        if (gained <= 0.0D) {
            return 0.0D;
        }

        // 必须先移除再加：MC 的同 id 修饰符是"已存在就抛异常"而不是替换
        if (existing != null) {
            instance.removeModifier(DRAIN_ID);
        }
        instance.addTransientModifier(new AttributeModifier(
                DRAIN_ID, -newTotal, AttributeModifier.Operation.ADD_VALUE));
        return gained;
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
