package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.lib.PartDataList;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.registry.ICustomMesh;
import net.silentchaos512.lib.util.LocalizationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class Blueprint extends Item implements IBlueprint, ICustomMesh {
    private static final String NAME = "blueprint";
    /** Maps gear to blueprints (not templates) */
    private static Map<String, Blueprint> ITEMS = new HashMap<>();

    private final boolean singleUse;
    @Nonnull
    private final ICoreItem gearItem;
    @Nonnull
    private final Function<PartDataList, ItemStack> craftingHandler;

    public Blueprint(boolean singleUse, @Nonnull ICoreItem gearItem, @Nonnull Function<PartDataList, ItemStack> craftingHandler) {
        this.singleUse = singleUse;
        if (!singleUse) {
            setContainerItem(this);
            ITEMS.put(gearItem.getGearClass(), this);
        }

        this.gearItem = gearItem;
        this.craftingHandler = craftingHandler;
    }

    /**
     * Gets the blueprint item (not template) associated with the gear item.
     * @return The Blueprint item, or null if it does not exist
     */
    @Nullable
    public static Blueprint getBlueprintForGear(ICoreItem item) {
        return ITEMS.get(item.getGearClass());
    }

    @Override
    public ItemStack getCraftingResult(ItemStack blueprint, Collection<ItemStack> parts) {
        return craftingHandler.apply(PartDataList.from(parts));
    }

    @Override
    public int getMaterialCost(ItemStack blueprint) {
        return this.gearItem.getConfig().getHeadCount();
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        LocalizationHelper loc = SilentGear.localization;
        String itemClass = this.gearItem.getGearClass();

        // Output item class
        String key = this.gearItem.getItem().getUnlocalizedName() + ".name";
        list.add(TextFormatting.AQUA + loc.getLocalizedString(key));

        // Material required for crafting
        int amount = this.gearItem.getConfig().getHeadCount();
        list.add(loc.getItemSubText(NAME, "materialAmount", amount));

        // Single use or multiple uses?
        if (this.singleUse) {
            list.add(TextFormatting.RED + loc.getItemSubText(NAME, "singleUse"));
        } else {
            list.add(TextFormatting.GREEN + loc.getItemSubText(NAME, "multiUse"));
        }

        // Is mixed material allowed in this GUI?
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCraftingStation) {
            list.add(TextFormatting.GREEN + loc.getItemSubText(NAME, "canMix"));
        } else {
            list.add(TextFormatting.RED + loc.getItemSubText(NAME, "noMixing"));
        }

        // Item recipe
        if (this.gearItem instanceof ICoreTool) {
            list.add("");
            if (KeyTrackerSL.isAltDown()) {
                String locToolName = loc.getItemSubText(itemClass, "name");
                list.add(TextFormatting.YELLOW + loc.getItemSubText(NAME, "itemRecipe1", locToolName));
                String toolHeadName = loc.getItemSubText("tool_head", itemClass);
                list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", 1, toolHeadName));

                int rodCount = this.gearItem.getConfig().getRodCount();
                if (rodCount > 0) {
                    String partName = loc.getLocalizedString("part", "type.rod");
                    list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", rodCount, partName));
                }
                int bowstringCount = this.gearItem.getConfig().getBowstringCount();
                if (bowstringCount > 0) {
                    String partName = loc.getLocalizedString("part", "type.bowstring");
                    list.add("  " + loc.getItemSubText(NAME, "itemRecipe2", bowstringCount, partName));
                }
            } else {
                list.add(TextFormatting.YELLOW + loc.getItemSubText(NAME, "altForRecipe"));
            }
        }

        // Shift key hint
        if (!KeyTrackerSL.isShiftDown()) {
            list.add(TextFormatting.DARK_GRAY + loc.getItemSubText(NAME, "shiftForIcon"));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack).replaceFirst("_[a-z]+$", "");
    }

    @Nonnull
    @Override
    public ItemMeshDefinition getCustomMesh() {
        return this::getModelFor;
    }

    private ModelResourceLocation modelNormal, modelWithOverlay;

    @Nonnull
    @Override
    public ResourceLocation[] getVariants() {
        ResourceLocation registryName = Objects.requireNonNull(getRegistryName());
        modelNormal = new ModelResourceLocation(registryName.toString().replaceFirst("_[a-z]+$", ""), "inventory");
        modelWithOverlay = new ModelResourceLocation(registryName, "inventory");
        return new ResourceLocation[]{modelNormal, modelWithOverlay};
    }

    @Nonnull
    private ModelResourceLocation getModelFor(ItemStack stack) {
        return !KeyTrackerSL.isShiftDown() ? modelNormal : modelWithOverlay;
    }
}
