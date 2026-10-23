package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.hero.HeroesExpeditionEffects;
import com.famthys.uniqueepic.secondwave.LakeLightItem;
import com.famthys.uniqueepic.secondwave.SecondWaveConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

/**
 * 玩家承受伤害的<b>统一入口</b>，由 {@code LivingEntityDamageMixin} 在 {@code actuallyHurt} 调用。
 *
 * <p>所有"改玩家受伤"的机制都串在这里，顺序很重要：</p>
 * <ol>
 *   <li>勇者之征：重担翻倍 / 危机火焰加剧</li>
 *   <li>湖光吟唱：受伤减半（把站桩三秒从纯风险变成赌注）</li>
 *   <li>厄血套装：血债接管——记成债，本次不扣血</li>
 * </ol>
 */
public final class IncomingDamage {

    private IncomingDamage() {
    }

    public static float modify(ServerPlayer player, DamageSource source, float amount) {
        if (amount <= 0.0F) {
            return amount;
        }

        // 1) 勇者之征
        float result = HeroesExpeditionEffects.modifyIncomingDamage(player, source, amount);

        // 2) 湖光吟唱中：减伤
        if (LakeLightItem.isChanting(player)) {
            result *= SecondWaveConfig.lakeLightChantDamageMultiplier;
        }

        // 3) 血债接管：记成债，本次真实伤害归零
        if (BloodDebt.absorb(player, result)) {
            return 0.0F;
        }

        return result;
    }
}
