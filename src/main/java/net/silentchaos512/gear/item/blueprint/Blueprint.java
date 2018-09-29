package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.lib.client.key.KeyTrackerSL;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.I18nHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class Blueprint extends Item implements IBlueprint, IColoredItem {
    private static final String NAME = "blueprint";
    private static final Map<String, Blueprint> ITEMS_BLUEPRINT = new HashMap<>();
    private static final Map<String, Blueprint> ITEMS_TEMPLATE = new HashMap<>();
    private static final int COLOR_OUTLINE_BLUEPRINT = 0xF1F1B9;
    private static final int COLOR_OUTLINE_TEMPLATE = 0xBAF2E2;

    private final boolean singleUse;
    @Nonnull
    private final ICoreItem gearItem;
    @Nonnull
    private final Function<PartDataList, ItemStack> craftingHandler;

    public Blueprint(boolean singleUse, @Nonnull ICoreItem gearItem, @Nonnull Function<PartDataList, ItemStack> craftingHandler) {
        this.singleUse = singleUse;
        if (!singleUse) {
            setContainerItem(this);
            ITEMS_BLUEPRINT.put(gearItem.getGearClass(), this);
        } else {
            ITEMS_TEMPLATE.put(gearItem.getGearClass(), this);
        }

        this.gearItem = gearItem;
        this.craftingHandler = craftingHandler;
    }

    /**
     * Gets the blueprint item (not template) associated with the gear item.
     * @return The Blueprint item, or null if it does not exist
     */
    @Nullable
    public static Blueprint getBlueprintForGear(ICoreItem item, boolean singleUse) {
        return singleUse ? ITEMS_TEMPLATE.get(item.getGearClass()) : ITEMS_BLUEPRINT.get(item.getGearClass());
    }

    @Nullable
    public static Ingredient getBlueprintIngredientForGear(ICoreItem item) {
        List<Blueprint> list = new ArrayList<>();
        Blueprint blueprint = getBlueprintForGear(item, false);
        Blueprint template = getBlueprintForGear(item, true);
        if (blueprint != null) list.add(blueprint);
        if (template != null) list.add(template);

        return !list.isEmpty() ? Ingredient.fromItems(list.toArray(new Item[0])) : null;
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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
        I18nHelper i18n = SilentGear.i18n;
        String itemClass = this.gearItem.getGearClass();

        // Output item class
        String key = this.gearItem.getItem().getTranslationKey() + ".name";
        list.add(TextFormatting.AQUA + i18n.translate(key));
        // Flavor text
        if (this.gearItem instanceof ICoreTool)
            list.add(TextFormatting.ITALIC + i18n.itemSubText(NAME, this.gearItem.getGearClass() + ".desc"));

        // Material required for crafting
        int amount = this.gearItem.getConfig().getHeadCount();
        list.add(i18n.itemSubText(NAME, "materialAmount", amount));

        // Single use or multiple uses?
        if (this.singleUse) {
            list.add(TextFormatting.RED + i18n.itemSubText(NAME, "singleUse"));
        } else {
            list.add(TextFormatting.GREEN + i18n.itemSubText(NAME, "multiUse"));
        }

        // Is mixed material allowed in this GUI?
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCraftingStation) {
            list.add(TextFormatting.GREEN + i18n.itemSubText(NAME, "canMix"));
        } else {
            list.add(TextFormatting.RED + i18n.itemSubText(NAME, "noMixing"));
        }

        // Item recipe
        if (this.gearItem instanceof ICoreTool) {
            list.add("");
            if (KeyTrackerSL.isAltDown()) {
                String locToolName = i18n.itemSubText(itemClass, "name");
                list.add(TextFormatting.YELLOW + i18n.itemSubText(NAME, "itemRecipe1", locToolName));
                String toolHeadName = i18n.itemSubText("tool_head", itemClass);
                list.add("  " + i18n.itemSubText(NAME, "itemRecipe2", 1, toolHeadName));

                for (PartType type : PartType.getValues()) {
                    if (type != PartType.MAIN) {
                        final int required = this.gearItem.getConfig().getCraftingPartCount(type);
                        if (required > 0) {
                            String partName = i18n.translate("part", "type." + type.getName());
                            list.add("  " + i18n.itemSubText(NAME, "itemRecipe2", required, partName));
                        }
                    }
                }
            } else {
                list.add(TextFormatting.YELLOW + i18n.itemSubText(NAME, "altForRecipe"));
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey(stack).replaceFirst("_[a-z]+$", "");
    }

    @Override
    public IItemColor getColorHandler() {
        return (stack, tintIndex) -> {
            if (tintIndex == 0) return Color.VALUE_WHITE;
            else return this.singleUse ? COLOR_OUTLINE_TEMPLATE : COLOR_OUTLINE_BLUEPRINT;
        };
    }
}
