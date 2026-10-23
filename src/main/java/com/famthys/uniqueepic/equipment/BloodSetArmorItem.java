package com.famthys.uniqueepic.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 厄血套装的四个部件共用这个类，只为把「血债」机制讲清楚。
 */
public class BloodSetArmorItem extends Item {

    private static final String KEY = "item.aunique-epic.blood_set.";

    public BloodSetArmorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> collector, TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "set_bonus").withStyle(ChatFormatting.DARK_RED));
        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "debt").withStyle(ChatFormatting.RED));
        collector.accept(Component.translatable(KEY + "debt_pay").withStyle(ChatFormatting.RED));
        collector.accept(Component.translatable(KEY + "debt_settle").withStyle(ChatFormatting.DARK_RED));
        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
