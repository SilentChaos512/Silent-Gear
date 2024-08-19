package net.silentchaos512.gear.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.block.compounder.AlloyMakerInfo;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;
import net.silentchaos512.gear.gear.material.MaterialCategories;
import net.silentchaos512.gear.gear.trait.Trait;
import net.silentchaos512.gear.setup.*;

public final class Const {
    // Model loaders
    public static final ResourceLocation COMPOUND_PART_MODEL_LOADER = modId("compound_part_model");
    public static final ResourceLocation FRAGMENT_MODEL_LOADER = modId("fragment_model");
    public static final ResourceLocation GEAR_MODEL_LOADER = modId("gear_model");

    // Model properties
    public static final ResourceLocation BROKEN_PROPERTY = modId("broken");
    public static final ModResourceLocation MODEL = SilentGear.getId("model");

    // Recipe types and categories
    public static final ResourceLocation COMBINE_FRAGMENTS = modId("combine_fragments");
    public static final ResourceLocation COMPOUND_PART = modId("compound_part");
    public static final ResourceLocation COMPOUNDING = modId("compounding");
    public static final ResourceLocation COMPOUNDING_FABRIC = modId("compounding/fabric");
    public static final ResourceLocation COMPOUNDING_GEM = modId("compounding/gem");
    public static final ResourceLocation COMPOUNDING_METAL = modId("compounding/metal");
    public static final ResourceLocation CONVERSION = modId("conversion");
    public static final ResourceLocation CRAFTING_SPECIAL_REPAIRITEM = modId("crafting_special_repairitem");
    public static final ResourceLocation DAMAGE_ITEM = modId("damage_item");
    public static final ResourceLocation FILL_REPAIR_KIT = modId("fill_repair_kit");
    public static final ResourceLocation GRADING = modId("grading");
    public static final ResourceLocation MOD_KIT_REMOVE_PART = modId("mod_kit_remove_part");
    public static final ResourceLocation PRESSING = modId("pressing");
    public static final ResourceLocation PRESSING_MATERIAL = modId("pressing/material");
    public static final ResourceLocation QUICK_REPAIR = modId("quick_repair");
    public static final ResourceLocation SALVAGING = modId("salvaging");
    public static final ResourceLocation SALVAGING_COMPOUND_PART = modId("salvaging/compound_part");
    public static final ResourceLocation SALVAGING_GEAR = modId("salvaging/gear");
    public static final ResourceLocation SHAPED_GEAR_CRAFTING = modId("shaped_gear_crafting");
    public static final ResourceLocation SHAPELESS_GEAR_CRAFTING = modId("gear_crafting");
    public static final ResourceLocation SMITHING_COATING = modId("smithing/coating");
    public static final ResourceLocation SMITHING_UPGRADE = modId("smithing/upgrade");
    public static final ResourceLocation SWAP_GEAR_PART = modId("swap_gear_part");
    public static final ResourceLocation TOOL_ACTION = modId("tool_action");

    // Mod IDs
    public static final String CAELUS = "caelus";
    public static final String CURIOS = "curios";

    // Random
    public static final ResourceLocation NULL_ID = new ResourceLocation("null");

    // Material Modifiers
    public static final ModResourceLocation GRADE = SilentGear.getId("grade");
    public static final ModResourceLocation STARCHARGED = SilentGear.getId("starcharged");
    public static final String NBT_IS_FOIL = "SG_IsFoil";

    // Compound-crafting block info
    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final AlloyMakerInfo<MetalAlloyRecipe> METAL_COMPOUNDER_INFO = new AlloyMakerInfo<>(
            ImmutableList.of(
                    MaterialCategories.METAL,
                    MaterialCategories.DUST
            ),
            4,
            () -> SgItems.ALLOY_INGOT.get(),
            () -> SgBlocks.ALLOY_FORGE.get(),
            () -> SgBlockEntities.ALLOY_FORGE.get(),
            () -> SgMenuTypes.METAL_ALLOYER.get(),
            () -> SgRecipes.COMPOUNDING_METAL.get(),
            () -> SgRecipes.COMPOUNDING_METAL_TYPE.get(),
            MetalAlloyRecipe.class);

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final AlloyMakerInfo<GemAlloyRecipe> GEM_COMPOUNDER_INFO = new AlloyMakerInfo<>(
            ImmutableList.of(
                    MaterialCategories.GEM,
                    MaterialCategories.DUST
            ),
            4,
            () -> SgItems.HYBRID_GEM.get(),
            () -> SgBlocks.RECRYSTALLIZER.get(),
            () -> SgBlockEntities.RECRYSTALLIZER.get(),
            () -> SgMenuTypes.RECRYSTALLIZER.get(),
            () -> SgRecipes.COMPOUNDING_GEM.get(),
            () -> SgRecipes.COMPOUNDING_GEM_TYPE.get(),
            GemAlloyRecipe.class);

    @SuppressWarnings({"Convert2MethodRef", "FunctionalExpressionCanBeFolded"})
    public static final AlloyMakerInfo<FabricAlloyRecipe> FABRIC_COMPOUNDER_INFO = new AlloyMakerInfo<>(
            ImmutableList.of(
                    MaterialCategories.CLOTH,
                    MaterialCategories.FIBER,
                    MaterialCategories.SLIME
            ),
            4,
            () -> SgItems.MIXED_FABRIC.get(),
            () -> SgBlocks.REFABRICATOR.get(),
            () -> SgBlockEntities.REFABRICATOR.get(),
            () -> SgMenuTypes.REFABRICATOR.get(),
            () -> SgRecipes.COMPOUNDING_FABRIC.get(),
            () -> SgRecipes.COMPOUNDING_FABRIC_TYPE.get(),
            FabricAlloyRecipe.class);

    private Const() {}

    public static final class Materials {
        public static final DataResource<Material> AZURE_ELECTRUM = DataResource.material("azure_electrum");
        public static final DataResource<Material> AZURE_SILVER = DataResource.material("azure_silver");
        public static final DataResource<Material> BLAZE_GOLD = DataResource.material("blaze_gold");
        public static final DataResource<Material> CRIMSON_STEEL = DataResource.material("crimson_steel");
        public static final DataResource<Material> DIAMOND = DataResource.material("diamond");
        public static final DataResource<Material> DIMERALD = DataResource.material("dimerald");
        public static final DataResource<Material> EMERALD = DataResource.material("emerald");
        public static final DataResource<Material> EMPTY = DataResource.material("empty");
        public static final DataResource<Material> EXAMPLE = DataResource.material("example");
        public static final DataResource<Material> FEATHER = DataResource.material("feather");
        public static final DataResource<Material> GOLD = DataResource.material("gold");
        public static final DataResource<Material> LEATHER = DataResource.material("leather");
        public static final DataResource<Material> NETHERITE = DataResource.material("netherite");
        public static final DataResource<Material> IRON = DataResource.material("iron");
        public static final DataResource<Material> STONE = DataResource.material("stone");
        public static final DataResource<Material> STRING = DataResource.material("string");
        public static final DataResource<Material> TYRIAN_STEEL = DataResource.material("tyrian_steel");

        public static final DataResource<Material> WOOD = DataResource.material("wood");
        public static final DataResource<Material> WOOD_ROUGH = DataResource.material("wood/rough");
        public static final DataResource<Material> WOOL = DataResource.material("wool");
        public static final DataResource<Material> WOOL_BLACK = DataResource.material("wool/black");

        private Materials() {}

    }

    public static final class Parts {
        public static final DataResource<GearPart> ARMOR_BODY = DataResource.part("armor_body");
        public static final DataResource<GearPart> AXE_HEAD = DataResource.part("axe_head");
        public static final DataResource<GearPart> BINDING = DataResource.part("binding");
        public static final DataResource<GearPart> BOOTS_PLATES = DataResource.part("boots_plates");
        public static final DataResource<GearPart> CHESTPLATE_PLATES = DataResource.part("chestplate_plates");
        public static final DataResource<GearPart> COATING = DataResource.part("coating");
        public static final DataResource<GearPart> GRIP = DataResource.part("grip");
        public static final DataResource<GearPart> HELMET_PLATES = DataResource.part("helmet_plates");
        public static final DataResource<GearPart> HOE_HEAD = DataResource.part("hoe_head");
        public static final DataResource<GearPart> LEGGINGS_PLATES = DataResource.part("leggings_plates");
        public static final DataResource<GearPart> MISC_SPOON = DataResource.part("misc/spoon");
        public static final DataResource<GearPart> PICKAXE_HEAD = DataResource.part("pickaxe_head");
        public static final DataResource<GearPart> RED_CARD = DataResource.part("misc/red_card");
        public static final DataResource<GearPart> ROD = DataResource.part("rod");
        public static final DataResource<GearPart> SHOVEL_HEAD = DataResource.part("shovel_head");
        public static final DataResource<GearPart> SWORD_BLADE = DataResource.part("sword_blade");
        public static final DataResource<GearPart> TIP = DataResource.part("tip");

        private Parts() {}
    }

    public static final class Traits {
        public static final DataResource<Trait> ACCELERATE = DataResource.trait("accelerate");
        public static final DataResource<Trait> ADAMANT = DataResource.trait("adamant");
        public static final DataResource<Trait> ANCIENT = DataResource.trait("ancient");
        public static final DataResource<Trait> AQUATIC = DataResource.trait("aquatic");
        public static final DataResource<Trait> BASTION = DataResource.trait("bastion");
        public static final DataResource<Trait> BENDING = DataResource.trait("bending");
        public static final DataResource<Trait> BOUNCE = DataResource.trait("bounce");
        public static final DataResource<Trait> BRILLIANT = DataResource.trait("brilliant");
        public static final DataResource<Trait> BRITTLE = DataResource.trait("brittle");
        public static final DataResource<Trait> BULKY = DataResource.trait("bulky");
        public static final DataResource<Trait> CHILLED = DataResource.trait("chilled");
        public static final DataResource<Trait> CHIPPING = DataResource.trait("chipping");
        public static final DataResource<Trait> CONFETTI = DataResource.trait("confetti");
        public static final DataResource<Trait> CRACKLER = DataResource.trait("crackler");
        public static final DataResource<Trait> CRUDE = DataResource.trait("crude");
        public static final DataResource<Trait> CRUSHING = DataResource.trait("crushing");
        public static final DataResource<Trait> CURE_POISON = DataResource.trait("cure_poison");
        public static final DataResource<Trait> CURE_WITHER = DataResource.trait("cure_wither");
        public static final DataResource<Trait> CURSED = DataResource.trait("cursed");
        public static final DataResource<Trait> SWIFT_SWIM = DataResource.trait("swift_swim");
        public static final DataResource<Trait> ERODED = DataResource.trait("eroded");
        public static final DataResource<Trait> FIERY = DataResource.trait("fiery");
        public static final DataResource<Trait> FIREPROOF = DataResource.trait("fireproof");
        public static final DataResource<Trait> FLAME_WARD = DataResource.trait("flame_ward");
        public static final DataResource<Trait> FLAMMABLE = DataResource.trait("flammable");
        public static final DataResource<Trait> FLEXIBLE = DataResource.trait("flexible");
        public static final DataResource<Trait> FLOATSTONER = DataResource.trait("floatstoner");
        public static final DataResource<Trait> GOLD_DIGGER = DataResource.trait("gold_digger");
        public static final DataResource<Trait> GREEDY = DataResource.trait("greedy");
        public static final DataResource<Trait> HARD = DataResource.trait("hard");
        public static final DataResource<Trait> HEAVY = DataResource.trait("heavy");
        public static final DataResource<Trait> HOLY = DataResource.trait("holy");
        public static final DataResource<Trait> INDESTRUCTIBLE = DataResource.trait("indestructible");
        public static final DataResource<Trait> IGNITE = DataResource.trait("ignite");
        public static final DataResource<Trait> IMPERIAL = DataResource.trait("imperial");
        public static final DataResource<Trait> JABBERWOCKY = DataResource.trait("jabberwocky");
        public static final DataResource<Trait> JAGGED = DataResource.trait("jagged");
        public static final DataResource<Trait> KITTY_VISION = DataResource.trait("kitty_vision");
        public static final DataResource<Trait> LIGHT = DataResource.trait("light");
        public static final DataResource<Trait> LUCKY = DataResource.trait("lucky");
        public static final DataResource<Trait> LUSTROUS = DataResource.trait("lustrous");
        public static final DataResource<Trait> MAGMATIC = DataResource.trait("magmatic");
        public static final DataResource<Trait> MAGNETIC = DataResource.trait("magnetic");
        public static final DataResource<Trait> MALLEABLE = DataResource.trait("malleable");
        public static final DataResource<Trait> MIGHTY = DataResource.trait("mighty");
        public static final DataResource<Trait> MOONWALKER = DataResource.trait("moonwalker");
        public static final DataResource<Trait> MULTI_BREAK = DataResource.trait("multi_break");
        public static final DataResource<Trait> ORGANIC = DataResource.trait("organic");
        public static final DataResource<Trait> RACKER = DataResource.trait("racker");
        public static final DataResource<Trait> REACH = DataResource.trait("reach");
        public static final DataResource<Trait> RED_CARD = DataResource.trait("red_card");
        public static final DataResource<Trait> REFRACTIVE = DataResource.trait("refractive");
        public static final DataResource<Trait> RENEW = DataResource.trait("renew");
        public static final DataResource<Trait> ROAD_MAKER = DataResource.trait("road_maker");
        public static final DataResource<Trait> RUSTIC = DataResource.trait("rustic");
        public static final DataResource<Trait> SHARP = DataResource.trait("sharp");
        public static final DataResource<Trait> SILKY = DataResource.trait("silky");
        public static final DataResource<Trait> SNOW_WALKER = DataResource.trait("snow_walker");
        public static final DataResource<Trait> SOFT = DataResource.trait("soft");
        public static final DataResource<Trait> SPOON = DataResource.trait("spoon");
        public static final DataResource<Trait> STELLAR = DataResource.trait("stellar");
        public static final DataResource<Trait> STURDY = DataResource.trait("sturdy");
        public static final DataResource<Trait> SYNERGISTIC = DataResource.trait("synergistic");
        public static final DataResource<Trait> TERMINUS = DataResource.trait("terminus");
        public static final DataResource<Trait> TURTLE = DataResource.trait("turtle");
        public static final DataResource<Trait> VENOM = DataResource.trait("venom");
        public static final DataResource<Trait> VOID_WARD = DataResource.trait("void_ward");
        public static final DataResource<Trait> VULCAN = DataResource.trait("vulcan");
        public static final DataResource<Trait> WIDEN = DataResource.trait("widen");

        // Misc constants
        public static final float ANCIENT_XP_BOOST = 0.25f;
        public static final float MOONWALKER_GRAVITY_MOD = -0.15f;
        public static final float STELLAR_REPAIR_CHANCE = 0.02f;
        @Deprecated
        public static final float SYNERGY_BOOST_MULTI = 0.04f;

        private Traits() {}
    }

    private static ResourceLocation modId(String path) {
        return new ResourceLocation(SilentGear.MOD_ID, path);
    }
}
