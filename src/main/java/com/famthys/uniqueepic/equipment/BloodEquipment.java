package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * 厄血系装备：噬心大剑、厄血套装、玛尔巴斯之噬。
 */
public final class BloodEquipment {

    private BloodEquipment() {
    }

    private static final Identifier SET_DAMAGE_ID = AUniqueEpic.id("blood_set_damage");
    private static final Identifier SET_ARMOR_ID = AUniqueEpic.id("blood_set_armor");
    private static final Identifier MARBAS_DAMAGE_ID = AUniqueEpic.id("marbas_damage");

    /**
     * 玛尔巴斯之噬的叠加层数。
     *
     * <p>{@code copyOnDeath()} 让"击杀叠层"跨死亡保留——设定里没写死亡会清零，
     * 而一个需要重新叠 10 层的饰品体验太差。</p>
     */
    public static final AttachmentType<Integer> MARBAS_STACKS = AttachmentRegistry
            .<Integer>create(AUniqueEpic.id("marbas_stacks"), builder -> builder
                    .persistent(Codec.INT)
                    .copyOnDeath());

    /* ==================== 噬心大剑 ==================== */

    /** 攻击回复生命。 */
    public static void onDealDamage(ServerPlayer attacker, float damageDealt) {
        if (damageDealt <= 0.0F || !EquipUtil.holds(attacker, ModEquipmentItems.HEART_DEVOURER)) {
            return;
        }
        float heal = Math.min(damageDealt * EquipConfig.greatswordLifesteal,
                EquipConfig.greatswordLifestealCap);
        if (heal > 0.0F) {
            attacker.heal(heal);
        }
    }

    /** 反噬：手持时持续轻微掉血。 */
    public static void backlash(ServerPlayer player, long gameTime) {
        int interval = EquipConfig.greatswordBacklashInterval;
        if (interval <= 0 || gameTime % interval != 0L) {
            return;
        }
        if (!EquipUtil.holds(player, ModEquipmentItems.HEART_DEVOURER)) {
            return;
        }
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        player.hurtServer(player.level(), player.damageSources().generic(),
                EquipConfig.greatswordBacklashDamage);
    }

    /* ==================== 厄血套装 ==================== */

    /** 穿齐四件时：血越少攻击越高，满血时防御极强。 */
    public static void tickSet(ServerPlayer player) {
        boolean full = EquipUtil.wearsFullSet(player,
                ModEquipmentItems.FEROCIOUS_MASK,
                ModEquipmentItems.MUTATED_CHESTPLATE,
                ModEquipmentItems.RAMPAGE_LEGGINGS,
                ModEquipmentItems.SHATTERED_BOOTS);

        if (!full) {
            EquipAttributes.clear(player, Attributes.ATTACK_DAMAGE, SET_DAMAGE_ID);
            EquipAttributes.clear(player, Attributes.ARMOR, SET_ARMOR_ID);
            return;
        }

        float maxHealth = player.getMaxHealth();
        float ratio = maxHealth <= 0.0F ? 1.0F : player.getHealth() / maxHealth;

        // 生命值越低，攻击力越高
        double missing = Math.max(0.0D, Math.min(1.0D, 1.0D - ratio));
        EquipAttributes.set(player, Attributes.ATTACK_DAMAGE, SET_DAMAGE_ID,
                missing * EquipConfig.bloodSetMaxDamageBonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

        // 满血时防御力极强
        boolean fullHealth = ratio >= 1.0F - EquipConfig.bloodSetFullHealthEpsilon;
        EquipAttributes.set(player, Attributes.ARMOR, SET_ARMOR_ID,
                fullHealth ? EquipConfig.bloodSetFullHealthArmor : 0.0D,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /* ==================== 玛尔巴斯之噬 ==================== */

    /** 每击杀一名敌人叠一层。 */
    public static void onKill(ServerPlayer killer) {
        if (!EquipUtil.carries(killer, ModEquipmentItems.MARBAS_FANG)) {
            return;
        }
        int stacks = stacks(killer);
        if (stacks >= EquipConfig.marbasMaxStacks) {
            return;
        }
        setStacks(killer, stacks + 1);
    }

    /** 每秒把层数换算成攻击力加成。 */
    public static void tickMarbas(ServerPlayer player) {
        if (!EquipUtil.carries(player, ModEquipmentItems.MARBAS_FANG)) {
            EquipAttributes.clear(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID);
            return;
        }
        EquipAttributes.set(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID,
                stacks(player) * EquipConfig.marbasPerStack,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    public static int stacks(ServerPlayer player) {
        return ((AttachmentTarget) player).getAttachedOrCreate(MARBAS_STACKS, () -> 0);
    }

    public static void setStacks(ServerPlayer player, int value) {
        ((AttachmentTarget) player).setAttached(MARBAS_STACKS, Math.max(0, value));
    }
}
