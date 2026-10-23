package com.famthys.uniqueepic.secondwave;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/**
 * 湖中剑·湖光。
 *
 * <p>长按右键进入吟唱——念完三段骑士王的事迹后，向前释放一道 3 格宽、64 格长的巨型光剑。</p>
 *
 * <p>中途松手就是中断，什么也不会发生（这是刻意的：长吟唱要有被打断的风险）。</p>
 */
public class LakeLightItem extends Item {

    private static final String KEY = "item.aunique-epic.lake_light.";

    /** 吟唱一共三行。 */
    private static final int CHANT_LINES = 3;

    public LakeLightItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return SecondWaveConfig.lakeLightChantTicks;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        // 举剑蓄力的姿势
        return ItemUseAnimation.SPEAR;
    }

    /**
     * 吟唱过程中每隔一段时间念一行。
     *
     * <p>用"已过去多久 / 间隔"算行号，所以每行只会念一次。</p>
     */
    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingTicks) {
        if (!(living instanceof ServerPlayer player)) {
            return;
        }

        // 吟唱期间钉在原地——这是代价，换来的是受伤减半（见 IncomingDamage）
        tickChant(player);

        int interval = SecondWaveConfig.lakeLightLineInterval;
        if (interval <= 0) {
            return;
        }
        int elapsed = SecondWaveConfig.lakeLightChantTicks - remainingTicks;
        if (elapsed < 0 || elapsed % interval != 0) {
            return;
        }
        int line = elapsed / interval;
        if (line < 0 || line >= CHANT_LINES) {
            return;
        }
        player.sendOverlayMessage(Component.translatable(KEY + "chant." + line)
                .withStyle(ChatFormatting.AQUA));
    }

    /* ==================== 吟唱状态 ==================== */

    private static final net.minecraft.resources.Identifier CHANT_ROOT_ID =
            com.famthys.uniqueepic.AUniqueEpic.id("lake_light_chant_root");

    /** 玩家是否正在吟唱（受伤减伤也靠这个判断，所以放在这里当唯一真相）。 */
    public static boolean isChanting(Player player) {
        return player.isUsingItem()
                && player.getUseItem().is(ModSecondWaveItems.LAKE_LIGHT);
    }

    /**
     * 每秒维护吟唱状态：吟唱时锁死移动，否则解除。
     *
     * <p>用"每秒重算"而不是"开始/结束钩子"：玩家中途死亡、掉线、被别的模组打断时钩子都可能不触发，
     * 重算永远是对的——否则玩家会被永久钉在原地。</p>
     */
    public static void tickChant(ServerPlayer player) {
        if (!SecondWaveConfig.lakeLightChantImmobile || !isChanting(player)) {
            com.famthys.uniqueepic.equipment.EquipAttributes.clear(
                    player, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                    CHANT_ROOT_ID);
            return;
        }
        com.famthys.uniqueepic.equipment.EquipAttributes.set(
                player, net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED,
                CHANT_ROOT_ID, -1.0D,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    /** 吟唱完成：释放光剑。 */
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (living instanceof ServerPlayer player) {
            LakeLightStrike.release(player, stack);
        }
        return stack;
    }

    /**
     * 提前松手：吟唱中断，无事发生。
     *
     * @return false —— 这次"使用"不算成功，物品不消耗也不进入冷却
     */
    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity living, int remainingTicks) {
        if (living instanceof ServerPlayer player
                && remainingTicks > 0
                && SecondWaveConfig.lakeLightChantTicks - remainingTicks > 5) {
            player.sendOverlayMessage(Component.translatable(KEY + "interrupted")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> collector, TooltipFlag flag) {
        collector.accept(Component.translatable(KEY + "desc").withStyle(ChatFormatting.AQUA));
        collector.accept(Component.translatable(KEY + "rate",
                        (int) SecondWaveConfig.lakeLightDamage)
                .withStyle(ChatFormatting.RED));
        collector.accept(Component.empty());
        collector.accept(Component.translatable(KEY + "dual").withStyle(ChatFormatting.GOLD));
        collector.accept(Component.translatable(KEY + "hint").withStyle(ChatFormatting.DARK_GRAY));
    }
}
