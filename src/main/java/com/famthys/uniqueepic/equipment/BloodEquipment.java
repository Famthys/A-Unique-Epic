package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

/**
 * 厄血系装备：噬心大剑、厄血套装、玛尔巴斯之噬。
 *
 * <p>这一族的设计核心是<b>"用代价换力量"</b>，但每件装备的代价形式不同：</p>
 * <ul>
 *   <li>噬心大剑 —— 吃你的饥饿值（越饿越强）</li>
 *   <li>厄血套装 —— 把伤害记成血债（延迟结算）</li>
 *   <li>玛尔巴斯之噬 —— 是个活物，你喂它，它替你挡刀；你不喂，它啃你</li>
 * </ul>
 */
public final class BloodEquipment {

    private BloodEquipment() {
    }

    private static final Identifier MARBAS_DAMAGE_ID = AUniqueEpic.id("marbas_damage");
    private static final Identifier HUNGER_DAMAGE_ID = AUniqueEpic.id("hunger_damage");

    /* ==================== 噬心大剑 · 饥渴 ==================== */

    /**
     * 每秒重算「饥渴」加成。
     *
     * <p>你越饿剑越强：每缺一格饥饿（2 点饱食度）加一份伤害；<b>满饱食反而被剑惩罚</b>
     * ——它嫌你吃饱了没杀气。</p>
     */
    public static void tickGreatsword(ServerPlayer player) {
        if (!EquipUtil.holds(player, ModEquipmentItems.HEART_DEVOURER)) {
            EquipAttributes.clear(player, Attributes.ATTACK_DAMAGE, HUNGER_DAMAGE_ID);
            return;
        }

        int food = player.getFoodData().getFoodLevel();
        double bonus;
        if (food >= 20) {
            bonus = EquipConfig.hungerFullPenalty;
        } else {
            double missingDrumsticks = (20 - food) / 2.0D;
            bonus = missingDrumsticks * EquipConfig.hungerPerDrumstickBonus;
        }

        EquipAttributes.set(player, Attributes.ATTACK_DAMAGE, HUNGER_DAMAGE_ID, bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /**
     * 命中时：剑吃掉你的饥饿值；如果你吃饱了，它还可能咬你一口。
     */
    public static void onGreatswordHit(ServerPlayer attacker) {
        if (!EquipUtil.holds(attacker, ModEquipmentItems.HEART_DEVOURER)) {
            return;
        }

        int food = attacker.getFoodData().getFoodLevel();
        if (food >= 20) {
            // 满饱食：剑嫌你没杀气
            if (attacker.getRandom().nextFloat() < EquipConfig.hungerBiteChance) {
                attacker.hurtServer(attacker.level(), attacker.damageSources().magic(),
                        EquipConfig.hungerBiteDamage);
                attacker.sendOverlayMessage(Component.translatable(
                                "message.aunique-epic.hunger_bite")
                        .withStyle(ChatFormatting.DARK_RED));
            }
            return;
        }
        // 饿着的时候它才配合你，但代价是继续吃你的饥饿
        attacker.causeFoodExhaustion(EquipConfig.hungerCostPerHit);
    }

    /* ==================== 玛尔巴斯之噬 · 寄生 ==================== */

    /**
     * 每击杀一名敌人：叠层 + 喂饱它。
     *
     * <p>顺带推动作栏提示——没有反馈玩家察觉不到自己在变强，这正是之前"效果不明显"的根因。</p>
     */
    public static void onKill(ServerPlayer killer) {
        ItemStack fang = findFang(killer);
        if (fang.isEmpty()) {
            return;
        }

        // 喂它（无论层数是否已满）
        int satiety = satiety(fang);
        int maxSatiety = EquipConfig.marbasSatietyMax;
        int newSatiety = Math.min(maxSatiety, satiety + EquipConfig.marbasSatietyPerKill);
        fang.set(ModComponents.MARBAS_SATIETY, newSatiety);

        // 叠层
        int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        int newStacks = stacks;
        if (stacks < EquipConfig.marbasMaxStacks) {
            newStacks = stacks + 1;
            fang.set(ModComponents.MARBAS_STACKS, newStacks);
        }

        int bonus = (int) Math.round(newStacks * EquipConfig.marbasPerStack * 100);
        killer.sendOverlayMessage(Component.translatable(
                        "message.aunique-epic.marbas_stack",
                        newStacks, EquipConfig.marbasMaxStacks, bonus, newSatiety, maxSatiety)
                .withStyle(ChatFormatting.GOLD));
    }

    /**
     * 每秒维护：饱食度自然消耗 → 饿极了啃玩家；同时把层数换算成攻击力。
     */
    public static void tickMarbas(ServerPlayer player) {
        ItemStack fang = findFang(player);
        if (fang.isEmpty()) {
            EquipAttributes.clear(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID);
            return;
        }

        // --- 它一直在饿 ---
        int satiety = satiety(fang);
        int next = Math.max(0, satiety - EquipConfig.marbasSatietyDecayPerSecond);
        if (next != satiety) {
            fang.set(ModComponents.MARBAS_SATIETY, next);
        }

        // --- 饿到 0 就啃你 ---
        if (next <= 0 && !player.isCreative() && !player.isSpectator()) {
            player.hurtServer(player.level(), player.damageSources().magic(),
                    EquipConfig.marbasStarvingBiteDamage);
            player.sendOverlayMessage(Component.translatable("message.aunique-epic.marbas_starving")
                    .withStyle(ChatFormatting.DARK_RED));
        }

        // --- 层数换算攻击力 ---
        int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        EquipAttributes.set(player, Attributes.ATTACK_DAMAGE, MARBAS_DAMAGE_ID,
                stacks * EquipConfig.marbasPerStack,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /* ==================== 厄血套装 · 血债 ==================== */

    /**
     * 血债接管了厄血套装的属性逻辑：债越多护甲越高，债由你打出的伤害抵扣。
     *
     * @see BloodDebt
     */
    public static void tickSet(ServerPlayer player) {
        boolean full = EquipUtil.wearsFullSet(player,
                ModEquipmentItems.FEROCIOUS_MASK,
                ModEquipmentItems.MUTATED_CHESTPLATE,
                ModEquipmentItems.RAMPAGE_LEGGINGS,
                ModEquipmentItems.SHATTERED_BOOTS);

        if (!full) {
            BloodDebt.refreshArmor(player);
            return;
        }
        BloodDebt.refreshArmor(player);
    }

    /* ==================== 工具 ==================== */

    /**
     * 找到玩家身上那件玛尔巴斯之噬：先看饰品栏，再看背包 / 快捷栏 / 盔甲 / 副手。
     *
     * @return 栏位里那个真实的 ItemStack（可直接写组件），没有就返回 {@link ItemStack#EMPTY}
     */
    public static ItemStack findFang(ServerPlayer player) {
        return EquipUtil.findStack(player, ModEquipmentItems.MARBAS_FANG);
    }

    public static int stacks(ServerPlayer player) {
        ItemStack fang = findFang(player);
        return fang.isEmpty() ? 0 : fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
    }

    public static int satiety(ItemStack fang) {
        return fang.getOrDefault(ModComponents.MARBAS_SATIETY, 0);
    }

    public static int satiety(ServerPlayer player) {
        ItemStack fang = findFang(player);
        return fang.isEmpty() ? 0 : satiety(fang);
    }
}
