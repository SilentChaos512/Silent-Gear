package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.compounder.MetalAlloyerScreen;
import net.silentchaos512.gear.block.compounder.RecrystallizerScreen;
import net.silentchaos512.gear.block.compounder.RefabricatorScreen;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.compounder.CompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.FabricCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.GemCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.compounder.MetalCompoundingRecipe;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.init.SgBlocks;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.init.SgRecipes;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class SGearJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = SilentGear.getId("plugin/main");

    public static final RecipeType<CompoundingRecipe> COMPOUNDING_FABRIC_TYPE = RecipeType.create(SilentGear.MOD_ID, "compounding_fabric", CompoundingRecipe.class);
    public static final RecipeType<CompoundingRecipe> COMPOUNDING_GEM_TYPE = RecipeType.create(SilentGear.MOD_ID, "compounding_gem", CompoundingRecipe.class);
    public static final RecipeType<CompoundingRecipe> COMPOUNDING_METAL_TYPE = RecipeType.create(SilentGear.MOD_ID, "compounding_metal", CompoundingRecipe.class);
    static final RecipeType<CraftingRecipe> GEAR_CRAFTING_TYPE = RecipeType.create(SilentGear.MOD_ID, "gear_crafting", CraftingRecipe.class);
    static final RecipeType<MaterialGraderRecipeCategory.GraderRecipe> GRADING_TYPE = RecipeType.create(SilentGear.MOD_ID, "grading", MaterialGraderRecipeCategory.GraderRecipe.class);
    static final RecipeType<SalvagingRecipe> SALVAGING_TYPE = RecipeType.create(SilentGear.MOD_ID, "salvaging", SalvagingRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new GearCraftingRecipeCategoryJei(guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory(Const.FABRIC_COMPOUNDER_INFO, "fabric", guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory(Const.GEM_COMPOUNDER_INFO, "gem", guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory(Const.METAL_COMPOUNDER_INFO, "metal", guiHelper));
        reg.addRecipeCategories(new MaterialGraderRecipeCategory(guiHelper));
        reg.addRecipeCategories(new SalvagingRecipeCategoryJei(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        assert Minecraft.getInstance().level != null;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Repair kit hints
        for (RepairKitItem item : SgItems.getItems(RepairKitItem.class)) {
            String itemName = NameUtils.fromItem(item).getPath();
            reg.addRecipes(RecipeTypes.CRAFTING, Collections.singletonList(new ShapelessRecipe(SilentGear.getId(itemName + "_fill_hint"), "",
                            new ItemStack(item),
                            NonNullList.of(Ingredient.EMPTY,
                                    Ingredient.of(item),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN)
                            ))));
            reg.addRecipes(RecipeTypes.CRAFTING, Collections.singletonList(new ShapelessRecipe(SilentGear.getId(itemName + "_fill_hint_frag"), "",
                            new ItemStack(item),
                            NonNullList.of(Ingredient.EMPTY,
                                    Ingredient.of(item),
                                    Ingredient.of(SgItems.FRAGMENT),
                                    Ingredient.of(SgItems.FRAGMENT),
                                    Ingredient.of(SgItems.FRAGMENT)
                            ))));
        }

        reg.addRecipes(GEAR_CRAFTING_TYPE, getRecipes(recipeManager, SGearJeiPlugin::isGearCraftingRecipe, CraftingRecipe.class));

        // Compounders
        reg.addRecipes(COMPOUNDING_FABRIC_TYPE, getRecipes(recipeManager, SgRecipes.COMPOUNDING_FABRIC_TYPE.get(), CompoundingRecipe.class));
        reg.addRecipes(COMPOUNDING_GEM_TYPE, getRecipes(recipeManager, SgRecipes.COMPOUNDING_GEM_TYPE.get(), CompoundingRecipe.class));
        reg.addRecipes(COMPOUNDING_METAL_TYPE, getRecipes(recipeManager, SgRecipes.COMPOUNDING_METAL_TYPE.get(), CompoundingRecipe.class));

        for (int i = 2; i <= 4; ++i) {
            reg.addRecipes(COMPOUNDING_FABRIC_TYPE, Collections.singletonList(CompoundingRecipe.makeExample(Const.FABRIC_COMPOUNDER_INFO,
                    i, new FabricCompoundingRecipe(SilentGear.getId("fabric_example_" + i)))));
            reg.addRecipes(COMPOUNDING_GEM_TYPE, Collections.singletonList(CompoundingRecipe.makeExample(Const.GEM_COMPOUNDER_INFO,
                    i, new GemCompoundingRecipe(SilentGear.getId("gem_example_" + i)))));
            reg.addRecipes(COMPOUNDING_METAL_TYPE, Collections.singletonList(CompoundingRecipe.makeExample(Const.METAL_COMPOUNDER_INFO,
                    i, new MetalCompoundingRecipe(SilentGear.getId("metal_example_" + i)))));
        }

        // Grading
        reg.addRecipes(GRADING_TYPE, Collections.singletonList(new MaterialGraderRecipeCategory.GraderRecipe()));

        // Salvaging
        reg.addRecipes(SALVAGING_TYPE, getRecipes(recipeManager, SgRecipes.SALVAGING_TYPE.get(), SalvagingRecipe.class));

        addInfoPage(reg, CraftingItems.RED_CARD_UPGRADE);
        addInfoPage(reg, CraftingItems.SPOON_UPGRADE);
        for (Item item : SgItems.getItems(item -> item instanceof ICoreTool)) {
            addInfoPage(reg, item);
        }
    }

    private static <R extends Recipe<?>> List<R> getRecipes(RecipeManager recipeManager, net.minecraft.world.item.crafting.RecipeType recipeType, Class<R> recipeClass) {
        return getRecipes(recipeManager, r -> r.getType() == recipeType, recipeClass);
    }

    private static <R extends Recipe<?>> List<R> getRecipes(RecipeManager recipeManager, RecipeSerializer<?> recipeSerializer, Class<R> recipeClass) {
        return getRecipes(recipeManager, r -> r.getSerializer() == recipeSerializer, recipeClass);
    }

    private static <R extends Recipe<?>> List<R> getRecipes(RecipeManager recipeManager, Predicate<Recipe<?>> predicate, Class<R> recipeClass) {
        return recipeManager.getRecipes().stream()
                .filter(predicate)
                .map(recipeClass::cast)
                .collect(Collectors.toList());
    }

    private static boolean isGearCraftingRecipe(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        return serializer == SgRecipes.SHAPED_GEAR.get() || serializer == SgRecipes.SHAPELESS_GEAR.get() || serializer == SgRecipes.COMPOUND_PART.get();
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), GEAR_CRAFTING_TYPE);
        reg.addRecipeCatalyst(new ItemStack(SgBlocks.REFABRICATOR), COMPOUNDING_FABRIC_TYPE);
        reg.addRecipeCatalyst(new ItemStack(SgBlocks.RECRYSTALLIZER), COMPOUNDING_GEM_TYPE);
        reg.addRecipeCatalyst(new ItemStack(SgBlocks.METAL_ALLOYER), COMPOUNDING_METAL_TYPE);
        reg.addRecipeCatalyst(new ItemStack(SgBlocks.MATERIAL_GRADER), GRADING_TYPE);
        reg.addRecipeCatalyst(new ItemStack(SgBlocks.SALVAGER), SALVAGING_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(RefabricatorScreen.class, 90, 30, 28, 23, COMPOUNDING_FABRIC_TYPE);
        reg.addRecipeClickArea(RecrystallizerScreen.class, 90, 30, 28, 23, COMPOUNDING_GEM_TYPE);
        reg.addRecipeClickArea(MetalAlloyerScreen.class, 90, 30, 28, 23, COMPOUNDING_METAL_TYPE);
        reg.addRecipeClickArea(GraderScreen.class, 48, 30, 28, 23, GRADING_TYPE);
        reg.addRecipeClickArea(SalvagerScreen.class, 30, 30, 28, 23, SALVAGING_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(SgItems.FRAGMENT.get(), (stack, context) -> {
            IMaterialInstance material = FragmentItem.getMaterial(stack);
            return material != null ? material.getId().toString() : "";
        });

        IIngredientSubtypeInterpreter<ItemStack> customMaterials = (stack, context) -> {
            IMaterialInstance material = CustomMaterialItem.getMaterial(stack);
            return material != null ? material.getId().toString() : "";
        };
        reg.registerSubtypeInterpreter(SgItems.CUSTOM_GEM.get(), customMaterials);
        reg.registerSubtypeInterpreter(SgItems.CUSTOM_INGOT.get(), customMaterials);
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemLike item) {
        String key = getDescKey(NameUtils.fromItem(item));
        ItemStack stack = new ItemStack(item);
        reg.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, Component.translatable(key));
    }

    private static void addInfoPage(IRecipeRegistration reg, String name, Collection<ItemLike> items) {
        String key = getDescKey(SilentGear.getId(name));
        List<ItemStack> stacks = items.stream().map(ItemStack::new).collect(Collectors.toList());
        reg.addIngredientInfo(stacks, VanillaTypes.ITEM_STACK, Component.translatable(key));
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemLike item, Stream<ItemStack> variants) {
        String key = getDescKey(NameUtils.fromItem(item));
        reg.addIngredientInfo(variants.collect(Collectors.toList()), VanillaTypes.ITEM_STACK, Component.translatable(key));
    }

    private static String getDescKey(ResourceLocation name) {
        return "jei." + name.getNamespace() + "." + name.getPath() + ".desc";
    }
}
