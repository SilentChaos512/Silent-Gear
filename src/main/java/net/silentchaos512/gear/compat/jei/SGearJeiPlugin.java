package net.silentchaos512.gear.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.block.craftingstation.CraftingStationContainer;
import net.silentchaos512.gear.block.craftingstation.CraftingStationScreen;
import net.silentchaos512.gear.block.craftingstation.CraftingStationTileEntity;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.CustomTippedUpgrade;
import net.silentchaos512.gear.parts.PartManager;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JeiPlugin
public class SGearJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = SilentGear.getId("plugin/main");
    static final ResourceLocation PART_ANALYZER = SilentGear.getId("category/part_analyzer");
    static final ResourceLocation GUI_TEXTURE = SilentGear.getId("textures/gui/recipe_display.png");

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
        initFailed = true;

        IGuiHelper guiHelper = reg.getJeiHelpers().getGuiHelper();
        reg.addRecipeCategories(new PartAnalyzerCategory(guiHelper));

        initFailed = false;
    }

    @Override
    public void registerRecipes(IRecipeRegistration reg) {
        initFailed = true;

        // Part analyzer hints
        List<PartAnalyzerCategory.Recipe> analyzerHints = PartManager.getMains().stream()
                .map(PartAnalyzerCategory.Recipe::new)
                .filter(PartAnalyzerCategory.Recipe::isValid)
                .collect(Collectors.toList());
        reg.addRecipes(analyzerHints, PART_ANALYZER);

        // Info pages
        addInfoPage(reg, ModBlocks.CRAFTING_STATION);
        addInfoPage(reg, ModBlocks.PART_ANALYZER);
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
        for (ICoreItem item : ModItems.gearClasses.values()) {
            addInfoPage(reg, item);
        }

        initFailed = false;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration reg) {
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.CRAFTING_STATION), VanillaRecipeCategoryUid.CRAFTING);
        reg.addRecipeCatalyst(new ItemStack(ModBlocks.PART_ANALYZER), PART_ANALYZER);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration reg) {
        reg.addRecipeClickArea(CraftingStationScreen.class, 88, 32, 28, 23, VanillaRecipeCategoryUid.CRAFTING);
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

        reg.registerSubtypeInterpreter(ModItems.customTippedUpgrade, stack -> {
            ResourceLocation partId = CustomTippedUpgrade.getPartId(stack);
            return partId != null ? partId.toString() : "null";
        });

        initFailed = false;
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration reg) {
        reg.addRecipeTransferHandler(CraftingStationContainer.class, VanillaRecipeCategoryUid.CRAFTING,
                0, 9, 9, 36 + CraftingStationTileEntity.SIDE_INVENTORY_SIZE);
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
