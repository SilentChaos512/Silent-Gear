package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class SGearJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = SilentGear.getId("plugin/main");
    static final ResourceLocation GEAR_CRAFTING = SilentGear.getId("category/gear_crafting");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new GearCraftingRecipeCategoryJei(guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory<>(Const.FABRIC_COMPOUNDER_INFO, "fabric", guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory<>(Const.GEM_COMPOUNDER_INFO, "gem", guiHelper));
        reg.addRecipeCategories(new CompoundingRecipeCategory<>(Const.METAL_COMPOUNDER_INFO, "metal", guiHelper));
        reg.addRecipeCategories(new MaterialGraderRecipeCategory(guiHelper));
        reg.addRecipeCategories(new SalvagingRecipeCategoryJei(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        assert Minecraft.getInstance().level != null;
        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();

        // Repair kit hints
        for (RepairKitItem item : Registration.getItems(RepairKitItem.class)) {
            String itemName = NameUtils.fromItem(item).getPath();
            reg.addRecipes(Collections.singleton(new ShapelessRecipe(SilentGear.getId(itemName + "_fill_hint"), "",
                            new ItemStack(item),
                            NonNullList.of(Ingredient.EMPTY,
                                    Ingredient.of(item),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN)
                            ))),
                    VanillaRecipeCategoryUid.CRAFTING);
            reg.addRecipes(Collections.singleton(new ShapelessRecipe(SilentGear.getId(itemName + "_fill_hint_frag"), "",
                            new ItemStack(item),
                            NonNullList.of(Ingredient.EMPTY,
                                    Ingredient.of(item),
                                    Ingredient.of(ModItems.FRAGMENT),
                                    Ingredient.of(ModItems.FRAGMENT),
                                    Ingredient.of(ModItems.FRAGMENT)
                            ))),
                    VanillaRecipeCategoryUid.CRAFTING);
        }

        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(SGearJeiPlugin::isGearCraftingRecipe)
                .collect(Collectors.toList()), GEAR_CRAFTING);

        // Compounders
        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == CompoundingRecipe.COMPOUNDING_FABRIC_TYPE)
                .collect(Collectors.toList()), Const.COMPOUNDING_FABRIC);
        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == CompoundingRecipe.COMPOUNDING_GEM_TYPE)
                .collect(Collectors.toList()), Const.COMPOUNDING_GEM);
        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == CompoundingRecipe.COMPOUNDING_METAL_TYPE)
                .collect(Collectors.toList()), Const.COMPOUNDING_METAL);

        for (int i = 2; i <= 4; ++i) {
            reg.addRecipes(Collections.singleton(CompoundingRecipe.makeExample(Const.FABRIC_COMPOUNDER_INFO,
                    i, new FabricCompoundingRecipe(SilentGear.getId("fabric_example_" + i)))), Const.COMPOUNDING_FABRIC);
            reg.addRecipes(Collections.singleton(CompoundingRecipe.makeExample(Const.GEM_COMPOUNDER_INFO,
                    i, new GemCompoundingRecipe(SilentGear.getId("gem_example_" + i)))), Const.COMPOUNDING_GEM);
            reg.addRecipes(Collections.singleton(CompoundingRecipe.makeExample(Const.METAL_COMPOUNDER_INFO,
                    i, new MetalCompoundingRecipe(SilentGear.getId("metal_example_" + i)))), Const.COMPOUNDING_METAL);
        }

        // Grading
        reg.addRecipes(Collections.singleton(new MaterialGraderRecipeCategory.GraderRecipe()), Const.GRADING);

        // Salvaging
        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == SalvagingRecipe.SALVAGING_TYPE)
                .collect(Collectors.toList()), Const.SALVAGING);

        addInfoPage(reg, CraftingItems.RED_CARD_UPGRADE);
        addInfoPage(reg, CraftingItems.SPOON_UPGRADE);
        for (Item item : Registration.getItems(item -> item instanceof ICoreTool)) {
            addInfoPage(reg, item);
        }
    }

    private static boolean isGearCraftingRecipe(Recipe<?> recipe) {
        RecipeSerializer<?> serializer = recipe.getSerializer();
        return serializer == ModRecipes.SHAPED_GEAR.get() || serializer == ModRecipes.SHAPELESS_GEAR.get() || serializer == ModRecipes.COMPOUND_PART.get();
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), GEAR_CRAFTING);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.REFABRICATOR), Const.COMPOUNDING_FABRIC);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.RECRYSTALLIZER), Const.COMPOUNDING_GEM);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.METAL_ALLOYER), Const.COMPOUNDING_METAL);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.MATERIAL_GRADER), Const.GRADING);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.SALVAGER), Const.SALVAGING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(RefabricatorScreen.class, 90, 30, 28, 23, Const.COMPOUNDING_FABRIC);
        reg.addRecipeClickArea(RecrystallizerScreen.class, 90, 30, 28, 23, Const.COMPOUNDING_GEM);
        reg.addRecipeClickArea(MetalAlloyerScreen.class, 90, 30, 28, 23, Const.COMPOUNDING_METAL);
        reg.addRecipeClickArea(GraderScreen.class, 48, 30, 28, 23, Const.GRADING);
        reg.addRecipeClickArea(SalvagerScreen.class, 30, 30, 28, 23, Const.SALVAGING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        reg.registerSubtypeInterpreter(ModItems.FRAGMENT.get(), (stack, context) -> {
            IMaterialInstance material = FragmentItem.getMaterial(stack);
            return material != null ? material.getId().toString() : "";
        });

        IIngredientSubtypeInterpreter<ItemStack> customMaterials = (stack, context) -> {
            IMaterialInstance material = CustomMaterialItem.getMaterial(stack);
            return material != null ? material.getId().toString() : "";
        };
        reg.registerSubtypeInterpreter(ModItems.CUSTOM_GEM.get(), customMaterials);
        reg.registerSubtypeInterpreter(ModItems.CUSTOM_INGOT.get(), customMaterials);
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemLike item) {
        String key = getDescKey(Objects.requireNonNull(item.asItem().getRegistryName()));
        ItemStack stack = new ItemStack(item);
        reg.addIngredientInfo(stack, VanillaTypes.ITEM, new TranslatableComponent(key));
    }

    private static void addInfoPage(IRecipeRegistration reg, String name, Collection<ItemLike> items) {
        String key = getDescKey(SilentGear.getId(name));
        List<ItemStack> stacks = items.stream().map(ItemStack::new).collect(Collectors.toList());
        reg.addIngredientInfo(stacks, VanillaTypes.ITEM, new TranslatableComponent(key));
    }

    private static void addInfoPage(IRecipeRegistration reg, ItemLike item, Stream<ItemStack> variants) {
        String key = getDescKey(Objects.requireNonNull(item.asItem().getRegistryName()));
        reg.addIngredientInfo(variants.collect(Collectors.toList()), VanillaTypes.ITEM, new TranslatableComponent(key));
    }

    private static String getDescKey(ResourceLocation name) {
        return "jei." + name.getNamespace() + "." + name.getPath() + ".desc";
    }
}
