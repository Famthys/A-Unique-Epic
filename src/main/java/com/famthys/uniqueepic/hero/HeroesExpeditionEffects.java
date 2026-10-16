package com.famthys.uniqueepic.hero;

import com.famthys.uniqueepic.AUniqueEpic;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 勇者之征的四条效果。
 *
 * <p>效果名（决心 / 不屈 / 重担 / 危机）对应设定图上的四条。</p>
 */
public final class HeroesExpeditionEffects {

    private HeroesExpeditionEffects() {
    }

    /** 护甲削弱修饰符的固定 id。必须是常量，因为要靠它反复增删同一个修饰符。 */
    private static final Identifier ARMOR_PENALTY_ID = AUniqueEpic.id("heroes_expedition_burden");

    /** 重担：每次受伤有概率附加的随机负面效果。 */
    private static final List<Holder<MobEffect>> BURDEN_DEBUFFS = List.of(
            MobEffects.WEAKNESS,
            MobEffects.SLOWNESS,
            MobEffects.MINING_FATIGUE,
            MobEffects.HUNGER,
            MobEffects.NAUSEA
    );

    /* ==================== 是否生效 ==================== */

    /**
     * 玩家身上是否带着勇者之征。
     *
     * <p>只要在玩家自己的物品栏范围内就算生效：快捷栏、背包主格、盔甲槽、副手，
     * 以及 Trinkets 的饰品栏。也就是说玩家不需要非得把它装进饰品槽。</p>
     */
    public static boolean isActive(ServerPlayer player) {
        // 1) Trinkets 饰品栏
        if (TrinketsApi.getAttachment(player).isEquipped(ModHeroItems.HEROES_EXPEDITION)) {
            return true;
        }
        // 2) 快捷栏 / 背包 / 盔甲 / 副手
        //    玩家 Inventory 的 getContainerSize() 已经把这些格子都算在内了
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (inventory.getItem(slot).is(ModHeroItems.HEROES_EXPEDITION)) {
                return true;
            }
        }
        return false;
    }

    /* ==================== 每秒判定（tick） ==================== */

    /**
     * 戴着时每秒跑一次。
     */
    public static void tick(ServerPlayer player) {
        applyArmorPenalty(player, true);

        // ---- 一、决心 ----
        if (isHoldingWeapon(player)) {
            applyResolveStrength(player);
            // 饥饿值加速下降
            player.causeFoodExhaustion(HeroesExpeditionConfig.resolveExhaustionPerSecond);
        }
        if (player.getFoodData().getFoodLevel() <= HeroesExpeditionConfig.resolveLowFoodLevel) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 0, false, true, true));
            player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 60, 0, false, true, true));
        }

        // ---- 四、危机：负面效果时长 -25% ----
        shortenNegativeEffects(player);

        // ---- 四、危机：毒液足以致命 ----
        if (HeroesExpeditionConfig.crisisLethalPoison
                && player.hasEffect(MobEffects.POISON)
                && player.getHealth() <= 1.0F) {
            player.hurtServer(player.level(), player.damageSources().magic(), 1.0F);
        }
    }

    /**
     * 没带时每秒跑一次，负责把生效期间加上去的东西撤干净。
     *
     * <p>刻意用"每秒重算"代替"装备/卸下回调"：玩家死亡、掉线、被其他模组拿走物品等情况下
     * 回调都可能不触发，而重算永远是对的。</p>
     */
    public static void removeArmorPenalty(ServerPlayer player) {
        applyArmorPenalty(player, false);
    }

    /* ==================== 二、不屈（击杀时触发） ==================== */

    public static void onKill(ServerPlayer killer, LivingEntity victim) {
        if (!isActive(killer)) {
            return;
        }
        if (killer.getHealth() > killer.getMaxHealth() * HeroesExpeditionConfig.unbrokenHealthRatio) {
            return;
        }

        float victimMaxHealth = victim.getMaxHealth();
        int amplifier = 0;
        if (victimMaxHealth >= HeroesExpeditionConfig.unbrokenTier3Health) {
            amplifier = 2;
        } else if (victimMaxHealth >= HeroesExpeditionConfig.unbrokenTier2Health) {
            amplifier = 1;
        }

        killer.addEffect(new MobEffectInstance(
                MobEffects.REGENERATION,
                HeroesExpeditionConfig.unbrokenTicks,
                amplifier,
                false, true, true));
    }

    /* ==================== 三、重担（受伤时触发） ==================== */

    public static void onDamageTaken(ServerPlayer player, DamageSource source, float damageTaken) {
        if (damageTaken <= 0.0F || !isActive(player)) {
            return;
        }
        if (player.getRandom().nextFloat() >= HeroesExpeditionConfig.burdenDebuffChance) {
            return;
        }
        Holder<MobEffect> pick = BURDEN_DEBUFFS.get(player.getRandom().nextInt(BURDEN_DEBUFFS.size()));
        player.addEffect(new MobEffectInstance(
                pick,
                HeroesExpeditionConfig.burdenDebuffTicks,
                0,
                false, true, true));
    }

    /* ==================== 伤害修正（由 mixin 调用） ==================== */

    /**
     * 修正玩家最终承受的伤害。
     *
     * <p>由 {@code LivingEntityDamageMixin} 在 {@code actuallyHurt} 的入口处调用——那个位置拿到的
     * 已经是扣完护甲和附魔之后的血量，所以在这里翻倍就是字面意义上的"受到的伤害翻倍"。</p>
     */
    public static float modifyIncomingDamage(ServerPlayer player, DamageSource source, float amount) {
        if (amount <= 0.0F || !isActive(player)) {
            return amount;
        }

        float result = amount;

        // 三、重担：敌人对你的伤害为双倍
        result *= HeroesExpeditionConfig.burdenDamageMultiplier;

        // 四、危机：火焰足以致命
        if (source.is(DamageTypeTags.IS_FIRE)) {
            result *= HeroesExpeditionConfig.crisisFireDamageMultiplier;
        }

        return result;
    }

    /* ==================== 内部实现 ==================== */

    private static boolean isHoldingWeapon(ServerPlayer player) {
        ItemStack mainHand = player.getMainHandItem();
        return !mainHand.isEmpty() && (mainHand.is(ItemTags.SWORDS) || mainHand.is(ItemTags.AXES));
    }

    /**
     * 一、决心：持武器时获得力量。
     *
     * <p>"若已有力量效果则叠加一层"的判定方式：我们自己授予的力量效果时长很短
     * （{@link HeroesExpeditionConfig#resolveEffectTicks}），所以只要检测到的力量效果时长
     * 明显长于这个值，就说明它来自别的来源（药水、信标等），此时额外叠一层。</p>
     */
    private static void applyResolveStrength(ServerPlayer player) {
        int amplifier = HeroesExpeditionConfig.resolveBaseAmplifier;

        MobEffectInstance current = player.getEffect(MobEffects.STRENGTH);
        boolean fromElsewhere = current != null
                && current.getDuration() > HeroesExpeditionConfig.resolveEffectTicks + 20;
        if (fromElsewhere) {
            amplifier = Math.min(
                    current.getAmplifier() + HeroesExpeditionConfig.resolveStackBonus,
                    HeroesExpeditionConfig.resolveBaseAmplifier + HeroesExpeditionConfig.resolveStackBonus);
        }

        player.addEffect(new MobEffectInstance(
                MobEffects.STRENGTH,
                HeroesExpeditionConfig.resolveEffectTicks,
                amplifier,
                true, false, true));
    }

    /** 三、重担：护甲效力打折。 */
    private static void applyArmorPenalty(ServerPlayer player, boolean equipped) {
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor == null) {
            return;
        }

        AttributeModifier existing = armor.getModifier(ARMOR_PENALTY_ID);
        if (!equipped) {
            if (existing != null) {
                armor.removeModifier(ARMOR_PENALTY_ID);
            }
            return;
        }
        if (existing == null) {
            armor.addTransientModifier(new AttributeModifier(
                    ARMOR_PENALTY_ID,
                    HeroesExpeditionConfig.burdenArmorMultiplier - 1.0D,
                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }

    /** 四、危机：缩短所有负面效果的时长。 */
    private static void shortenNegativeEffects(ServerPlayer player) {
        float reduction = HeroesExpeditionConfig.crisisNegativeDurationReduction;
        if (reduction <= 0.0F) {
            return;
        }
        // 每跑一次少扣"本次间隔的 25%"——按时间比例削减，而不是每秒钟打个 75 折。
        // 直接乘 0.75 的话，效果每秒都在缩水，几秒内就会被削干净，那就不是 25% 了。
        int delta = Math.max(1, Math.round(HeroesExpeditionConfig.tickInterval * reduction));

        for (MobEffectInstance instance : player.getActiveEffects()) {
            if (instance.getEffect().value().isBeneficial() || instance.isInfiniteDuration()) {
                continue;
            }
            instance.mapDuration(remaining -> Math.max(0, remaining - delta));
        }
    }
}
