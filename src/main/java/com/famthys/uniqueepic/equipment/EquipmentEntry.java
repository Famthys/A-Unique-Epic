package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * 史诗装备的总入口（第一波）。
 */
public final class EquipmentEntry {

    private EquipmentEntry() {
    }

    public static void init() {
        ModComponents.init();
        ModEquipmentItems.init();

        ServerLivingEntityEvents.AFTER_DAMAGE.register(EquipmentEntry::afterDamage);
        ServerLivingEntityEvents.AFTER_DEATH.register(EquipmentEntry::afterDeath);
        ServerTickEvents.END_SERVER_TICK.register(EquipmentEntry::endServerTick);

        AUniqueEpic.LOGGER.info("[史诗装备] 第一波已注册：噬心大剑 / 石中剑 / 圆桌骑士套装 / 厄血套装 / 玛尔巴斯之噬");
    }

    private static void afterDamage(LivingEntity entity, DamageSource source, float baseDamage,
                                    float damageTaken, boolean blocked) {
        if (blocked || damageTaken <= 0.0F) {
            return;
        }
        if (source.getEntity() instanceof ServerPlayer attacker && attacker != entity) {
            BloodEquipment.onDealDamage(attacker, damageTaken);
        }
    }

    private static void afterDeath(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer killer && killer != entity) {
            BloodEquipment.onKill(killer);
        }
    }

    private static void endServerTick(MinecraftServer server) {
        int interval = EquipConfig.tickInterval;
        if (interval <= 0) {
            return;
        }
        long gameTime = server.overworld().getGameTime();
        if (gameTime % interval != 0L) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            BloodEquipment.tickSet(player);
            BloodEquipment.tickMarbas(player);
            GloryEquipment.tickSet(player);
            BloodEquipment.backlash(player, gameTime);
        }
    }
}
