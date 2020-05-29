package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PartItem extends Item {
    private static final String NBT_MATERIALS = "Materials";

    private final ResourceLocation partId;

    public PartItem(ResourceLocation partId, Properties properties) {
        super(properties);
        this.partId = partId;
    }

    public ItemStack createFromItems(Collection<ItemStack> materials) {
        // TODO: Ignores invalid items, is that the best thing to do?
        return create(materials.stream()
                .map(MaterialManager::from)
                .filter(Objects::nonNull)
                .map(MaterialInstance::of)
                .collect(Collectors.toList()));
    }

    public ItemStack create(Collection<IMaterialInstance> materials) {
        ListNBT materialListNbt = new ListNBT();
        materials.forEach(m -> materialListNbt.add(m.write(new CompoundNBT())));

        CompoundNBT tag = new CompoundNBT();
        tag.put(NBT_MATERIALS, materialListNbt);
        ItemStack result = new ItemStack(this);
        result.setTag(tag);

        return result;
    }

    public Collection<IMaterialInstance> getMaterials(ItemStack stack) {
        ListNBT materialListNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, 10);
        return materialListNbt.stream()
                .filter(nbt -> nbt instanceof CompoundNBT)
                .map(nbt -> (CompoundNBT) nbt)
                .map(MaterialInstance::read)
                .collect(Collectors.toList());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (flagIn.isAdvanced()) {
            tooltip.add(new StringTextComponent("Part ID: " + this.partId).applyTextStyle(TextFormatting.DARK_GRAY));
        }
    }
}
