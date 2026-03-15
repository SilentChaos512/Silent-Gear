package net.silentchaos512.gear.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.client.util.TextListBuilder;
import net.silentchaos512.gear.gear.material.AbstractMaterial;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
        Component text = material != null ? material.getSimpleName() : TextUtil.misc("unknown");
        return Component.translatable(this.getDescriptionId(), text);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (Config.Client.showMaterialTooltips.get()) {
            MaterialInstance material = MaterialInstance.from(stack);
            if (material == null) return;

            List<MaterialInstance> subMaterials = getSubMaterials(stack);
            List<TraitInstance> traits = material.getProperty(PartTypes.MAIN, PropertyKey.of(GearProperties.TRAITS, GearTypes.ALL));

            float synergy = SynergyUtils.getSynergy(PartTypes.MAIN.get(), subMaterials, traits);
            tooltipAdder.accept(SynergyUtils.getDisplayText(synergy));

            TextListBuilder materialListBuilder = new TextListBuilder();
            for (MaterialInstance subMaterial : subMaterials) {
                int nameColor = subMaterial.getNameColor(PartTypes.MAIN.get(), GearTypes.ALL.get());
                materialListBuilder.add(TextUtil.withColor(subMaterial.getSimpleName().copy(), nameColor));
            }
            materialListBuilder.build().forEach(tooltipAdder);
        }
    }
}
