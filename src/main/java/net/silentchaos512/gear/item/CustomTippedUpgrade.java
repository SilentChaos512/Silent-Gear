package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.crafting.ingredient.CustomTippedUpgradeIngredient;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;

import javax.annotation.Nullable;
import java.util.List;

public class CustomTippedUpgrade extends Item {
    public CustomTippedUpgrade() {
        super(new Properties().group(SilentGear.ITEM_GROUP));
    }

    public static ItemStack getStack(ResourceLocation partId) {
        ItemStack stack = new ItemStack(ModItems.customTippedUpgrade);
        stack.getOrCreateTag().putString("PartID", partId.toString());
        return stack;
    }

    @Nullable
    public static ResourceLocation getPartId(ItemStack stack) {
        return ResourceLocation.tryCreate(stack.getOrCreateTag().getString("PartID"));
    }

    @Nullable
    public static IGearPart getPart(ItemStack stack) {
        String partId = stack.getOrCreateTag().getString("PartID");
        return !partId.isEmpty() ? PartManager.get(partId) : null;
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            IGearPart part = getPart(stack);
            if (part != null) {
                return PartData.of(part).getColor(ItemStack.EMPTY, 0);
            }
        }
        return 0xFFFFFF;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IGearPart part = getPart(stack);
        if (part != null) {
            ITextComponent partDisplayName = PartData.of(part).getDisplayName(ItemStack.EMPTY);
            return new TranslationTextComponent("item.silentgear.custom_tipped_upgrade.nameProper", partDisplayName);
        }
        return super.getDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        IGearPart part = getPart(stack);
        if (part != null) {
            tooltip.add(PartData.of(part).getDisplayName(ItemStack.EMPTY));
        }
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!isInGroup(group)) return;
        PartManager.getValues().stream()
                .filter(p -> p.getMaterials().getNormal() instanceof CustomTippedUpgradeIngredient)
                .forEach(p -> items.add(getStack(p.getId())));
    }
}
