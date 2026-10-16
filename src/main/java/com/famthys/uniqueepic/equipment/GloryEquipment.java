package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 荣耀王系装备里需要持续结算的部分：圆桌骑士套装。
 *
 * <p>（石中剑的"王威"是右键触发，见 {@link SacredSwordItem}。）</p>
 */
public final class GloryEquipment {

    private GloryEquipment() {
    }

    private static final Identifier ROUND_TABLE_ARMOR_ID = AUniqueEpic.id("round_table_armor");

    /**
     * 圆桌骑士套装：穿齐四件后，周围每有一名友方生物，护甲 +10%，上限 +80%。
     *
     * <p>"友方"= 其他玩家 + 你自己驯服的宠物。</p>
     */
    public static void tickSet(ServerPlayer player) {
        boolean full = EquipUtil.wearsFullSet(player,
                ModEquipmentItems.ROUND_TABLE_HELMET,
                ModEquipmentItems.OATH_CHESTPLATE,
                ModEquipmentItems.GLORY_LEGGINGS,
                ModEquipmentItems.EXPEDITION_BOOTS);

        if (!full) {
            EquipAttributes.clear(player, Attributes.ARMOR, ROUND_TABLE_ARMOR_ID);
            return;
        }

        int allies = countAllies(player);
        double bonus = Math.min(allies * EquipConfig.roundTablePerAlly, EquipConfig.roundTableMax);
        EquipAttributes.set(player, Attributes.ARMOR, ROUND_TABLE_ARMOR_ID, bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }

    private static int countAllies(ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(EquipConfig.roundTableRadius);
        List<LivingEntity> nearby = player.level().getEntitiesOfClass(LivingEntity.class, box);

        int count = 0;
        for (LivingEntity candidate : nearby) {
            if (candidate == player || !candidate.isAlive()) {
                continue;
            }
            if (candidate instanceof Player) {
                count++;
                continue;
            }
            if (candidate instanceof TamableAnimal pet && pet.isTame() && pet.getOwner() == player) {
                count++;
            }
        }
        return count;
    }
}
