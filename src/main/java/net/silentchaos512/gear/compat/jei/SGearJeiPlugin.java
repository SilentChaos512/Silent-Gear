/*
package net.silentchaos512.gear.compat.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearTool;
import net.silentchaos512.gear.api.material.IMaterialCategory;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.block.alloymaker.screen.AlloyForgeScreen;
import net.silentchaos512.gear.block.alloymaker.screen.RecrystallizerScreen;
import net.silentchaos512.gear.block.alloymaker.screen.RefabricatorScreen;
import net.silentchaos512.gear.block.alloymaker.screen.SuperMixerScreen;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.client.ClientRecipeHelper;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.*;
import net.silentchaos512.gear.crafting.recipe.salvage.SalvagingRecipe;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomMaterialItem;
import net.silentchaos512.gear.item.RepairKitItem;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.IngredientUtils;
import net.silentchaos512.lib.crafting.recipe.CraftingRecipeExtension;
import net.silentchaos512.lib.util.NameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class SGearJeiPlugin implements IModPlugin {
    private static final Identifier PLUGIN_UID = SilentGear.getId("plugin/main");

    public static final IRecipeType<AlloyRecipe> ALLOY_MAKING_FABRIC_TYPE = IRecipeType.create(SilentGear.MOD_ID, "alloy_making/fabric", AlloyRecipe.class);
    public static final IRecipeType<AlloyRecipe> ALLOY_MAKING_GEM_TYPE = IRecipeType.create(SilentGear.MOD_ID, "alloy_making/gem", AlloyRecipe.class);
    public static final IRecipeType<AlloyRecipe> ALLOY_MAKING_METAL_TYPE = IRecipeType.create(SilentGear.MOD_ID, "alloy_making/metal", AlloyRecipe.class);
    public static final IRecipeType<AlloyRecipe> ALLOY_MAKING_SUPER_TYPE = IRecipeType.create(SilentGear.MOD_ID, "alloy_making/super", AlloyRecipe.class);
    static final IRecipeType<CraftingRecipeExtension> GEAR_CRAFTING_TYPE = IRecipeType.create(SilentGear.MOD_ID, "gear_crafting", CraftingRecipeExtension.class);
    static final IRecipeType<MaterialGraderRecipeCategory.GraderRecipe> GRADING_TYPE = IRecipeType.create(SilentGear.MOD_ID, "grading", MaterialGraderRecipeCategory.GraderRecipe.class);
    static final IRecipeType<SalvagingRecipe> SALVAGING_TYPE = IRecipeType.create(SilentGear.MOD_ID, "salvaging", SalvagingRecipe.class);
    static final IRecipeType<ToolActionRecipe> TOOL_ACTION_TYPE = IRecipeType.create(SilentGear.MOD_ID, "tool_action", ToolActionRecipe.class);

    @Override
    public Identifier getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new GearCraftingRecipeCategoryJei(guiHelper));
        reg.addRecipeCategories(new AlloyMakingRecipeCategory(Const.FABRIC_ALLOY_MAKER_INFO, "fabric", guiHelper));
        reg.addRecipeCategories(new AlloyMakingRecipeCategory(Const.GEM_ALLOY_MAKER_INFO, "gem", guiHelper));
        reg.addRecipeCategories(new AlloyMakingRecipeCategory(Const.METAL_ALLOY_MAKER_INFO, "metal", guiHelper));
        reg.addRecipeCategories(new AlloyMakingRecipeCategory(Const.SUPER_MIXER_INFO, "super", guiHelper));
        reg.addRecipeCategories(new MaterialGraderRecipeCategory(guiHelper));
        reg.addRecipeCategories(new SalvagingRecipeCategoryJei(guiHelper));
        reg.addRecipeCategories(new ToolActionRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        // Repair kit hints
        for (RepairKitItem item : SgItems.getItems(RepairKitItem.class)) {
            String itemName = NameUtils.fromItem(item).getPath();
            reg.addRecipes(RecipeTypes.CRAFTING, Collections.singletonList(new RecipeHolder<>(
                    ResourceKey.create(Registries.RECIPE, SilentGear.getId(itemName)),
                    new ShapelessRecipe("",
                            CraftingBookCategory.MISC,
                            new ItemStack(item),
                            List.of(
                                    Ingredient.of(item),
                                    new Ingredient(PartMaterialIngredient.of(PartTypes.MAIN.get())),
                                    new Ingredient(PartMaterialIngredient.of(PartTypes.MAIN.get())),
                                    new Ingredient(PartMaterialIngredient.of(PartTypes.MAIN.get()))
                            )
                    )
            )));
        }

        reg.addRecipes(GEAR_CRAFTING_TYPE, getRecipes(RecipeType.CRAFTING, CraftingRecipeExtension.class));

        // Tool Action (Stone Anvil)
        reg.addRecipes(TOOL_ACTION_TYPE, getRecipes(SgRecipes.TOOL_ACTION_TYPE.get(), ToolActionRecipe.class));

        // Alloy Makers
        reg.addRecipes(ALLOY_MAKING_FABRIC_TYPE, getRecipes(SgRecipes.ALLOY_MAKING_FABRIC_TYPE.get(), AlloyRecipe.class));
        reg.addRecipes(ALLOY_MAKING_GEM_TYPE, getRecipes(SgRecipes.ALLOY_MAKING_GEM_TYPE.get(), AlloyRecipe.class));
        reg.addRecipes(ALLOY_MAKING_METAL_TYPE, getRecipes(SgRecipes.ALLOY_MAKING_METAL_TYPE.get(), AlloyRecipe.class));
        reg.addRecipes(ALLOY_MAKING_SUPER_TYPE, getRecipes(SgRecipes.ALLOY_MAKING_SUPER_TYPE.get(), AlloyRecipe.class));

        for (int i = 2; i <= 4; ++i) {
            reg.addRecipes(ALLOY_MAKING_FABRIC_TYPE, Collections.singletonList(
                    AlloyRecipe.makeExample(Const.FABRIC_ALLOY_MAKER_INFO, i, FabricAlloyRecipe::new)
            ));
            reg.addRecipes(ALLOY_MAKING_GEM_TYPE, Collections.singletonList(
                    AlloyRecipe.makeExample(Const.GEM_ALLOY_MAKER_INFO, i, GemAlloyRecipe::new)
            ));
            reg.addRecipes(ALLOY_MAKING_METAL_TYPE, Collections.singletonList(
                    AlloyRecipe.makeExample(Const.METAL_ALLOY_MAKER_INFO, i, MetalAlloyRecipe::new)
            ));
            reg.addRecipes(ALLOY_MAKING_SUPER_TYPE, List.of(
                    AlloyRecipe.makeExample(Const.SUPER_MIXER_INFO, i, SuperAlloyRecipe::new)
            ));
        }

        // Grading
        reg.addRecipes(GRADING_TYPE, Collections.singletonList(new MaterialGraderRecipeCategory.GraderRecipe()));

        // Salvaging
        reg.addRecipes(SALVAGING_TYPE, getRecipes(SgRecipes.SALVAGING_TYPE.get(), SalvagingRecipe.class));

        addInfoPage(reg, CraftingItems.RED_CARD_UPGRADE);
        addInfoPage(reg, CraftingItems.SPOON_UPGRADE);
        for (Item item : SgItems.getItems(item -> item instanceof GearTool)) {
            addInfoPage(reg, item);
        }
    }

    private static <R extends Recipe<?>> List<R> getRecipes(RecipeType<?> recipeType, Class<R> recipeClass) {
        var listBuilder = ImmutableList.<R>builder();
        for (RecipeHolder<?> recipe : ClientRecipeHelper.getRecipes(recipeType)) {
            listBuilder.add(recipeClass.cast(recipe.value()));
        }
        return listBuilder.build();
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addCraftingStation(GEAR_CRAFTING_TYPE, Blocks.CRAFTING_TABLE);
        reg.addCraftingStation(ALLOY_MAKING_FABRIC_TYPE, SgBlocks.REFABRICATOR);
        reg.addCraftingStation(ALLOY_MAKING_GEM_TYPE, SgBlocks.RECRYSTALLIZER);
        reg.addCraftingStation(ALLOY_MAKING_METAL_TYPE, SgBlocks.ALLOY_FORGE);
        reg.addCraftingStation(ALLOY_MAKING_SUPER_TYPE, SgBlocks.SUPER_MIXER);
        reg.addCraftingStation(GRADING_TYPE, SgBlocks.MATERIAL_GRADER);
        reg.addCraftingStation(SALVAGING_TYPE, SgBlocks.SALVAGER);
        reg.addCraftingStation(TOOL_ACTION_TYPE, SgBlocks.STONE_ANVIL);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(RefabricatorScreen.class, 90, 30, 28, 23, ALLOY_MAKING_FABRIC_TYPE);
        reg.addRecipeClickArea(RecrystallizerScreen.class, 90, 30, 28, 23, ALLOY_MAKING_GEM_TYPE);
        reg.addRecipeClickArea(AlloyForgeScreen.class, 90, 30, 28, 23, ALLOY_MAKING_METAL_TYPE);
        reg.addRecipeClickArea(SuperMixerScreen.class, 90, 30, 28, 23, ALLOY_MAKING_SUPER_TYPE);
        reg.addRecipeClickArea(GraderScreen.class, 48, 30, 28, 23, GRADING_TYPE);
        reg.addRecipeClickArea(SalvagerScreen.class, 30, 30, 28, 23, SALVAGING_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        ISubtypeInterpreter<ItemStack> customMaterials = (ingredient, context) -> CustomMaterialItem.getMaterial(ingredient);
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

    private static String getDescKey(Identifier name) {
        return "jei." + name.getNamespace() + "." + name.getPath() + ".desc";
    }

    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        for (Material material : SgRegistries.MATERIAL.getValues(true)) {
            List<ItemStack> itemStacks = IngredientUtils.getItemList(material.getIngredient());
            List<String> aliases = new ArrayList<>(List.of("materials"));
            for (IMaterialCategory category : material.getCategories(MaterialInstance.of(material))) {
                aliases.add("materials/" + category.getName());
            }
            registration.addAliases(VanillaTypes.ITEM_STACK, itemStacks, aliases);
        }
    }
}
*/
