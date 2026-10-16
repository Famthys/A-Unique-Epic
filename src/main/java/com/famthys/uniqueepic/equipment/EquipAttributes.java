package com.famthys.uniqueepic.equipment;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/**
 * 通用属性修饰符工具。
 *
 * <p>"每秒重算 + 固定 id 增删"这套写法在勇者之征上验证过了：不依赖装备/卸下回调，
 * 玩家死亡、掉线、物品被别的模组拿走都不会留下脏修饰符。</p>
 */
public final class EquipAttributes {

    private EquipAttributes() {
    }

    /**
     * 把某个属性修饰符设成指定数值——没有就加，数值变了就换，一致就什么都不做。
     */
    public static void set(LivingEntity entity, Holder<Attribute> attribute, Identifier id,
                           double amount, AttributeModifier.Operation operation) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier existing = instance.getModifier(id);
        if (existing == null) {
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
            return;
        }
        if (Double.compare(existing.amount(), amount) != 0) {
            instance.removeModifier(id);
            instance.addTransientModifier(new AttributeModifier(id, amount, operation));
        }
    }

    /** 移除修饰符（不存在就什么都不做）。 */
    public static void clear(LivingEntity entity, Holder<Attribute> attribute, Identifier id) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && instance.getModifier(id) != null) {
            instance.removeModifier(id);
        }
    }
}
