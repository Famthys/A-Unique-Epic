package com.famthys.uniqueepic.hero;

import com.famthys.uniqueepic.AUniqueEpic;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 勇者之征的总入口：注册物品，然后把四条效果挂到对应事件上。
 *
 * <p>事件分工：</p>
 * <ul>
 *   <li>每秒 tick → 决心、重担的护甲、危机的负面时长与毒液</li>
 *   <li>击杀 → 不屈</li>
 *   <li>受伤 → 重担的随机负面效果</li>
 *   <li>{@code actuallyHurt} mixin → 重担的受伤翻倍、危机的火焰加剧</li>
 * </ul>
 */
public final class HeroesExpedition {

    private HeroesExpedition() {
    }

    public static void init() {
        ModHeroItems.init();

        ServerLivingEntityEvents.AFTER_DEATH.register(HeroesExpedition::afterDeath);
        ServerLivingEntityEvents.AFTER_DAMAGE.register(HeroesExpedition::afterDamage);
        ServerTickEvents.END_SERVER_TICK.register(HeroesExpedition::endServerTick);

        AUniqueEpic.LOGGER.info("[勇者之征] 已注册：饰品栏 chest/necklace + 决心/不屈/重担/危机");
    }

    /** 二、不屈：击杀时若已半血，回一波生命恢复。 */
    private static void afterDeath(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer killer && killer != entity) {
            HeroesExpeditionEffects.onKill(killer, entity);
        }
    }

    /** 三、重担：受伤时有概率被附加随机负面效果。 */
    private static void afterDamage(LivingEntity entity, DamageSource source, float baseDamage,
                                    float damageTaken, boolean blocked) {
        if (blocked || !(entity instanceof ServerPlayer player)) {
            return;
        }
        HeroesExpeditionEffects.onDamageTaken(player, source, damageTaken);
    }

    /** 每秒一次：戴着就跑效果，没戴就把加过的属性撤掉。 */
    private static void endServerTick(MinecraftServer server) {
        int interval = HeroesExpeditionConfig.tickInterval;
        if (interval <= 0) {
            return;
        }
        long gameTime = server.overworld().getGameTime();
        if (gameTime % interval != 0L) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (HeroesExpeditionEffects.isActive(player)) {
                HeroesExpeditionEffects.tick(player);
            } else {
                HeroesExpeditionEffects.removeArmorPenalty(player);
            }
        }
    }
}
