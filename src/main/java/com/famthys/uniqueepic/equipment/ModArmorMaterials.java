package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.AUniqueEpic;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

/**
 * 两套盔甲的材料定义。
 *
 * <p><b>数值地板：对齐下界合金。</b>下界合金的基准是 护甲 3/8/6/3（合计 20）、耐久倍率 37、
 * 韧性 3.0、抗击退 0.1、附魔等级 15。本模组的两套都在此之上，不允许低于这个线。</p>
 *
 * <p>{@code ArmorMaterial} 在 26.2 里是<b>普通 record，不需要注册表</b>；真正走注册/加载的是
 * {@code EquipmentAsset}——它对应客户端资源 {@code assets/aunique-epic/equipment/<name>.json}，
 * 里面指定盔甲层贴图。</p>
 */
public final class ModArmorMaterials {

    private ModArmorMaterials() {
    }

    public static final ResourceKey<EquipmentAsset> KNIGHT_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, AUniqueEpic.id("knight"));
    public static final ResourceKey<EquipmentAsset> BLOOD_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, AUniqueEpic.id("blood"));

    /**
     * 圆桌骑士：防御向重甲。
     *
     * <p>合计护甲 23（下界合金 20），耐久 45（37），韧性 4.0（3.0），抗击退 0.15（0.1）。</p>
     */
    public static final ArmorMaterial KNIGHT = new ArmorMaterial(
            45,
            Map.of(
                    ArmorType.HELMET, 4,
                    ArmorType.CHESTPLATE, 9,
                    ArmorType.LEGGINGS, 6,
                    ArmorType.BOOTS, 4),
            18,
            SoundEvents.ARMOR_EQUIP_IRON,
            4.0F,
            0.15F,
            ItemTags.REPAIRS_IRON_ARMOR,
            KNIGHT_ASSET);

    /**
     * 厄血：攻击向重甲。
     *
     * <p>护甲正好压在下界合金线上（3/8/6/3 = 20），但耐久 40、韧性 3.5、附魔等级 20 都更高——
     * 它的强度主要来自套装效果，而不是堆防御。</p>
     */
    public static final ArmorMaterial BLOOD = new ArmorMaterial(
            40,
            Map.of(
                    ArmorType.HELMET, 3,
                    ArmorType.CHESTPLATE, 8,
                    ArmorType.LEGGINGS, 6,
                    ArmorType.BOOTS, 3),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.5F,
            0.10F,
            ItemTags.REPAIRS_NETHERITE_ARMOR,
            BLOOD_ASSET);
}
