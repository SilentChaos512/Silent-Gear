package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.block.salvager.SalvagerScreen;
import net.silentchaos512.gear.crafting.recipe.ShapedGearRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessCompoundPartRecipe;
import net.silentchaos512.gear.crafting.recipe.ShapelessGearRecipe;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModRecipes;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomTippedUpgrade;
import net.silentchaos512.gear.util.Const;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class SGearJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = SilentGear.getId("plugin/main");
    static final ResourceLocation GUI_TEXTURE = SilentGear.getId("textures/gui/recipe_display.png");
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
        reg.addRecipeCategories(new SalvagingRecipeCategoryJei(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        initFailed = true;

        assert Minecraft.getInstance().world != null;
        RecipeManager recipeManager = Minecraft.getInstance().world.getRecipeManager();

        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(SGearJeiPlugin::isGearCraftingRecipe)
                .collect(Collectors.toList()), GEAR_CRAFTING);

        reg.addRecipes(recipeManager.getRecipes().stream()
                .filter(r -> r.getType() == ModRecipes.SALVAGING_TYPE)
                .collect(Collectors.toList()), Const.SALVAGING);

        // FIXME: Fails on servers
/*        addInfoPage(reg, "tip_upgrade", PartManager.getPartsOfType(PartType.TIP).stream()
                .flatMap(part -> {
                    Ingredient ingredient = part.getMaterials().getIngredient();
                    return ingredient != null ? Arrays.stream(ingredient.getMatchingStacks()) : Stream.of();
                })
                .map(ItemStack::getItem)
                .collect(Collectors.toList())
        );*/
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
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.SALVAGER), Const.SALVAGING);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(SalvagerScreen.class, 30, 30, 28, 23, Const.SALVAGING);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration reg) {
        initFailed = true;

//        ModItems.gearClasses.forEach((id, item) ->
//                reg.registerSubtypeInterpreter(item.asItem(), stack -> {
//                    PartData part = GearData.getPrimaryPart(stack);
//                    return part != null ? id + "|" + part.getPart().getId() : id;
//                })
//        );

        reg.registerSubtypeInterpreter(ModItems.CUSTOM_TIPPED_UPGRADE.get(), stack -> {
            ResourceLocation partId = CustomTippedUpgrade.getPartId(stack);
            return partId != null ? partId.toString() : "null";
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
