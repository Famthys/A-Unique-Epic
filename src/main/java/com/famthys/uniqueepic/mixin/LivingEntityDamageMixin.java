package com.famthys.uniqueepic.mixin;

import com.famthys.uniqueepic.hero.HeroesExpeditionEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 勇者之征"重担"的受伤翻倍、"危机"的火焰加剧。
 *
 * <p>注入点选在 {@code actuallyHurt} 而不是 {@code hurtServer}：这个方法拿到的血量已经是
 * 扣完护甲和附魔之后的最终值，在这里翻倍就是字面意义上的"受到的伤害为双倍"。写在
 * {@code hurtServer} 里则会被后续的护甲计算按比例削弱，语义不对。</p>
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
    private float uniqueepic$heroesExpeditionIncomingDamage(float amount,
                                                            ServerLevel level,
                                                            DamageSource source) {
        if ((Object) this instanceof ServerPlayer player) {
            return HeroesExpeditionEffects.modifyIncomingDamage(player, source, amount);
        }
        return amount;
    }
}
