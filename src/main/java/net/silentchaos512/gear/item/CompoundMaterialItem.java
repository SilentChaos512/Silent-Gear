package net.silentchaos512.gear.item;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.AbstractMaterial;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundMaterialItem extends Item implements IColoredMaterialItem {
    public CompoundMaterialItem(Properties properties) {
        super(properties);
    }

    public static MaterialList getSubMaterials(ItemStack stack) {
        MaterialList ret = MaterialList.empty();

        ListTag listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_STRING);
        if (!listNbt.isEmpty()) {
            for (Tag nbt : listNbt) {
                IMaterial mat = MaterialManager.get(SilentGear.getIdWithDefaultNamespace(nbt.getAsString()));
                if (mat != null) {
                    ret.add(MaterialInstance.of(mat));
                }
            }
        } else {
            ListTag list = stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_COMPOUND);
            return MaterialList.deserializeNbt(list);
        }

        return ret;
    }

    public int getCraftedCount(ItemStack stack) {
        ListTag listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_STRING);
        return listNbt.size();
    }

    public ItemStack create(MaterialList materials) {
        return create(materials, materials.size());
    }

    public ItemStack create(MaterialList materials, int craftedCount) {
        ItemStack result = new ItemStack(this, craftedCount);
        MaterialList materialsWithoutEnhancements = MaterialList.of(materials.stream()
                .map(AbstractMaterial::removeEnhancements)
                .collect(Collectors.toList()));
        result.getOrCreateTag().put(NBT_MATERIALS, materialsWithoutEnhancements.serializeNbt());
        return result;
    }

    @Nullable
    private static IMaterialInstance getPrimaryMaterial(ItemStack stack) {
        IMaterialInstance first = MaterialList.deserializeFirst(stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_COMPOUND));
        if (first != null) {
            return first;
        }

        // Read old style
        ListTag listNbt = stack.getOrCreateTag().getList(NBT_MATERIALS, Tag.TAG_STRING);
        if (!listNbt.isEmpty()) {
            Tag nbt = listNbt.get(0);
            ResourceLocation id = ResourceLocation.tryParse(nbt.getAsString());
            if (id != null) {
                IMaterial material = MaterialManager.get(id);
                if (material != null) {
                    return MaterialInstance.of(material);
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public IMaterialInstance getPrimarySubMaterial(ItemStack stack) {
        return getPrimaryMaterial(stack);
    }

    public static String getModelKey(ItemStack stack) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + "#");

        if (!stack.hasTag()) {
            return s.append(Const.Materials.EXAMPLE.getId()).toString();
        }

        for (IMaterialInstance material : getSubMaterials(stack)) {
            s.append(SilentGear.shortenId(material.getId()));
        }

        return s.toString();
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        return ColorUtils.getBlendedColor(this, getSubMaterials(stack), layer);
    }

    public int getColorWeight(int index, int totalCount) {
        // TODO: Is this even needed? Should probably axe material order mattering.
        return 1;
    }

    @Override
    public Component getName(ItemStack stack) {
        IMaterialInstance material = getPrimaryMaterial(stack);
        Component text = material != null ? material.getDisplayName(PartType.MAIN) : TextUtil.misc("unknown");
        return new TranslatableComponent(this.getDescriptionId(), text);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        if(Config.Client.showMaterialTooltips.get()) {
            Collection<IMaterialInstance> materials = getSubMaterials(stack);

            TextListBuilder statsBuilder = new TextListBuilder();
            for (IMaterialInstance material : materials) {
                int nameColor = material.getNameColor(PartType.MAIN, GearType.ALL);
                statsBuilder.add(TextUtil.withColor(material.getDisplayName(PartType.MAIN).copy(), nameColor));
            }
            tooltip.addAll(statsBuilder.build());
        }
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            items.add(create(MaterialList.of(LazyMaterialInstance.of(Const.Materials.EXAMPLE)), 1));
        }
    }
}
