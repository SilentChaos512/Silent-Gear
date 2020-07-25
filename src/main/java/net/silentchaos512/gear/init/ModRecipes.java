package net.silentchaos512.gear.init;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.crafting.ingredient.ExclusionIngredient;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.ingredient.GearTypeIngredient;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.*;
import net.silentchaos512.gear.crafting.recipe.salvage.CompoundPartSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.GearSalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.crafting.recipe.smithing.CoatingSmithingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.Const;

public final class ModRecipes {
    // TODO: Use DeferredRegister, collect all constants in dedicated classes
    public static final IRecipeType<SalvagingRecipe> SALVAGING_TYPE = IRecipeType.register(Const.SALVAGING.toString());
    public static final IRecipeSerializer<SalvagingRecipe> SALVAGING_SERIALIZER = new SalvagingRecipe.Serializer();
    public static final IRecipeSerializer<GearSalvagingRecipe> SALVAGING_GEAR_SERIALIZER = new GearSalvagingRecipe.Serializer();
    public static final IRecipeSerializer<CompoundPartSalvagingRecipe> SALVAGING_COMPOUND_PART_SERIALIZER = new CompoundPartSalvagingRecipe.Serializer();
    public static final IRecipeSerializer<CoatingSmithingRecipe> COATING_SMITHING = new CoatingSmithingRecipe.Serializer();

    private ModRecipes() {}

    public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        // Recipe serializers
        register(ShapelessCompoundPartRecipe.NAME, ShapelessCompoundPartRecipe.SERIALIZER);
        register(ShapedGearRecipe.NAME, ShapedGearRecipe.SERIALIZER);
        register(ShapelessGearRecipe.NAME, ShapelessGearRecipe.SERIALIZER);
        register(GearPartSwapRecipe.NAME, GearPartSwapRecipe.SERIALIZER);
        register(FillRepairKitRecipe.NAME, FillRepairKitRecipe.SERIALIZER);
        register(QuickRepairRecipe.NAME, QuickRepairRecipe.SERIALIZER);
        register(UpgradeGearRecipe.NAME, UpgradeGearRecipe.SERIALIZER);
        register(SGearDamageItemRecipe.NAME, SGearDamageItemRecipe.SERIALIZER);
        register(SilentGear.getId("conversion"), ConversionRecipe.SERIALIZER);
        register(SilentGear.getId("crafting_special_repairitem"), new SpecialRecipeSerializer<>(RepairItemRecipeFix::new));
        register(Const.SMITHING_COATING, COATING_SMITHING);
        register(Const.SALVAGING, SALVAGING_SERIALIZER);
        register(Const.SALVAGING_GEAR, SALVAGING_GEAR_SERIALIZER);
        register(Const.SALVAGING_COMPOUND_PART, SALVAGING_COMPOUND_PART_SERIALIZER);

        // Ingredient serializers
        CraftingHelper.register(ExclusionIngredient.Serializer.NAME, ExclusionIngredient.Serializer.INSTANCE);
        CraftingHelper.register(GearPartIngredient.Serializer.NAME, GearPartIngredient.Serializer.INSTANCE);
        CraftingHelper.register(GearTypeIngredient.Serializer.NAME, GearTypeIngredient.Serializer.INSTANCE);
        CraftingHelper.register(PartMaterialIngredient.Serializer.NAME, PartMaterialIngredient.Serializer.INSTANCE);

        if (SilentGear.isDevBuild()) {
//            MinecraftForge.EVENT_BUS.addListener(ModRecipes::onPlayerJoinServer);
        }
    }

    private static void register(ResourceLocation id, IRecipeSerializer<?> serializer) {
        ForgeRegistries.RECIPE_SERIALIZERS.register(serializer.setRegistryName(id));
    }

    private static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player.world.isRemote || player.world.getServer() == null) return;

        ResourceLocation[] recipes = player.world.getServer().getRecipeManager().getRecipes()
                .stream()
                .map(IRecipe::getId)
                .filter(name -> name.getNamespace().equals(SilentGear.MOD_ID))
                .toArray(ResourceLocation[]::new);

        SilentGear.LOGGER.info("DEV: Unlocking {} recipes in recipe book", recipes.length);
        player.unlockRecipes(recipes);
    }

    public static boolean isRepairMaterial(ItemStack gear, ItemStack materialItem) {
        MaterialInstance mat = MaterialInstance.from(materialItem);
        if (mat != null) {
            return mat.getRepairValue(gear) > 0;
        }

        return false;
    }
}
