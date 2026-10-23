package com.famthys.uniqueepic.client.armor;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.entity.EquipmentSlot;

/** 26.2 官方命名；几何与 MC 玩家六个部件同名，供 Fabric 在提交时复制动作。 */
public final class BloodArmorModel extends Model<HumanoidRenderState> {
    public BloodArmorModel(ModelPart root) { super(root, RenderTypes::entityCutout); }

    @Override public void setupAnim(HumanoidRenderState state) {
        // 不重复计算动作：由 submitTransformCopyingModel 从实际玩家模型复制。
    }

    public static LayerDefinition createLayer(EquipmentSlot slot) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Map<String, PartDefinition> parts = new HashMap<>();
        parts.put("head", root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.00000F, 0.00000F, 0.00000F)));
        parts.put("body", root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.00000F, 0.00000F, 0.00000F)));
        parts.put("right_arm", root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.00000F, 2.00000F, 0.00000F)));
        parts.put("left_arm", root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.00000F, 2.00000F, 0.00000F)));
        parts.put("right_leg", root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.90000F, 12.00000F, 0.00000F)));
        parts.put("left_leg", root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.90000F, 12.00000F, 0.00000F)));

        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_0", 1, 1, -4.50000F, -8.50000F, -4.50000F, 9.00000F, 8.75000F, 9.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // helm_dark_shell
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_1", 38, 1, -3.50000F, -8.80000F, -4.85000F, 7.00000F, 1.80000F, 0.35000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // crown_plate
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_2", 54, 1, -1.00000F, -8.40000F, -5.05000F, 2.00000F, 3.00000F, 0.55000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // forehead_center
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_3", 61, 1, -4.35000F, -6.30000F, -5.10000F, 3.35000F, 1.10000F, 0.60000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // brow_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_4", 70, 1, -3.40000F, -4.90000F, -4.65000F, 2.40000F, 0.50000F, 0.14000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // eye_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_5", 77, 1, -4.50000F, -3.80000F, -4.90000F, 3.10000F, 2.80000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // cheek_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_6", 86, 1, -4.90000F, -7.50000F, -2.80000F, 1.60000F, 5.50000F, 5.30000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // temple_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_7", 101, 1, -3.50000F, -2.70000F, -5.40000F, 1.00000F, 2.20000F, 0.70000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // tusk_base_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_8", 106, 1, -3.25000F, -4.00000F, -5.35000F, 0.50000F, 1.30000F, 0.50000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // tusk_tip_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_9", 109, 1, -4.50000F, -9.70000F, -1.00000F, 1.00000F, 1.70000F, 2.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // crown_spike_base_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_10", 116, 1, -4.25000F, -10.50000F, -0.50000F, 0.50000F, 0.80000F, 1.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // crown_spike_tip_-1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_11", 120, 1, 1.00000F, -6.30000F, -5.10000F, 3.35000F, 1.10000F, 0.60000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // brow_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_12", 129, 1, 1.00000F, -4.90000F, -4.65000F, 2.40000F, 0.50000F, 0.14000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // eye_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_13", 136, 1, 1.40000F, -3.80000F, -4.90000F, 3.10000F, 2.80000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // cheek_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_14", 145, 1, 3.30000F, -7.50000F, -2.80000F, 1.60000F, 5.50000F, 5.30000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // temple_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_15", 160, 1, 2.50000F, -2.70000F, -5.40000F, 1.00000F, 2.20000F, 0.70000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // tusk_base_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_16", 165, 1, 2.75000F, -4.00000F, -5.35000F, 0.50000F, 1.30000F, 0.50000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // tusk_tip_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_17", 168, 1, 3.50000F, -9.70000F, -1.00000F, 1.00000F, 1.70000F, 2.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // crown_spike_base_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_18", 175, 1, 3.75000F, -10.50000F, -0.50000F, 0.50000F, 0.80000F, 1.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // crown_spike_tip_1
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_19", 179, 1, -0.65000F, -5.50000F, -5.30000F, 1.30000F, 4.20000F, 0.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // nose_ridge
        if (slot == EquipmentSlot.HEAD) add(parts.get("head"), "cube_20", 185, 1, -2.50000F, -1.50000F, -4.95000F, 5.00000F, 1.50000F, 0.45000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // chin_plate
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_21", 197, 1, -4.40000F, 0.20000F, -2.45000F, 8.80000F, 11.70000F, 4.90000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // torso_core
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_22", 226, 1, -3.80000F, 1.00000F, -2.85000F, 7.60000F, 8.00000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // flesh_front
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_23", 243, 1, -0.50000F, 1.70000F, -3.55000F, 1.00000F, 6.90000F, 0.70000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // sternum
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_24", 248, 1, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, -1.95000F, 6.67500F, -3.15000F, 0.00000F, 0.00000F, 0.20944F); // rib_0_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_25", 1, 20, -3.65000F, 5.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_0_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_26", 5, 20, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, 1.95000F, 6.67500F, -3.15000F, 0.00000F, 0.00000F, -0.20944F); // rib_0_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_27", 13, 20, 3.00000F, 5.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_0_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_28", 17, 20, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, -1.95000F, 4.67500F, -3.15000F, 0.00000F, 0.00000F, 0.20944F); // rib_1_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_29", 25, 20, -3.65000F, 3.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_1_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_30", 29, 20, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, 1.95000F, 4.67500F, -3.15000F, 0.00000F, 0.00000F, -0.20944F); // rib_1_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_31", 37, 20, 3.00000F, 3.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_1_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_32", 41, 20, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, -1.95000F, 2.67500F, -3.15000F, 0.00000F, 0.00000F, 0.20944F); // rib_2_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_33", 49, 20, -3.65000F, 1.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_2_-1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_34", 53, 20, -1.30000F, -0.32500F, -0.30000F, 2.60000F, 0.65000F, 0.60000F, 1.95000F, 2.67500F, -3.15000F, 0.00000F, 0.00000F, -0.20944F); // rib_2_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_35", 61, 20, 3.00000F, 1.90000F, -3.15000F, 0.65000F, 0.90000F, 0.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // rib_end_2_1
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_36", 65, 20, -3.00000F, 0.00000F, -3.10000F, 6.00000F, 1.00000F, 0.75000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // collar_front
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_37", 80, 20, -3.80000F, 1.00000F, 2.45000F, 7.60000F, 9.00000F, 0.55000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // back_plate
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_38", 98, 20, -0.70000F, 8.00000F, 3.00000F, 1.40000F, 1.00000F, 0.65000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // spine_15
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_39", 104, 20, -0.70000F, 6.00000F, 3.00000F, 1.40000F, 1.00000F, 0.65000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // spine_17
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_40", 110, 20, -0.70000F, 4.00000F, 3.00000F, 1.40000F, 1.00000F, 0.65000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // spine_19
        if (slot == EquipmentSlot.CHEST) add(parts.get("body"), "cube_41", 116, 20, -0.70000F, 2.00000F, 3.00000F, 1.40000F, 1.00000F, 0.65000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // spine_21
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_42", 122, 20, -3.60000F, -1.00000F, -2.40000F, 4.30000F, 10.70000F, 4.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // arm_shell_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_43", 142, 20, -4.00000F, -2.20000F, -2.90000F, 4.50000F, 3.20000F, 5.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // pauldron_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_44", 164, 20, -3.10000F, -3.70000F, -0.70000F, 1.70000F, 1.50000F, 1.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_spike_base_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_45", 172, 20, -2.60000F, -5.00000F, -0.40000F, 0.80000F, 1.30000F, 0.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_spike_tip_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_46", 177, 20, -5.00000F, -1.40000F, -0.60000F, 1.30000F, 1.20000F, 1.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_outer_spike_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_47", 184, 20, -3.80000F, 5.00000F, -2.75000F, 4.30000F, 4.00000F, 0.35000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // bracer_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_48", 195, 20, -3.90000F, 8.90000F, -2.50000F, 4.50000F, 0.70000F, 5.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // cuff_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("right_arm"), "cube_49", 215, 20, -1.85000F, 5.80000F, -2.90000F, 0.40000F, 2.20000F, 0.15000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // vein_right_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_50", 218, 20, -0.70000F, -1.00000F, -2.40000F, 4.30000F, 10.70000F, 4.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // arm_shell_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_51", 1, 37, -0.50000F, -2.20000F, -2.90000F, 4.50000F, 3.20000F, 5.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // pauldron_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_52", 23, 37, 1.40000F, -3.70000F, -0.70000F, 1.70000F, 1.50000F, 1.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_spike_base_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_53", 31, 37, 1.80000F, -5.00000F, -0.40000F, 0.80000F, 1.30000F, 0.80000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_spike_tip_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_54", 36, 37, 3.70000F, -1.40000F, -0.60000F, 1.30000F, 1.20000F, 1.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // shoulder_outer_spike_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_55", 43, 37, -0.50000F, 5.00000F, -2.75000F, 4.30000F, 4.00000F, 0.35000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // bracer_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_56", 54, 37, -0.60000F, 8.90000F, -2.50000F, 4.50000F, 0.70000F, 5.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // cuff_left_arm
        if (slot == EquipmentSlot.CHEST) add(parts.get("left_arm"), "cube_57", 74, 37, 1.45000F, 5.80000F, -2.90000F, 0.40000F, 2.20000F, 0.15000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // vein_left_arm
        if (slot == EquipmentSlot.LEGS) add(parts.get("body"), "cube_58", 77, 37, -4.50000F, 10.50000F, -2.70000F, 9.00000F, 1.50000F, 5.40000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // belt
        if (slot == EquipmentSlot.LEGS) add(parts.get("body"), "cube_59", 107, 37, -1.00000F, 10.50000F, -3.00000F, 2.00000F, 1.50000F, 0.30000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // belt_buckle
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_60", 113, 37, -2.35000F, 0.00000F, -2.25000F, 4.05000F, 10.70000F, 4.50000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // leg_under_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_61", 132, 37, -2.40000F, 0.30000F, -2.65000F, 3.95000F, 4.00000F, 5.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // thigh_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_62", 151, 37, -2.30000F, 4.30000F, -3.00000F, 3.70000F, 2.00000F, 0.75000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // knee_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_63", 161, 37, -1.10000F, 4.70000F, -3.35000F, 1.60000F, 1.30000F, 0.35000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // kneecap_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_64", 166, 37, -3.35000F, 1.80000F, -0.60000F, 1.00000F, 1.20000F, 1.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // thigh_spike_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("right_leg"), "cube_65", 172, 37, -2.35000F, 3.35000F, -2.85000F, 3.90000F, 0.55000F, 0.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // torn_strap_right_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("right_leg"), "cube_66", 182, 37, -2.50000F, 6.80000F, -2.65000F, 4.15000F, 5.20000F, 5.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_right_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("right_leg"), "cube_67", 202, 37, -2.40000F, 6.60000F, -2.95000F, 3.95000F, 3.40000F, 0.30000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_shin_right_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("right_leg"), "cube_68", 212, 37, -2.50000F, 10.40000F, -3.70000F, 4.10000F, 1.60000F, 1.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_toe_right_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("right_leg"), "cube_69", 224, 37, -2.60000F, 11.50000F, -3.75000F, 4.30000F, 0.50000F, 6.25000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // sole_right_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("right_leg"), "cube_70", 247, 37, -0.25000F, 6.85000F, -3.00000F, 0.45000F, 2.45000F, 0.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_crack_right_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_71", 1, 54, -1.70000F, 0.00000F, -2.25000F, 4.05000F, 10.70000F, 4.50000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // leg_under_left_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_72", 20, 54, -1.55000F, 0.30000F, -2.65000F, 3.95000F, 4.00000F, 5.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // thigh_left_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_73", 39, 54, -1.40000F, 4.30000F, -3.00000F, 3.70000F, 2.00000F, 0.75000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // knee_left_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_74", 49, 54, -0.50000F, 4.70000F, -3.35000F, 1.60000F, 1.30000F, 0.35000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // kneecap_left_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_75", 54, 54, 2.35000F, 1.80000F, -0.60000F, 1.00000F, 1.20000F, 1.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // thigh_spike_left_leg
        if (slot == EquipmentSlot.LEGS) add(parts.get("left_leg"), "cube_76", 60, 54, -1.55000F, 3.35000F, -2.85000F, 3.90000F, 0.55000F, 0.20000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // torn_strap_left_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("left_leg"), "cube_77", 70, 54, -1.65000F, 6.80000F, -2.65000F, 4.15000F, 5.20000F, 5.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_left_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("left_leg"), "cube_78", 90, 54, -1.55000F, 6.60000F, -2.95000F, 3.95000F, 3.40000F, 0.30000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_shin_left_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("left_leg"), "cube_79", 100, 54, -1.60000F, 10.40000F, -3.70000F, 4.10000F, 1.60000F, 1.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_toe_left_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("left_leg"), "cube_80", 112, 54, -1.70000F, 11.50000F, -3.75000F, 4.30000F, 0.50000F, 6.25000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // sole_left_leg
        if (slot == EquipmentSlot.FEET) add(parts.get("left_leg"), "cube_81", 135, 54, -0.20000F, 6.85000F, -3.00000F, 0.45000F, 2.45000F, 0.05000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F, 0.00000F); // boot_crack_left_leg
        return LayerDefinition.create(mesh, 256, 256);
    }

    private static void add(PartDefinition parent, String name, int u, int v,
            float x, float y, float z, float w, float h, float d,
            float px, float py, float pz, float rx, float ry, float rz) {
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(u, v).addBox(x, y, z, w, h, d),
                PartPose.offsetAndRotation(px, py, pz, rx, ry, rz));
    }
}
