package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CompoundPartItem extends Item {
    private final Supplier<PartType> partType;

    public CompoundPartItem(Supplier<PartType> partType, Properties properties) {
        super(properties.component(SgDataComponents.MATERIAL_LIST, List.of(MaterialInstance.of(Const.Materials.EXAMPLE))));
        this.partType = partType;
    }

    public PartType getPartType() {
        return partType.get();
    }

    public GearType getGearType() {
        return GearTypes.NONE.get();
    }

    public static List<MaterialInstance> getMaterials(ItemInstance instance) {
        return instance.getOrDefault(SgDataComponents.MATERIAL_LIST, List.of());
    }

    @Nullable
    public static MaterialInstance getPrimaryMaterial(ItemInstance instance) {
        var materials = getMaterials(instance);
        return !materials.isEmpty() ? materials.getFirst() : null;
    }

    public ItemStack create(MaterialInstance material) {
        return create(material, 1);
    }

    public ItemStack create(MaterialInstance material, @Nonnegative int materialCount) {
        var materials = new ArrayList<MaterialInstance>();
        for (int i = 0; i < materialCount; ++i) {
            materials.add(material);
        }
        return create(materials);
    }

    public ItemStack create(List<MaterialInstance> materials) {
        ItemStack result = new ItemStack(this);
        result.set(SgDataComponents.MATERIAL_LIST, List.copyOf(materials));
        return result;
    }

    public static String getModelKey(ItemStack stack) {
        StringBuilder s = new StringBuilder(SilentGear.shortenId(NameUtils.fromItem(stack)) + "#");
        var materials = getMaterials(stack);

        for (var material : materials) {
            s.append(SilentGear.shortenId(material.getId()));
        }

        return s.toString();
    }

    @Deprecated
    public int getColor(ItemStack stack, int layer) {
        if (layer == 0) {
            var primaryMaterial = getPrimaryMaterial(stack);
            return primaryMaterial != null
                    ? primaryMaterial.getColor(getGearType(), getPartType()) | 0xFF000000
                    : 0xFFFFFFFF;
        }
        return 0xFFFFFFFF;
    }

    public int getColorWeight(int index, int totalCount) {
        return totalCount - index;
    }

    @Override
    public Component getName(ItemStack stack) {
        PartInstance part = PartInstance.from(stack);
        MaterialInstance material = getPrimaryMaterial(stack);
        if (part != null && material != null) {
            var materialDisplayName = material.getDisplayName(partType.get(), ItemStack.EMPTY);
            var nameText = Component.translatable(this.getDescriptionId() + ".nameProper", materialDisplayName);
            int nameColor = Color.blend(part.getColor(ItemStack.EMPTY), 0xFFFFFFFF, 0.25f) & 0xFFFFFF;
            return TextUtil.withColor(nameText, nameColor);
        }
        return super.getName(stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        var basicItemName = Component.translatable(this.getDescriptionId());
        var gearPartText = Component.translatable("item.silentgear.compound_part.part_name", basicItemName);
        tooltipAdder.accept(gearPartText.withStyle(ChatFormatting.ITALIC));

        PartInstance part = PartInstance.from(stack);
        MaterialInstance material = getPrimaryMaterial(stack);

        if (part != null && material != null && Config.Client.showPartTooltips.get()) {
            var nameColor = material.getNameColor(part.getType(), this.getGearType());
            var displayNameWithModifiers = material.getSimpleNameWithModifiers();
            tooltipAdder.accept(TextUtil.withColor(displayNameWithModifiers, nameColor));
        }
    }
}
