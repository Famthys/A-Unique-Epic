package com.famthys.uniqueepic.equipment;

import com.famthys.uniqueepic.item.ModItemIds;
import com.famthys.uniqueepic.item.ModItems;
import eu.pb4.trinkets.api.DefaultTrinketSlots;
import eu.pb4.trinkets.api.component.TrinketDataComponents;
import eu.pb4.trinkets.api.component.TrinketEquippable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

/**
 * 第一波 11 件装备的注册。
 *
 * <p>武器用 {@code ToolMaterial.NETHERITE} 打底（耐久与附魔等级都在下界合金线上），
 * 再做攻击力加成；盔甲用 {@link ModArmorMaterials} 里那两套材料。</p>
 */
public final class ModEquipmentItems {

    private ModEquipmentItems() {
    }

    /* ==================== 一、噬心大剑（厄血） ==================== */

    public static final Item HEART_DEVOURER =
            registerSword("heart_devourer", 7.0F, -3.0F);

    /* ==================== 二、石中剑（荣耀王） ==================== */

    public static final Item SWORD_IN_STONE;
    public static final ResourceKey<Item> SWORD_IN_STONE_KEY = ModItemIds.create("sword_in_stone");

    /* ==================== 三、圆桌骑士套装（荣耀王） ==================== */

    public static final Item ROUND_TABLE_HELMET =
            registerArmor("round_table_helmet", ArmorType.HELMET, ModArmorMaterials.KNIGHT);
    public static final Item OATH_CHESTPLATE =
            registerArmor("oath_chestplate", ArmorType.CHESTPLATE, ModArmorMaterials.KNIGHT);
    public static final Item GLORY_LEGGINGS =
            registerArmor("glory_leggings", ArmorType.LEGGINGS, ModArmorMaterials.KNIGHT);
    public static final Item EXPEDITION_BOOTS =
            registerArmor("expedition_boots", ArmorType.BOOTS, ModArmorMaterials.KNIGHT);

    /* ==================== 四、厄血套装（厄血） ==================== */

    public static final Item FEROCIOUS_MASK =
            registerArmor("ferocious_mask", ArmorType.HELMET, ModArmorMaterials.BLOOD);
    public static final Item MUTATED_CHESTPLATE =
            registerArmor("mutated_chestplate", ArmorType.CHESTPLATE, ModArmorMaterials.BLOOD);
    public static final Item RAMPAGE_LEGGINGS =
            registerArmor("rampage_leggings", ArmorType.LEGGINGS, ModArmorMaterials.BLOOD);
    public static final Item SHATTERED_BOOTS =
            registerArmor("shattered_boots", ArmorType.BOOTS, ModArmorMaterials.BLOOD);

    /* ==================== 五、玛尔巴斯之噬（护符） ==================== */

    public static final Item MARBAS_FANG = registerNecklace("marbas_fang");

    static {
        SWORD_IN_STONE = ModItems.register(
                SWORD_IN_STONE_KEY,
                SacredSwordItem::new,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .sword(ToolMaterial.NETHERITE, 6.0F, -2.4F));
    }

    public static void init() {
    }

    /* ==================== 注册工具 ==================== */

    private static Item registerSword(String name, float attackDamage, float attackSpeed) {
        ResourceKey<Item> key = ModItemIds.create(name);
        return ModItems.register(key, Item::new,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .sword(ToolMaterial.NETHERITE, attackDamage, attackSpeed));
    }

    private static Item registerArmor(String name, ArmorType type, ArmorMaterial material) {
        ResourceKey<Item> key = ModItemIds.create(name);
        return ModItems.register(key, Item::new,
                new Item.Properties()
                        .rarity(Rarity.RARE)
                        .humanoidArmor(material, type));
    }

    /** 护符：走 Trinkets 项链栏，同时右键可直接戴上。 */
    private static Item registerNecklace(String name) {
        ResourceKey<Item> key = ModItemIds.create(name);
        return ModItems.register(key, Item::new,
                new Item.Properties()
                        .stacksTo(1)
                        .rarity(Rarity.RARE)
                        .component(
                                TrinketDataComponents.EQUIPMENT,
                                TrinketEquippable.DEFAULT
                                        .withSlots(DefaultTrinketSlots.CHEST_NECKLACE)
                                        .withEquipOnInteract(true)
                                        .withSwappable(true)));
    }
}
