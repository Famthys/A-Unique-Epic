package com.famthys.uniqueepic.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 噬心大剑：只负责把「饥渴」机制讲清楚。
 *
 * <p>为什么不在这里显示当前加成：那是玩家饱食度的函数，而 tooltip 拿不到玩家；
 * 去读客户端玩家又会让这个类在服务端崩掉。实时数值放在 HUD 上（见客户端 EquipmentHud）。</p>
 */
public class HungerGreatswordItem extends Item {

    private static final String KEY = "item.aunique-epic.heart_devourer.";

    public HungerGreatswordItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> collector, TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "hunger").withStyle(ChatFormatting.RED));
        collector.accept(Component.translatable(KEY + "hunger_full").withStyle(ChatFormatting.DARK_RED));
        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "hunger_hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
