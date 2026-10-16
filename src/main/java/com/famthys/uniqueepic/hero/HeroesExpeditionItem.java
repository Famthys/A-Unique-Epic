package com.famthys.uniqueepic.hero;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * 勇者之征的本体。
 *
 * <p>除了把四条效果写进 tooltip，它没有任何额外逻辑——效果全部由事件驱动，
 * 物品本身只是"凭证"。</p>
 *
 * <p>tooltip 刻意拆成"短名称 + 灰色短说明"两段并压短每行长度：中文一行太长会把
 * tooltip 撑到整个屏幕宽，很难看。</p>
 */
public class HeroesExpeditionItem extends Item {

    private static final String KEY = "item.aunique-epic.heroes_expedition.";

    public HeroesExpeditionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext context,
                                TooltipDisplay display,
                                Consumer<Component> collector,
                                TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "silence").withStyle(ChatFormatting.LIGHT_PURPLE));
        collector.accept(Component.empty());

        collector.accept(effectLine("resolve", ChatFormatting.GOLD));
        collector.accept(effectLine("unbroken", ChatFormatting.GOLD));
        collector.accept(effectLine("burden", ChatFormatting.RED));
        collector.accept(effectLine("crisis", ChatFormatting.RED));

        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "hint").withStyle(ChatFormatting.DARK_GRAY));
    }

    /** 一行效果：彩色短名 + 灰色短说明。 */
    private static Component effectLine(String id, ChatFormatting nameColor) {
        return Component.translatable(KEY + id + ".name").withStyle(nameColor)
                .append(Component.literal("  ").withStyle(ChatFormatting.DARK_GRAY))
                .append(Component.translatable(KEY + id + ".desc").withStyle(ChatFormatting.GRAY));
    }
}
