package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * 厄血系装备：噬心大剑、厄血套装、玛尔巴斯之噬。
 */
public final class BloodEquipment {

    private BloodEquipment() {
    }

    private static final Identifier SET_DAMAGE_ID = AUniqueEpic.id("blood_set_damage");
    private static final Identifier SET_ARMOR_ID = AUniqueEpic.id("blood_set_armor");
    private static final Identifier MARBAS_DAMAGE_ID = AUniqueEpic.id("marbas_damage");

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

    /**
     * 每击杀一名敌人叠一层。
     *
     * <p>顺带推一条动作栏提示——没有这个反馈，玩家根本察觉不到自己在变强，
     * 这正是之前"效果不明显"的根因。</p>
     */
    public static void onKill(ServerPlayer killer) {
        ItemStack fang = findFang(killer);
        if (fang.isEmpty()) {
            return;
        }
        int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        if (stacks >= EquipConfig.marbasMaxStacks) {
            return;
        }

        int next = stacks + 1;
        fang.set(ModComponents.MARBAS_STACKS, next);

        int bonus = (int) Math.round(next * EquipConfig.marbasPerStack * 100);
        killer.sendOverlayMessage(Component.translatable(
                        "message.aunique-epic.marbas_stack",
                        next, EquipConfig.marbasMaxStacks, bonus)
                .withStyle(ChatFormatting.GOLD));
    }

    /** 每秒把层数换算成攻击力加成。 */
    public static void tickMarbas(ServerPlayer player) {
        ItemStack fang = findFang(player);
        if (fang.isEmpty()) {
            EquipAttributes.clear(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID);
            return;
        }
        int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        EquipAttributes.set(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID,
                stacks * EquipConfig.marbasPerStack,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /**
     * 找到玩家身上那件玛尔巴斯之噬：先看饰品栏，再看背包 / 快捷栏 / 盔甲 / 副手。
     *
     * @return 栏位里那个真实的 ItemStack（可直接写组件），没有就返回 {@link ItemStack#EMPTY}
     */
    public static ItemStack findFang(ServerPlayer player) {
        if (TrinketsApi.getAttachment(player).isEquipped(ModEquipmentItems.MARBAS_FANG)) {
            Optional<TrinketSlotAccess> slot =
                    TrinketsApi.getAttachment(player).findFirst(ModEquipmentItems.MARBAS_FANG);
            if (slot.isPresent()) {
                return slot.get().get();
            }
        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(ModEquipmentItems.MARBAS_FANG)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int stacks(ServerPlayer player) {
        ItemStack fang = findFang(player);
        return fang.isEmpty() ? 0 : fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
    }
}
