package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class FragmentItem extends Item {
    private static final String NBT_MATERIAL = "Material";

    public FragmentItem(Properties properties) {
        super(properties);
    }

    public ItemStack create(IMaterialInstance material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().put(NBT_MATERIAL, material.write(new CompoundNBT()));
        return stack;
    }

    @Nullable
    public static IMaterialInstance getMaterial(ItemStack stack) {
        if (stack.getOrCreateTag().contains(NBT_MATERIAL, Constants.NBT.TAG_COMPOUND)) {
            return MaterialInstance.read(stack.getOrCreateTag().getCompound(NBT_MATERIAL));
        }

        // Old, pre-compound style
        ResourceLocation id = ResourceLocation.tryParse(stack.getOrCreateTag().getString(NBT_MATERIAL));
        IMaterial material = MaterialManager.get(id);
        if (material != null) {
            return MaterialInstance.of(material);
        }
        return null;
    }

    public static String getModelKey(ItemStack stack) {
        if (stack.getOrCreateTag().contains(NBT_MATERIAL, Constants.NBT.TAG_COMPOUND)) {
            MaterialInstance material = MaterialInstance.read(stack.getOrCreateTag().getCompound(NBT_MATERIAL));
            if (material != null) {
                return material.getModelKey();
            }
        }
        return stack.getOrCreateTag().getString(NBT_MATERIAL);
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        IMaterialInstance material = getMaterial(stack);
        if (material == null) {
            return new TranslationTextComponent(this.getDescriptionId(stack) + ".invalid");
        }
        return new TranslationTextComponent(this.getDescriptionId(stack), material.getDisplayName(PartType.MAIN));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (!this.allowdedIn(group)) return;

        items.add(new ItemStack(this));

        if (SilentGear.isDevBuild()) {
            for (IMaterial material : MaterialManager.getValues()) {
                items.add(create(MaterialInstance.of(material), 1));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "fragment.hint").withStyle(TextFormatting.ITALIC));
    }
}
