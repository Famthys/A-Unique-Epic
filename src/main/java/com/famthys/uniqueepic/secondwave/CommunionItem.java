package com.famthys.uniqueepic.secondwave;

import com.famthys.uniqueepic.equipment.ModComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 噬心大剑·共噬。
 *
 * <p>效果全部由事件驱动（见 {@link BloodBurst} 与 {@link SecondWaveEntry}），物品本身只负责 tooltip。</p>
 */
public class CommunionItem extends Item {

    private static final String KEY = "item.aunique-epic.heart_devourer_communion.";

    public CommunionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> collector, TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "burst").withStyle(ChatFormatting.RED));
        collector.accept(Component.translatable(KEY + "revive").withStyle(ChatFormatting.GOLD));
        collector.accept(Component.empty());

        // 组件里存的是"剩余冷却 tick"，服务端每秒递减，所以这里显示的是真实剩余时间
        int remainingTicks = stack.getOrDefault(ModComponents.COMMUNION_COOLDOWN, 0);
        if (remainingTicks <= 0) {
            collector.accept(Component.translatable(KEY + "ready").withStyle(ChatFormatting.GREEN));
        } else {
            collector.accept(Component.translatable(KEY + "cooling", remainingTicks / 20)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
