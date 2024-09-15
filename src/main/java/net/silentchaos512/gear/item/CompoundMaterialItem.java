package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.gear.material.AbstractMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompoundMaterialItem extends Item implements IColoredMaterialItem {
    public CompoundMaterialItem(Properties properties) {
        super(properties);
    }

    public static List<MaterialInstance> getSubMaterials(ItemStack stack) {
        var materialList = stack.get(SgDataComponents.MATERIAL_LIST);
        return materialList != null ? materialList : Collections.emptyList();
    }

    public ItemStack create(List<MaterialInstance> materials) {
        return create(materials, materials.size());
    }

    public ItemStack create(List<MaterialInstance> materials, int craftedCount) {
        ItemStack result = new ItemStack(this, craftedCount);
        List<MaterialInstance> materialsWithoutEnhancements = materials.stream()
                .map(AbstractMaterial::removeEnhancements)
                .toList();
        result.set(SgDataComponents.MATERIAL_LIST, materialsWithoutEnhancements);
        return result;
    }

    @Nullable
    private static MaterialInstance getPrimaryMaterial(ItemStack stack) {
        var materialList = stack.get(SgDataComponents.MATERIAL_LIST);
        return materialList != null ? materialList.getFirst() : null;
    }

    @Nullable
    @Override
    public MaterialInstance getPrimarySubMaterial(ItemStack stack) {
        return getPrimaryMaterial(stack);
    }

    public static String getModelKey(ItemStack stack) {
        return SilentGear.shortenId(NameUtils.fromItem(stack)) + "#" +
                getSubMaterials(stack).stream()
                        .map(mat -> SilentGear.shortenId(mat.getId()))
                        .collect(Collectors.joining(","));
    }

    @Override
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            return ColorUtils.getBlendedColorForCompoundMaterial(getSubMaterials(stack));
        }
        return 0xFFFFFFFF;
    }

    @Override
    public Component getName(ItemStack stack) {
        MaterialInstance material = getPrimaryMaterial(stack);
        Component text = material != null ? material.getDisplayName(PartTypes.MAIN.get()) : TextUtil.misc("unknown");
        return Component.translatable(this.getDescriptionId(), text);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        if (Config.Client.showMaterialTooltips.get()) {
            List<MaterialInstance> materials = getSubMaterials(stack);
            List<TraitInstance> traits = TraitHelper.getTraitsFromComponents(materials, PartGearKey.of(GearTypes.ALL, PartTypes.MAIN));

            float synergy = SynergyUtils.getSynergy(PartTypes.MAIN.get(), materials, traits);
            tooltip.add(SynergyUtils.getDisplayText(synergy));

            TextListBuilder statsBuilder = new TextListBuilder();
            for (MaterialInstance material : materials) {
                int nameColor = material.getNameColor(PartTypes.MAIN.get(), GearTypes.ALL.get());
                statsBuilder.add(TextUtil.withColor(material.getDisplayName(PartTypes.MAIN.get()).copy(), nameColor));
            }
            tooltip.addAll(statsBuilder.build());
        }
    }
}
