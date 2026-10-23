package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 厄血套装的「血债」机制。
 *
 * <p>核心思路：<b>你受到的伤害不立刻扣血，而是记成一笔债</b>。你打出去的伤害会抵扣这笔债；
 * 债还清了就爆一团血雾并回一点血；债堆到上限就<b>一次性全部结算</b>——可能当场暴毙。</p>
 *
 * <p>于是战斗节奏从"躲伤害"变成"赶紧打回来还债"。</p>
 */
public final class BloodDebt {

    private BloodDebt() {
    }

    /** 血债值（点）。纯服务端状态，靠动作栏提示反馈，不需要同步到客户端。 */
    public static final AttachmentType<Double> DEBT = AttachmentRegistry
            .<Double>create(AUniqueEpic.id("blood_debt"), builder -> builder
                    .persistent(Codec.DOUBLE)
                    .copyOnDeath());

    /**
     * 结算中标记。
     *
     * <p>很关键：结算本身会造成伤害，而那笔伤害又会走一遍受伤管线——没有这个标记会无限递归。</p>
     */
    private static final Set<UUID> SETTLING = ConcurrentHashMap.newKeySet();

    /* ==================== 状态读写 ==================== */

    public static double get(ServerPlayer player) {
        return ((AttachmentTarget) player).getAttachedOrCreate(DEBT, () -> 0.0D);
    }

    private static void set(ServerPlayer player, double value) {
        ((AttachmentTarget) player).setAttached(DEBT, Math.max(0.0D, value));
    }

    /** 玩家是否穿齐厄血四件。 */
    public static boolean active(ServerPlayer player) {
        if (!EquipConfig.debtEnabled) {
            return false;
        }
        return EquipUtil.wearsFullSet(player,
                ModEquipmentItems.FEROCIOUS_MASK,
                ModEquipmentItems.MUTATED_CHESTPLATE,
                ModEquipmentItems.RAMPAGE_LEGGINGS,
                ModEquipmentItems.SHATTERED_BOOTS);
    }

    private static double limit(ServerPlayer player) {
        return Math.max(1.0D, player.getMaxHealth() * EquipConfig.debtLimitRatio);
    }

    /* ==================== 接管受伤 ==================== */

    /**
     * 把这次伤害记成血债。
     *
     * @return {@code true} 表示已被接管——调用方应把这次真实伤害抹成 0
     */
    public static boolean absorb(ServerPlayer player, float amount) {
        if (amount <= 0.0F || !active(player)) {
            return false;
        }
        // 结算过程中放行，避免自己吞自己
        if (SETTLING.contains(player.getUUID())) {
            return false;
        }

        double next = get(player) + amount;
        if (next >= limit(player)) {
            settle(player, next);
        } else {
            set(player, next);
            notifyDebt(player);
        }
        return true;
    }

    /* ==================== 打伤害抵债 ==================== */

    /** 你造成伤害时抵扣血债；还清时爆血雾并回血。 */
    public static void pay(ServerPlayer player, float damageDealt) {
        if (damageDealt <= 0.0F || !active(player)) {
            return;
        }
        double debt = get(player);
        if (debt <= 0.0D) {
            return;
        }

        double next = Math.max(0.0D, debt - damageDealt * EquipConfig.debtPayoffRatio);
        set(player, next);

        if (next <= 0.0D) {
            cleared(player);
        } else {
            notifyDebt(player);
        }
    }

    /* ==================== 每秒维护 ==================== */

    /** 自然消退 + 清理。没穿套装时把债清空，避免卸装备后债还在。 */
    public static void tick(ServerPlayer player, long gameTime) {
        if (!active(player)) {
            if (get(player) > 0.0D) {
                set(player, 0.0D);
            }
            return;
        }
        double debt = get(player);
        if (debt <= 0.0D) {
            return;
        }
        double next = Math.max(0.0D, debt - EquipConfig.debtDecayPerSecond);
        set(player, next);
        if (next <= 0.0D) {
            cleared(player);
        } else {
            notifyDebt(player);
        }
    }

    /* ==================== 结算与还清 ==================== */

    /** 一次性结算：把欠的全部还上，可能当场死。 */
    private static void settle(ServerPlayer player, double debt) {
        set(player, 0.0D);
        SETTLING.add(player.getUUID());
        try {
            ServerLevel level = player.level();
            // 用 magic：无视护甲，债就是债
            player.hurtServer(level, level.damageSources().magic(), (float) debt);
            spawnMist(level, player, 2, 1.4F);
            player.sendOverlayMessage(Component.translatable(
                            "message.aunique-epic.debt_settled", (int) Math.round(debt))
                    .withStyle(ChatFormatting.DARK_RED));
        } finally {
            SETTLING.remove(player.getUUID());
        }
    }

    /** 还清：血雾 + 少量治疗。 */
    private static void cleared(ServerPlayer player) {
        ServerLevel level = player.level();
        spawnMist(level, player, 1, 0.9F);
        if (EquipConfig.debtClearedHeal > 0.0F) {
            player.heal(EquipConfig.debtClearedHeal);
        }
        player.sendOverlayMessage(Component.translatable("message.aunique-epic.debt_cleared")
                .withStyle(ChatFormatting.RED));
    }

    /** 血债越高，雾气越浓。 */
    private static void spawnMist(ServerLevel level, ServerPlayer player, int shells, float scale) {
        var random = level.getRandom();
        for (int shell = 1; shell <= shells; shell++) {
            double radius = 0.8D * shell;
            for (int i = 0; i < 14; i++) {
                double theta = random.nextDouble() * Math.PI * 2;
                level.sendParticles(new DustParticleOptions(0x8E1F26, scale),
                        player.getX() + Math.cos(theta) * radius,
                        player.getY() + 0.6D + random.nextDouble() * 0.8D,
                        player.getZ() + Math.sin(theta) * radius,
                        1, 0.0D, 0.06D, 0.0D, 0.02D);
            }
        }
    }

    private static void notifyDebt(ServerPlayer player) {
        double debt = get(player);
        player.sendOverlayMessage(Component.translatable(
                        "message.aunique-epic.debt", (int) Math.round(debt), (int) Math.round(limit(player)))
                .withStyle(ChatFormatting.RED));
    }

    /* ==================== 属性联动 ==================== */

    /**
     * 血债期间的护甲加成。
     *
     * <p>债越多护甲越高——"欠债的人拼命护着自己"，也让债这个机制不只是惩罚。</p>
     */
    public static void refreshArmor(ServerPlayer player) {
        double debt = get(player);
        double bonus = debt <= 0.0D ? 0.0D : Math.min(0.5D, debt / limit(player) * 0.5D);
        EquipAttributes.set(player, Attributes.ARMOR, ARMOR_ID, bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static final net.minecraft.resources.Identifier ARMOR_ID =
            AUniqueEpic.id("blood_debt_armor");
}
