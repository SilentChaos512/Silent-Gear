package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.block.grader.GraderScreen;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;
import net.silentchaos512.gear.crafting.recipe.ShapedGearRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessGearRecipe;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CraftingItems;
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

    private static boolean initFailed = false;

    public static boolean hasInitFailed() {
        return initFailed;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration reg) {
        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new GearCraftingRecipeCategoryJei(guiHelper));
        reg.addRecipeCategories(new MaterialGraderRecipeCategory(guiHelper));
        reg.addRecipeCategories(new SalvagingRecipeCategoryJei(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        initFailed = true;

        assert Minecraft.getInstance().world != null;
        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        // Repair kit hint
        for (RepairKitItem item : Registration.getItems(RepairKitItem.class)) {
            String itemName = NameUtils.fromItem(item).getPath();
            reg.addRecipes(Collections.singleton(new ShapelessRecipe(SilentGear.getId(itemName + "_fill_hint"), "",
                            new ItemStack(item),
                            NonNullList.from(Ingredient.EMPTY,
                                    Ingredient.fromItems(item),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN),
                                    PartMaterialIngredient.of(PartType.MAIN)
                            ))),
                    VanillaRecipeCategoryUid.CRAFTING);
        }

        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(SGearJeiPlugin::isGearCraftingRecipe)
                .collect(Collectors.toList()), GEAR_CRAFTING);

        // Grading
        reg.addRecipes(Collections.singleton(new MaterialGraderRecipeCategory.GraderRecipe()), Const.GRADING);

        // Salvaging
        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == ModRecipes.SALVAGING_TYPE)
                .collect(Collectors.toList()), Const.SALVAGING);

        addInfoPage(reg, CraftingItems.RED_CARD_UPGRADE);
        addInfoPage(reg, CraftingItems.SPOON_UPGRADE);
        for (Item item : Registration.getItems(item -> item instanceof ICoreTool)) {
            addInfoPage(reg, item);
        }

        initFailed = false;
    }

    private static boolean isGearCraftingRecipe(IRecipe<?> recipe) {
        IRecipeSerializer<?> serializer = recipe.getSerializer();
        return serializer == ShapedGearRecipe.SERIALIZER || serializer == ShapelessGearRecipe.SERIALIZER || serializer == ShapelessCompoundPartRecipe.SERIALIZER;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), GEAR_CRAFTING);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.MATERIAL_GRADER), Const.GRADING);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.SALVAGER), Const.SALVAGING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(GraderScreen.class, 48, 30, 28, 23, Const.GRADING);
        reg.addRecipeClickArea(SalvagerScreen.class, 30, 30, 28, 23, Const.SALVAGING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        initFailed = true;

        reg.registerSubtypeInterpreter(ModItems.FRAGMENT.get(), stack -> {
            IMaterial material = FragmentItem.getMaterial(stack);
            return material != null ? material.getId().toString() : "";
        });

        initFailed = false;
    }

    private static void addInfoPage(IRecipeRegistration reg, IItemProvider item) {
        String key = getDescKey(Objects.requireNonNull(item.asItem().getRegistryName()));
        ItemStack stack = new ItemStack(item);
        reg.addIngredientInfo(stack, VanillaTypes.ITEM, key);
    }

    private static void addInfoPage(IRecipeRegistration reg, String name, Collection<IItemProvider> items) {
        String key = getDescKey(SilentGear.getId(name));
        List<ItemStack> stacks = items.stream().map(ItemStack::new).collect(Collectors.toList());
        reg.addIngredientInfo(stacks, VanillaTypes.ITEM, key);
    }

    private static void addInfoPage(IRecipeRegistration reg, IItemProvider item, Stream<ItemStack> variants) {
        String key = getDescKey(Objects.requireNonNull(item.asItem().getRegistryName()));
        reg.addIngredientInfo(variants.collect(Collectors.toList()), VanillaTypes.ITEM, key);
    }

    private static String getDescKey(ResourceLocation name) {
        return "jei." + name.getNamespace() + "." + name.getPath() + ".desc";
    }
}
