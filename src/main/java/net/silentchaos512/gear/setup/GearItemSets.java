package net.silentchaos512.gear.setup;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.item.gear.*;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.GearHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class GearItemSets {
    private static final List<GearItemSet<?>> LIST = new ArrayList<>();

    public static final GearItemSet<GearSwordItem> SWORD = set(GearTypes.SWORD, "sword_blade", GearSwordItem::new);
    public static final GearItemSet<GearSwordItem> KATANA = set(GearTypes.KATANA, "katana_blade", GearSwordItem::new);
    public static final GearItemSet<GearMacheteItem> MACHETE = set(GearTypes.MACHETE, "machete_blade", GearMacheteItem::new);
    public static final GearItemSet<GearDaggerItem> KNIFE = set(GearTypes.KNIFE, "knife_blade", GearDaggerItem::new);
    public static final GearItemSet<GearDaggerItem> DAGGER = set(GearTypes.DAGGER, "dagger_blade", GearDaggerItem::new);
    public static final GearItemSet<GearSpearItem> SPEAR = set(GearTypes.SPEAR, "spearhead", GearSpearItem::new,
            p -> GearHelper.applyBasicItemProperties(p)
                    .delayedHolderComponent(DataComponents.DAMAGE_TYPE, DamageTypes.SPEAR)
    );
    public static final GearItemSet<GearTridentItem> TRIDENT = set(GearTypes.TRIDENT, "trident_prongs", GearTridentItem::new);
    public static final GearItemSet<GearMaceItem> MACE = set(GearTypes.MACE, "mace_core", GearMaceItem::new);

    public static final GearItemSet<GearShieldItem> SHIELD = set(GearTypes.SHIELD, "shield_plate", GearShieldItem::new);

    public static final GearItemSet<GearBowItem> BOW = set(GearTypes.BOW, "bow_limbs", GearBowItem::new);
    public static final GearItemSet<GearCrossbowItem> CROSSBOW = set(GearTypes.CROSSBOW, "crossbow_limbs", GearCrossbowItem::new);
    public static final GearItemSet<GearSlingshotItem> SLINGSHOT = set(GearTypes.SLINGSHOT, "slingshot_limbs", GearSlingshotItem::new);

    public static final GearItemSet<GearArrowItem> ARROW = set(GearTypes.ARROW, "arrow_heads", GearArrowItem::new, p -> p.stacksTo(64));

    public static final GearItemSet<GearPickaxeItem> PICKAXE = set(GearTypes.PICKAXE, "pickaxe_head", GearPickaxeItem::new);
    public static final GearItemSet<GearShovelItem> SHOVEL = set(GearTypes.SHOVEL, "shovel_head", GearShovelItem::new);
    public static final GearItemSet<GearAxeItem> AXE = set(GearTypes.AXE, "axe_head", GearAxeItem::new);
    public static final GearItemSet<GearPaxelItem> PAXEL = set(GearTypes.PAXEL, "paxel_head", GearPaxelItem::new);
    public static final GearItemSet<GearHammerItem> HAMMER = set(GearTypes.HAMMER, "hammer_head", GearHammerItem::new);
    public static final GearItemSet<GearExcavatorItem> EXCAVATOR = set(GearTypes.EXCAVATOR, "excavator_head", GearExcavatorItem::new);
    public static final GearItemSet<GearSawItem> SAW = set(GearTypes.SAW, "saw_blade", GearSawItem::new);
    public static final GearItemSet<GearProspectorHammerItem> PROSPECTOR_HAMMER = set(GearTypes.PROSPECTOR_HAMMER, "prospector_hammer_head", GearProspectorHammerItem::new);

    public static final GearItemSet<GearHoeItem> HOE = set(GearTypes.HOE, "hoe_head", GearHoeItem::new);
    public static final GearItemSet<GearMattockItem> MATTOCK = set(GearTypes.MATTOCK, "mattock_head", GearMattockItem::new);
    public static final GearItemSet<GearSickleItem> SICKLE = set(GearTypes.SICKLE, "sickle_blade", GearSickleItem::new);
    public static final GearItemSet<GearShearsItem> SHEARS = set(GearTypes.SHEARS, "shear_blades", GearShearsItem::new);
    public static final GearItemSet<GearFishingRodItem> FISHING_ROD = set(GearTypes.FISHING_ROD, "fishing_reel_and_hook", GearFishingRodItem::new);

    public static final GearItemSet<GearArmorItem> HELMET = set(GearTypes.HELMET, "helmet_plates", (gt, props) -> new GearArmorItem(gt, ArmorType.HELMET, props));
    public static final GearItemSet<GearArmorItem> CHESTPLATE = set(GearTypes.CHESTPLATE, "chestplate_plates", (gt, props) -> new GearArmorItem(gt, ArmorType.CHESTPLATE, props));
    public static final GearItemSet<GearArmorItem> LEGGINGS = set(GearTypes.LEGGINGS, "legging_plates", (gt, props) -> new GearArmorItem(gt, ArmorType.LEGGINGS, props));
    public static final GearItemSet<GearArmorItem> BOOTS = set(GearTypes.BOOTS, "boot_plates", (gt, props) -> new GearArmorItem(gt, ArmorType.BOOTS, props));
    public static final GearItemSet<GearElytraItem> ELYTRA = set(GearTypes.ELYTRA, "elytra_wings", GearElytraItem::new);

    public static final GearItemSet<GearCurioItem> RING = set(GearTypes.RING, "ring_shank", (gt, props) -> new GearCurioItem(gt, "ring", props), GearItemSets::unstackableItemProperties);
    public static final GearItemSet<GearCurioItem> BRACELET = set(GearTypes.BRACELET, "bracelet_band", (gt, props) -> new GearCurioItem(gt, "bracelet", props), GearItemSets::unstackableItemProperties);
    public static final GearItemSet<GearCurioItem> NECKLACE = set(GearTypes.NECKLACE, "necklace_chain", (gt, props) -> new GearCurioItem(gt, "necklace", props), GearItemSets::unstackableItemProperties);

    private static <I extends Item & GearItem> GearItemSet<I> set(DeferredHolder<GearType, GearType> type, String partName, BiFunction<Supplier<GearType>, Item.Properties, I> itemFactory) {
        return set(type, partName, itemFactory, GearHelper::applyBasicItemProperties);
    }

    private static <I extends Item & GearItem> GearItemSet<I> set(DeferredHolder<GearType, GearType> type, String partName, BiFunction<Supplier<GearType>, Item.Properties, I> itemFactory, UnaryOperator<Item.Properties> gearItemProperties) {
        return set(new GearItemSet<>(type, partName, itemFactory, gearItemProperties));
    }

    private static <I extends Item & GearItem> GearItemSet<I> set(GearItemSet<I> set) {
        LIST.add(set);
        return set;
    }

    static void registerGearItems() {
        LIST.forEach(set -> set.registerGearItem(SgItems.ITEMS));
    }

    static void registerMainPartItems() {
        LIST.forEach(set -> set.registerMainPartItem(SgItems.ITEMS));
    }

    static void registerBlueprintItems() {
        LIST.forEach(set -> set.registerBlueprintItem(SgItems.ITEMS));
    }

    static void registerTemplateItems() {
        LIST.forEach(set -> set.registerTemplateItem(SgItems.ITEMS));
    }

    public static Iterator<GearItemSet<?>> getIterator() {
        return LIST.iterator();
    }

    private static Item.Properties unstackableItemProperties(Item.Properties properties) {
        return properties.stacksTo(1);
    }
}
