package com.famthys.uniqueepic.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** 只负责穿戴几何；物品本身仍由原模组注册并设置可装备槽位。 */
public final class BloodArmorRenderer implements ArmorRenderer {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
            "aunique-epic", "textures/entity/equipment/blood_armor.png");
    private final Map<EquipmentSlot, BloodArmorModel> models = new EnumMap<>(EquipmentSlot.class);

    public BloodArmorRenderer() {
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            models.put(slot, new BloodArmorModel(BloodArmorModel.createLayer(slot).bakeRoot()));
        }
    }

    @Override
    public void render(PoseStack matrices, SubmitNodeCollector collector, ItemStack stack,
            HumanoidRenderState state, EquipmentSlot slot, int light,
            HumanoidModel<HumanoidRenderState> contextModel) {
        BloodArmorModel model = models.get(slot);
        if (model == null) return;
        // 让 Fabric 在真正提交模型时复制姿态，避免延迟提交阶段动作被别的实体覆盖。
        ArmorRenderer.submitTransformCopyingModel(contextModel, state, model, state, false,
                collector.order(0), matrices, RenderTypes.entityCutout(TEXTURE),
                light, OverlayTexture.NO_OVERLAY, 0, null);
    }
}
