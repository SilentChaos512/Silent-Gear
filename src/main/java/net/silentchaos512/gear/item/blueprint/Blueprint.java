package net.silentchaos512.gear.item.blueprint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.block.craftingstation.GuiCraftingStation;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.lib.item.IColoredItem;
import net.silentchaos512.utils.Color;

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
        super(new Properties().group(SilentGear.ITEM_GROUP));
        this.singleUse = singleUse;
        if (!singleUse) {
            ITEMS_BLUEPRINT.put(gearItem.getGearClass(), this);
        } else {
            ITEMS_TEMPLATE.put(gearItem.getGearClass(), this);
        }

        this.gearItem = gearItem;
        this.craftingHandler = craftingHandler;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return isSingleUse(itemStack) ? ItemStack.EMPTY : new ItemStack(this);
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return !isSingleUse(stack);
    }

    /**
     * Gets the blueprint item (not template) associated with the gear item.
     *
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
        // Blueprints/templates disabled?
        if (isDisabled())
            return ItemStack.EMPTY;

        final PartDataList partList = PartDataList.from(parts);
        for (PartData part : partList) {
            // Block blacklisted parts
//            if (part.getPart().isBlacklisted())
//                return ItemStack.EMPTY;
        }

        return craftingHandler.apply(partList);
    }

    @Override
    public int getMaterialCost(ItemStack blueprint) {
        // FIXME
        return 0;
    }

    @Override
    public boolean isSingleUse(ItemStack blueprint) {
        return singleUse;
    }

    public boolean isDisabled() {
        BlueprintType config = Config.GENERAL.blueprintTypes.get();
        return singleUse && !config.allowTemplate()
                || !singleUse && !config.allowBlueprint();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> list, ITooltipFlag flag) {
        String itemClass = this.gearItem.getGearClass();

        // Output item class
        list.add(gearItem.asItem().getName().applyTextStyle(TextFormatting.AQUA));
        // Flavor text
        if (this.gearItem instanceof ICoreTool) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint." + gearItem.getGearClass() + ".desc")
                    .applyTextStyle(TextFormatting.ITALIC));
        }

        // Material required for crafting
        list.add(new TextComponentTranslation("item.silentgear.blueprint.materialAmount",
                this.getMaterialCost(stack)));

        // Single use or multiple uses? Or disabled?
        if (isDisabled()) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.disabled")
                    .applyTextStyle(TextFormatting.DARK_RED));
        } else if (this.singleUse) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.singleUse")
                    .applyTextStyle(TextFormatting.RED));
        } else {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.multiUse")
                    .applyTextStyle(TextFormatting.GREEN));
        }

        // Is mixed material allowed in this GUI?
        if (Minecraft.getInstance().currentScreen instanceof GuiCraftingStation) {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.canMix")
                    .applyTextStyle(TextFormatting.GREEN));
        } else {
            list.add(new TextComponentTranslation("item.silentgear.blueprint.noMixing")
                    .applyTextStyle(TextFormatting.RED));
        }

        // Item recipe
        if (this.gearItem instanceof ICoreTool) {
            list.add(new TextComponentString(""));
            if (KeyTracker.isAltDown()) {
                ITextComponent textItemClass = new TextComponentTranslation("item.silentgear.blueprint." + itemClass);
                ITextComponent textLine1 = new TextComponentTranslation("item.silentgear.blueprint.blueprint.itemRecipe1", textItemClass);
                list.add(textLine1.applyTextStyle(TextFormatting.YELLOW));

                ITextComponent textToolHead = new TextComponentTranslation("item.silentgear.blueprint.tool_head." + itemClass);
                ITextComponent textLine2 = new TextComponentTranslation("item.silentgear.blueprint.blueprint.itemRecipe2", 1, textToolHead);
                list.add(new TextComponentString("  ").appendSibling(textLine2.applyTextStyle(TextFormatting.YELLOW)));

                for (PartType type : PartType.getValues()) {
                    if (type != PartType.MAIN) {
                        // FIXME
                        final int required = 0; //this.gearItem.getConfig().getCraftingPartCount(type);
                        if (required > 0) {
                            ITextComponent textName = type.getDisplayName(0);
                            ITextComponent text = new TextComponentTranslation("item.silentgear.blueprint.itemRecipe2", required, textName);
                            list.add(new TextComponentString("  ").appendSibling(text));
                        }
                    }
                }
            } else {
                list.add(new TextComponentTranslation("item.silentgear.blueprint.altForRecipe")
                        .applyTextStyle(TextFormatting.YELLOW));
            }
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!isDisabled()) super.fillItemGroup(group, items);
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
