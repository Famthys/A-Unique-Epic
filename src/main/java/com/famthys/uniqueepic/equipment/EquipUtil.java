package com.famthys.uniqueepic.equipment;

import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * 装备相关的通用判定。
 *
 * <p>沿用勇者之征定下的规则：<b>带在身上就算数</b>——背包、快捷栏、盔甲槽、副手，
 * 以及 Trinkets 饰品栏。玩家不需要把东西专门塞进饰品槽。</p>
 */
public final class EquipUtil {

    private EquipUtil() {
    }

    /** 玩家是否持有（背包 / 快捷栏 / 盔甲槽 / 副手 / 饰品栏）。 */
    public static boolean carries(ServerPlayer player, Item item) {
        return !findStack(player, item).isEmpty();
    }

    /**
     * 找出玩家身上那件物品的<b>真实 ItemStack</b>（可直接写数据组件）。
     *
     * <p>先查 Trinkets 饰品栏，再扫背包 / 快捷栏 / 盔甲 / 副手。找不到返回
     * {@link ItemStack#EMPTY}。</p>
     */
    public static ItemStack findStack(ServerPlayer player, Item item) {
        if (TrinketsApi.getAttachment(player).isEquipped(item)) {
            Optional<TrinketSlotAccess> slot = TrinketsApi.getAttachment(player).findFirst(item);
            if (slot.isPresent()) {
                return slot.get().get();
            }
        }
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /** 玩家主手是否拿着该物品。 */
    public static boolean holds(ServerPlayer player, Item item) {
        return player.getMainHandItem().is(item);
    }

    /** 玩家某个装备槽是否穿着该物品。 */
    public static boolean wears(ServerPlayer player, EquipmentSlot slot, Item item) {
        return player.getItemBySlot(slot).is(item);
    }

    /** 是否穿齐四件套。 */
    public static boolean wearsFullSet(ServerPlayer player,
                                       Item helmet, Item chestplate, Item leggings, Item boots) {
        return wears(player, EquipmentSlot.HEAD, helmet)
                && wears(player, EquipmentSlot.CHEST, chestplate)
                && wears(player, EquipmentSlot.LEGS, leggings)
                && wears(player, EquipmentSlot.FEET, boots);
    }
}
