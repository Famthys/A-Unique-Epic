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

import java.util.Optional;

/**
 * 玛尔巴斯之噬的层数 HUD。
 *
 * <p>层数存在物品的数据组件里，而数据组件会随物品栏同步到客户端，所以这里能直接读到，
 * 不需要任何自定义网络包。</p>
 */
public final class MarbasHud {

    private MarbasHud() {
    }

    public static void register() {
        // 挂在快捷栏之后，位置就在快捷栏上方
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                AUniqueEpic.id("marbas_hud"),
                MarbasHud::extract);
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        // 刻意不检查"GUI 是否隐藏"和"是否开着界面"：这个元素是在原版 HUD 管线里跑的，
        // 原版自己会决定该不该画。26.2 里那两个字段也改名了，与其猜，不如交给管线。

        ItemStack fang = findFang(player);
        if (fang.isEmpty()) {
            return;
        }

        int stacks = fang.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        int max = EquipConfig.marbasMaxStacks;

        Component line = Component.translatable("hud.aunique-epic.marbas", stacks, max)
                .withStyle(stacks > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY);

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();

        // 居中、压在快捷栏正上方
        int textWidth = client.font.width(line);
        graphics.text(client.font, line, (screenWidth - textWidth) / 2, screenHeight - 55, 0xFFFFFFFF);
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
