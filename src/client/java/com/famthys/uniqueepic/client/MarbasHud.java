package com.famthys.uniqueepic.client;

import com.famthys.uniqueepic.AUniqueEpic;
import com.famthys.uniqueepic.equipment.EquipConfig;
import com.famthys.uniqueepic.equipment.ModComponents;
import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import eu.pb4.trinkets.api.TrinketSlotAccess;
import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 装备状态 HUD：把"物品自己的资源"直接画在快捷栏上方。
 *
 * <p>这些数值都存在<b>物品的数据组件</b>里（层数、饱食度），而组件会随物品栏同步到客户端，
 * 所以这里能直接读，一行网络代码都不用写。噬心大剑的饥渴加成则读客户端玩家自己的饱食度。</p>
 */
public final class MarbasHud {

    private MarbasHud() {
    }

    public static void register() {
        // 挂在快捷栏之后，位置就在快捷栏上方
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                AUniqueEpic.id("equipment_hud"),
                MarbasHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        // 刻意不检查"GUI 是否隐藏"和"是否开着界面"：这个元素跑在原版 HUD 管线里，
        // 该不该画由原版决定。26.2 里那两个字段也改名了，与其猜不如交给管线。

        List<Component> lines = new ArrayList<>(2);

        // ---- 玛尔巴斯之噬：层数 + 饱食度 ----
        ItemStack fang = findFang(player);
        if (!fang.isEmpty()) {
            int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
            int satiety = fang.getOrDefault(ModComponents.MARBAS_SATIETY, 0);
            boolean canBlock = satiety >= EquipConfig.marbasSatietyBlockCost;
            lines.add(Component.translatable("hud.aunique-epic.marbas",
                            stacks, EquipConfig.marbasMaxStacks,
                            satiety, EquipConfig.marbasSatietyMax)
                    .withStyle(canBlock ? ChatFormatting.GOLD
                            : (satiety <= 0 ? ChatFormatting.DARK_RED : ChatFormatting.GRAY)));
        }

        // ---- 噬心大剑：当前饥渴加成 ----
        if (player.getMainHandItem().is(ModEquipmentItems.HEART_DEVOURER)) {
            int food = player.getFoodData().getFoodLevel();
            int percent;
            if (food >= 20) {
                percent = (int) Math.round(EquipConfig.hungerFullPenalty * 100);
            } else {
                double missing = (20 - food) / 2.0D;
                percent = (int) Math.round(missing * EquipConfig.hungerPerDrumstickBonus * 100);
            }
            lines.add(Component.translatable("hud.aunique-epic.hunger", percent)
                    .withStyle(percent >= 0 ? ChatFormatting.RED : ChatFormatting.DARK_RED));
        }

        if (lines.isEmpty()) {
            return;
        }

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // 居中、压在快捷栏正上方；多行往上堆
        int y = screenHeight - 55 - (lines.size() - 1) * 10;
        for (Component line : lines) {
            int textWidth = client.font.width(line);
            graphics.text(client.font, line, (screenWidth - textWidth) / 2, y, 0xFFFFFFFF);
            y += 10;
        }
    }

    /** 客户端侧找物品：只读背包与饰品栏，不做任何写入。 */
    private static ItemStack findFang(LocalPlayer player) {
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
}
