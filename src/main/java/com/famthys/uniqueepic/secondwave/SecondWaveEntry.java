package com.famthys.uniqueepic.secondwave;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.equipment.ModComponents;
import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import com.famthys.uniqueepic.item.ModItemIds;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 第二波的总入口。
 *
 * <p>目前只有共噬需要事件（免死拦截 + 冷却递减）；湖光走的是物品自身的 use 流程。</p>
 */
public final class SecondWaveEntry {

    private SecondWaveEntry() {
    }

    public static void init() {
        ModSecondWaveItems.init();

        ServerLivingEntityEvents.ALLOW_DEATH.register(SecondWaveEntry::allowDeath);
        ServerTickEvents.END_SERVER_TICK.register(SecondWaveEntry::endServerTick);

        AUniqueEpic.LOGGER.info("[第二波] 已注册：噬心大剑·共噬 / 湖中剑·湖光");
    }

    /**
     * 免死拦截。
     *
     * @return {@code true} 允许死亡；{@code false} 拦下这次死亡
     */
    private static boolean allowDeath(LivingEntity entity, DamageSource source, float amount) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        ItemStack stack = findCommunion(player);
        if (stack.isEmpty()) {
            return true;
        }
        // 冷却中 → 正常死
        if (stack.getOrDefault(ModComponents.COMMUNION_COOLDOWN, 0) > 0) {
            return true;
        }

        stack.set(ModComponents.COMMUNION_COOLDOWN, SecondWaveConfig.communionCooldown);
        BloodBurst.revive(player);
        return false;
    }

    /** 每秒把冷却往下推一格。 */
    private static void endServerTick(MinecraftServer server) {
        int interval = SecondWaveConfig.tickInterval;
        if (interval <= 0) {
            return;
        }
        long gameTime = server.overworld().getGameTime();
        if (gameTime % interval != 0L) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ItemStack stack = findCommunion(player);
            if (stack.isEmpty()) {
                continue;
            }
            int remaining = stack.getOrDefault(ModComponents.COMMUNION_COOLDOWN, 0);
            if (remaining > 0) {
                stack.set(ModComponents.COMMUNION_COOLDOWN, Math.max(0, remaining - interval));
            }
        }
    }

    /** 找出玩家身上那件共噬（饰品栏 / 背包 / 快捷栏 / 盔甲 / 副手）。 */
    private static ItemStack findCommunion(ServerPlayer player) {
        return com.famthys.uniqueepic.equipment.EquipUtil.findStack(
                player, ModSecondWaveItems.HEART_DEVOURER_COMMUNION);
    }
}
