package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

public class FragmentItem extends Item {
    private static final String NBT_MATERIAL = "Material";

    public FragmentItem(Properties properties) {
        super(properties);
    }

    public ItemStack create(IMaterial material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().putString(NBT_MATERIAL, SilentGear.shortenId(material.getId()));
        return stack;
    }

    @Nullable
    public static IMaterialInstance getMaterial(ItemStack stack) {
        ResourceLocation id = ResourceLocation.tryCreate(stack.getOrCreateTag().getString(NBT_MATERIAL));
        IMaterial material = MaterialManager.get(id);
        if (material != null) {
            return MaterialInstance.of(material);
        }
        return null;
    }

    public static String getModelKey(ItemStack stack) {
        return stack.getOrCreateTag().getString(NBT_MATERIAL);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        IMaterialInstance material = getMaterial(stack);
        if (material == null) {
            return new TranslationTextComponent(this.getTranslationKey(stack) + ".invalid");
        }
        return new TranslationTextComponent(this.getTranslationKey(stack), material.getDisplayName(PartType.MAIN));
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (!this.isInGroup(group)) return;

        items.add(new ItemStack(this));

        if (SilentGear.isDevBuild()) {
            for (IMaterial material : MaterialManager.getValues()) {
                items.add(create(material, 1));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "fragment.hint").mergeStyle(TextFormatting.ITALIC));
    }
}
