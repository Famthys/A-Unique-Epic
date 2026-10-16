package com.famthys.uniqueepic.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 玛尔巴斯之噬。
 *
 * <p>tooltip 会实时显示当前层数与攻击力加成——层数存在物品的数据组件里，
 * 客户端本来就有这份数据，所以不需要额外同步。</p>
 */
public class MarbasFangItem extends Item {

    private static final String KEY = "item.aunique-epic.marbas_fang.";

    public MarbasFangItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> collector,
                                TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "desc").withStyle(ChatFormatting.GRAY));

        int stacks = stack.getOrDefault(ModComponents.MARBAS_STACKS, 0);
        int bonus = (int) Math.round(stacks * EquipConfig.marbasPerStack * 100);

        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "stacks", stacks, EquipConfig.marbasMaxStacks)
                .withStyle(stacks > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));
        collector.accept(Component.translatable(KEY + "bonus", bonus)
                .withStyle(stacks > 0 ? ChatFormatting.GOLD : ChatFormatting.DARK_GRAY));

        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
