package com.famthys.uniqueepic.mixin;

import com.famthys.uniqueepic.equipment.IncomingDamage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 玩家承受伤害的统一注入点。
 *
 * <p>注入在 {@code actuallyHurt}：这里拿到的血量已经是扣完护甲和附魔之后的最终值，
 * 所以"翻倍""减半""记成血债"都是字面意义上对<b>最终伤害</b>操作。写在 {@code hurtServer}
 * 会被后续护甲计算按比例削弱，语义不对。</p>
 *
 * <p>具体规则全部交给 {@link IncomingDamage}，mixin 只负责挂钩子。</p>
 *
 * <p>方法描述符写全，避免和同名重载撞车。</p>
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageMixin {

    @ModifyVariable(
            method = "actuallyHurt(Lnet/minecraft/server/level/ServerLevel;"
                    + "Lnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private float uniqueepic$modifyIncomingDamage(float amount,
                                                  ServerLevel level,
                                                  DamageSource source) {
        if ((Object) this instanceof ServerPlayer player) {
            return IncomingDamage.modify(player, source, amount);
        }
        return amount;
    }
}
