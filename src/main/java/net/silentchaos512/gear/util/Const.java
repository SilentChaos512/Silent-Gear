package net.silentchaos512.gear.util;

import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.traits.ITrait;

public final class Const {
    // Model loaders
    public static final ResourceLocation COMPOUND_PART_MODEL_LOADER = SilentGear.getId("compound_part_model");
    public static final ResourceLocation FRAGMENT_MODEL_LOADER = SilentGear.getId("fragment_model");
    public static final ResourceLocation GEAR_MODEL_LOADER = SilentGear.getId("gear_model");

    // Model properties
    public static final ResourceLocation BROKEN_PROPERTY = SilentGear.getId("broken");

    // Recipe types and categories
    public static final ResourceLocation COMBINE_FRAGMENTS = SilentGear.getId("combine_fragments");
    public static final ResourceLocation COMPOUND_PART = SilentGear.getId("compound_part");
    public static final ResourceLocation COMPOUNDING = SilentGear.getId("compounding");
    public static final ResourceLocation COMPOUNDING_GEM = SilentGear.getId("compounding/gem");
    public static final ResourceLocation COMPOUNDING_METAL = SilentGear.getId("compounding/metal");
    public static final ResourceLocation DAMAGE_ITEM = SilentGear.getId("damage_item");
    public static final ResourceLocation FILL_REPAIR_KIT = SilentGear.getId("fill_repair_kit");
    public static final ResourceLocation GRADING = SilentGear.getId("grading");
    public static final ResourceLocation MOD_KIT_REMOVE_PART = SilentGear.getId("mod_kit_remove_part");
    public static final ResourceLocation QUICK_REPAIR = SilentGear.getId("quick_repair");
    public static final ResourceLocation SALVAGING = SilentGear.getId("salvaging");
    public static final ResourceLocation SALVAGING_COMPOUND_PART = SilentGear.getId("salvaging/compound_part");
    public static final ResourceLocation SALVAGING_GEAR = SilentGear.getId("salvaging/gear");
    public static final ResourceLocation SHAPED_GEAR_CRAFTING = SilentGear.getId("shaped_gear_crafting");
    public static final ResourceLocation SHAPELESS_GEAR_CRAFTING = SilentGear.getId("gear_crafting");
    public static final ResourceLocation SMITHING_COATING = SilentGear.getId("smithing/coating");
    public static final ResourceLocation SMITHING_UPGRADE = SilentGear.getId("smithing/upgrade");
    public static final ResourceLocation SWAP_GEAR_PART = SilentGear.getId("swap_gear_part");

    // Mod IDs
    public static final String CAELUS = "caelus";
    public static final String CURIOS = "curios";

    // Random
    public static final ResourceLocation NULL_ID = new ResourceLocation("null");

    private Const() {}

    public static final class Materials {
        public static final DataResource<IMaterial> AZURE_ELECTRUM = DataResource.material("azure_electrum");
        public static final DataResource<IMaterial> AZURE_SILVER = DataResource.material("azure_silver");
        public static final DataResource<IMaterial> BLAZE_GOLD = DataResource.material("blaze_gold");
        public static final DataResource<IMaterial> CRIMSON_STEEL = DataResource.material("crimson_steel");
        public static final DataResource<IMaterial> EMERALD = DataResource.material("emerald");
        public static final DataResource<IMaterial> EXAMPLE = DataResource.material("example");
        public static final DataResource<IMaterial> STRING = DataResource.material("string");

        public static final DataResource<IMaterial> WOOD_ROUGH = DataResource.material("wood/rough");
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
        public static final DataResource<ITrait> FLAME_WARD = DataResource.trait("flame_ward");
        public static final DataResource<ITrait> FLAMMABLE = DataResource.trait("flammable");
        public static final DataResource<ITrait> FLEXIBLE = DataResource.trait("flexible");
        public static final DataResource<ITrait> FLOATSTONER = DataResource.trait("floatstoner");
        public static final DataResource<ITrait> GOLD_DIGGER = DataResource.trait("gold_digger");
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
        public static final DataResource<ITrait> ROAD_MAKER = DataResource.trait("road_maker");
        public static final DataResource<ITrait> RUSTIC = DataResource.trait("rustic");
        public static final DataResource<ITrait> SHARP = DataResource.trait("sharp");
        public static final DataResource<ITrait> SILKY = DataResource.trait("silky");
        public static final DataResource<ITrait> SOFT = DataResource.trait("soft");
        public static final DataResource<ITrait> SPOON = DataResource.trait("spoon");
        public static final DataResource<ITrait> STELLAR = DataResource.trait("stellar");
        public static final DataResource<ITrait> SYNERGISTIC = DataResource.trait("synergistic");
        public static final DataResource<ITrait> TERMINUS = DataResource.trait("terminus");
        public static final DataResource<ITrait> TURTLE = DataResource.trait("turtle");
        public static final DataResource<ITrait> VENOM = DataResource.trait("venom");
        public static final DataResource<ITrait> VULCAN = DataResource.trait("vulcan");

        // Misc constants
        public static final float ANCIENT_XP_BOOST = 0.25f;
        public static final float MOONWALKER_GRAVITY_MOD = -0.15f;
        @Deprecated
        public static final float SYNERGY_BOOST_MULTI = 0.04f;

        private Traits() {}
    }
}
