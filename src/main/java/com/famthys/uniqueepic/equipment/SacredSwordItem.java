package com.famthys.uniqueepic.equipment;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 石中剑。
 *
 * <p>基础攻击力高于下界合金剑（{@code ModEquipmentItems} 里给的是 +6，下界合金剑是 +3），
 * 右键释放"王威"震慑周围敌人。</p>
 */
public class SacredSwordItem extends Item {

    public SacredSwordItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            // 客户端只负责播动作，真正的效果由服务端结算
            return InteractionResult.SUCCESS;
        }

        AABB box = player.getBoundingBox().inflate(EquipConfig.sacredSwordRadius);
        List<Monster> targets = level.getEntitiesOfClass(Monster.class, box, Monster::isAlive);

        for (Monster monster : targets) {
            monster.addEffect(new MobEffectInstance(
                    MobEffects.SLOWNESS, EquipConfig.sacredSwordDuration, 1, false, true, true));
            monster.addEffect(new MobEffectInstance(
                    MobEffects.WEAKNESS, EquipConfig.sacredSwordDuration, 0, false, true, true));
        }

        player.getCooldowns().addCooldown(stack, EquipConfig.sacredSwordCooldown);
        player.sendSystemMessage(Component.translatable(
                        "item.aunique-epic.sword_in_stone.kings_awe", targets.size())
                .withStyle(ChatFormatting.GOLD));

        return InteractionResult.SUCCESS;
    }
}
