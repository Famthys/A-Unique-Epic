package com.famthys.uniqueepic.block;

import com.famthys.uniqueepic.equipment.ModEquipmentItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 石中剑（封印）—— 剑插在石头里，右键拔剑。
 *
 * <p>拔完之后方块<b>不消失</b>，只是换成"空石座"的外观（{@code sealed=false}）。
 * 这样石座留在原地，符合"剑被拔走、石头还在"的画面；也避免出现"点一下方块没了"的突兀感。</p>
 */
public class SwordInStoneBlock extends Block {

    /** 剑是否还在石头里。 */
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");

    /**
     * 碰撞/选取形状：只取石头基座那一段。
     *
     * <p>模型里剑身一直伸到 21.5 格高，如果按整个模型取形状，玩家会撞到空气。
     * 这里按石头实际范围框（x 1~15、z 2~14、y 0~6）来设。</p>
     */
    private static final VoxelShape BASE_SHAPE = Shapes.box(
            1.0 / 16.0, 0.0, 2.0 / 16.0,
            15.0 / 16.0, 6.0 / 16.0, 14.0 / 16.0);

    public SwordInStoneBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any().setValue(SEALED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SEALED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return BASE_SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!(level instanceof net.minecraft.server.level.ServerLevel server)) {
            // 客户端：只负责摆手，实际逻辑在服务端
            return InteractionResult.SUCCESS;
        }

        // 剑已经被拔走了
        if (!state.getValue(SEALED)) {
            server.playSound(null, pos, SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.5F, 1.3F);
            player.sendOverlayMessage(Component.translatable(
                    "message.aunique-epic.sword_already_taken").withStyle(ChatFormatting.DARK_GRAY));
            return InteractionResult.SUCCESS;
        }

        // 拔剑：给物品，方块换成空石座
        ItemStack sword = new ItemStack(ModEquipmentItems.SWORD_IN_STONE);
        if (!player.getInventory().add(sword)) {
            player.drop(sword, false);       // 背包满了就掉在脚边，不能凭空吞掉
        }
        level.setBlockAndUpdate(pos, state.setValue(SEALED, false));

        server.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.4F);
        server.playSound(null, pos, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 0.8F, 0.9F);
        player.sendSystemMessage(Component.translatable("message.aunique-epic.sword_drawn")
                .withStyle(ChatFormatting.GOLD));
        return InteractionResult.SUCCESS;
    }
}
