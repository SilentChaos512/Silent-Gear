package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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

    public ItemStack create(IMaterialInstance material, int count) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().put(NBT_MATERIAL, material.write(new CompoundTag()));
        return stack;
    }

    @Nullable
    public static IMaterialInstance getMaterial(ItemStack stack) {
        if (stack.getOrCreateTag().contains(NBT_MATERIAL, Tag.TAG_COMPOUND)) {
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
        if (stack.getOrCreateTag().contains(NBT_MATERIAL, Tag.TAG_COMPOUND)) {
            MaterialInstance material = MaterialInstance.read(stack.getOrCreateTag().getCompound(NBT_MATERIAL));
            if (material != null) {
                return material.getModelKey();
            }
        }
        return stack.getOrCreateTag().getString(NBT_MATERIAL);
    }

    @Override
    public Component getName(ItemStack stack) {
        IMaterialInstance material = getMaterial(stack);
        if (material == null) {
            return Component.translatable(this.getDescriptionId(stack) + ".invalid");
        }
        return Component.translatable(this.getDescriptionId(stack), material.getDisplayName(PartType.MAIN));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (!this.allowedIn(group)) return;

        items.add(new ItemStack(this));

        if (SilentGear.isDevBuild()) {
            for (IMaterial material : MaterialManager.getValues()) {
                items.add(create(MaterialInstance.of(material), 1));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "fragment.hint").withStyle(ChatFormatting.ITALIC));
    }
}
