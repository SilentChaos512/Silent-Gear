package net.silentchaos512.gear.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.block.compounder.CompoundMakerInfo;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;
import net.silentchaos512.gear.gear.material.MaterialCategories;
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
    public static final CompoundMakerInfo<MetalAlloyRecipe> METAL_COMPOUNDER_INFO = new CompoundMakerInfo<>(
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
    public static final CompoundMakerInfo<GemAlloyRecipe> GEM_COMPOUNDER_INFO = new CompoundMakerInfo<>(
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
    public static final CompoundMakerInfo<FabricAlloyRecipe> FABRIC_COMPOUNDER_INFO = new CompoundMakerInfo<>(
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
            SgRecipes.COMPOUNDING_FABRIC,
            SgRecipes.COMPOUNDING_FABRIC_TYPE,
            FabricAlloyRecipe.class);

    private Const() {}

    public static final class Materials {
        public static final DataResource<IMaterial> AZURE_ELECTRUM = DataResource.material("azure_electrum");
        public static final DataResource<IMaterial> AZURE_SILVER = DataResource.material("azure_silver");
        public static final DataResource<IMaterial> BLAZE_GOLD = DataResource.material("blaze_gold");
        public static final DataResource<IMaterial> CRIMSON_STEEL = DataResource.material("crimson_steel");
        public static final DataResource<IMaterial> DIAMOND = DataResource.material("diamond");
        public static final DataResource<IMaterial> DIMERALD = DataResource.material("dimerald");
        public static final DataResource<IMaterial> EMERALD = DataResource.material("emerald");
        public static final DataResource<IMaterial> EXAMPLE = DataResource.material("example");
        public static final DataResource<IMaterial> FEATHER = DataResource.material("feather");
        public static final DataResource<IMaterial> IRON = DataResource.material("iron");
        public static final DataResource<IMaterial> STRING = DataResource.material("string");
        public static final DataResource<IMaterial> TYRIAN_STEEL = DataResource.material("tyrian_steel");

        public static final DataResource<IMaterial> WOOD = DataResource.material("wood");
        public static final DataResource<IMaterial> WOOD_ROUGH = DataResource.material("wood/rough");
        public static final DataResource<IMaterial> WOOL = DataResource.material("wool");
        public static final DataResource<IMaterial> WOOL_BLACK = DataResource.material("wool/black");

        private Materials() {}

    }

    public static final class Parts {
        public static final DataResource<IGearPart> ARMOR_BODY = DataResource.part("armor_body");
        public static final DataResource<IGearPart> BINDING = DataResource.part("binding");
        public static final DataResource<IGearPart> GRIP = DataResource.part("grip");
        public static final DataResource<IGearPart> MISC_SPOON = DataResource.part("misc/spoon");
        public static final DataResource<IGearPart> PICKAXE_HEAD = DataResource.part("pickaxe_head");
        public static final DataResource<IGearPart> RED_CARD = DataResource.part("misc/red_card");
        public static final DataResource<IGearPart> ROD = DataResource.part("rod");
        public static final DataResource<IGearPart> TIP = DataResource.part("tip");

        private Parts() {}
    }

    public static final class Traits {
        public static final DataResource<ITrait> ACCELERATE = DataResource.trait("accelerate");
        public static final DataResource<ITrait> ADAMANT = DataResource.trait("adamant");
        public static final DataResource<ITrait> ANCIENT = DataResource.trait("ancient");
        public static final DataResource<ITrait> AQUATIC = DataResource.trait("aquatic");
        public static final DataResource<ITrait> BASTION = DataResource.trait("bastion");
        public static final DataResource<ITrait> BENDING = DataResource.trait("bending");
        public static final DataResource<ITrait> BOUNCE = DataResource.trait("bounce");
        public static final DataResource<ITrait> BRILLIANT = DataResource.trait("brilliant");
        public static final DataResource<ITrait> BRITTLE = DataResource.trait("brittle");
        public static final DataResource<ITrait> BULKY = DataResource.trait("bulky");
        public static final DataResource<ITrait> CHILLED = DataResource.trait("chilled");
        public static final DataResource<ITrait> CHIPPING = DataResource.trait("chipping");
        public static final DataResource<ITrait> CONFETTI = DataResource.trait("confetti");
        public static final DataResource<ITrait> CRACKLER = DataResource.trait("crackler");
        public static final DataResource<ITrait> CRUDE = DataResource.trait("crude");
        public static final DataResource<ITrait> CRUSHING = DataResource.trait("crushing");
        public static final DataResource<ITrait> CURE_POISON = DataResource.trait("cure_poison");
        public static final DataResource<ITrait> CURE_WITHER = DataResource.trait("cure_wither");
        public static final DataResource<ITrait> CURSED = DataResource.trait("cursed");
        public static final DataResource<ITrait> SWIFT_SWIM = DataResource.trait("swift_swim");
        public static final DataResource<ITrait> ERODED = DataResource.trait("eroded");
        public static final DataResource<ITrait> FIERY = DataResource.trait("fiery");
        public static final DataResource<ITrait> FIREPROOF = DataResource.trait("fireproof");
        public static final DataResource<ITrait> FLAME_WARD = DataResource.trait("flame_ward");
        public static final DataResource<ITrait> FLAMMABLE = DataResource.trait("flammable");
        public static final DataResource<ITrait> FLEXIBLE = DataResource.trait("flexible");
        public static final DataResource<ITrait> FLOATSTONER = DataResource.trait("floatstoner");
        public static final DataResource<ITrait> GOLD_DIGGER = DataResource.trait("gold_digger");
        public static final DataResource<ITrait> GREEDY = DataResource.trait("greedy");
        public static final DataResource<ITrait> HARD = DataResource.trait("hard");
        public static final DataResource<ITrait> HEAVY = DataResource.trait("heavy");
        public static final DataResource<ITrait> HOLY = DataResource.trait("holy");
        public static final DataResource<ITrait> INDESTRUCTIBLE = DataResource.trait("indestructible");
        public static final DataResource<ITrait> IGNITE = DataResource.trait("ignite");
        public static final DataResource<ITrait> IMPERIAL = DataResource.trait("imperial");
        public static final DataResource<ITrait> JABBERWOCKY = DataResource.trait("jabberwocky");
        public static final DataResource<ITrait> JAGGED = DataResource.trait("jagged");
        public static final DataResource<ITrait> KITTY_VISION = DataResource.trait("kitty_vision");
        public static final DataResource<ITrait> LIGHT = DataResource.trait("light");
        public static final DataResource<ITrait> LUCKY = DataResource.trait("lucky");
        public static final DataResource<ITrait> LUSTROUS = DataResource.trait("lustrous");
        public static final DataResource<ITrait> MAGMATIC = DataResource.trait("magmatic");
        public static final DataResource<ITrait> MAGNETIC = DataResource.trait("magnetic");
        public static final DataResource<ITrait> MALLEABLE = DataResource.trait("malleable");
        public static final DataResource<ITrait> MIGHTY = DataResource.trait("mighty");
        public static final DataResource<ITrait> MOONWALKER = DataResource.trait("moonwalker");
        public static final DataResource<ITrait> MULTI_BREAK = DataResource.trait("multi_break");
        public static final DataResource<ITrait> ORGANIC = DataResource.trait("organic");
        public static final DataResource<ITrait> RACKER = DataResource.trait("racker");
        public static final DataResource<ITrait> REACH = DataResource.trait("reach");
        public static final DataResource<ITrait> RED_CARD = DataResource.trait("red_card");
        public static final DataResource<ITrait> REFRACTIVE = DataResource.trait("refractive");
        public static final DataResource<ITrait> RENEW = DataResource.trait("renew");
        public static final DataResource<ITrait> ROAD_MAKER = DataResource.trait("road_maker");
        public static final DataResource<ITrait> RUSTIC = DataResource.trait("rustic");
        public static final DataResource<ITrait> SHARP = DataResource.trait("sharp");
        public static final DataResource<ITrait> SILKY = DataResource.trait("silky");
        public static final DataResource<ITrait> SNOW_WALKER = DataResource.trait("snow_walker");
        public static final DataResource<ITrait> SOFT = DataResource.trait("soft");
        public static final DataResource<ITrait> SPOON = DataResource.trait("spoon");
        public static final DataResource<ITrait> STELLAR = DataResource.trait("stellar");
        public static final DataResource<ITrait> STURDY = DataResource.trait("sturdy");
        public static final DataResource<ITrait> SYNERGISTIC = DataResource.trait("synergistic");
        public static final DataResource<ITrait> TERMINUS = DataResource.trait("terminus");
        public static final DataResource<ITrait> TURTLE = DataResource.trait("turtle");
        public static final DataResource<ITrait> VENOM = DataResource.trait("venom");
        public static final DataResource<ITrait> VOID_WARD = DataResource.trait("void_ward");
        public static final DataResource<ITrait> VULCAN = DataResource.trait("vulcan");
        public static final DataResource<ITrait> WIDEN = DataResource.trait("widen");

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
