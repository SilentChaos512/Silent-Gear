package net.silentchaos512.gear.gear.part;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.part.PartCraftingData;
import net.silentchaos512.gear.api.part.PartDisplayData;
import net.silentchaos512.gear.api.part.PartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.Color;

import java.util.List;

public class UpgradeGearPart extends CoreGearPart {
    private final GearTypeMatcher upgradeGearTypes;

    public UpgradeGearPart(
            GearType gearType,
            PartType partType,
            GearTypeMatcher upgradeGearTypes,
            PartCraftingData crafting,
            PartDisplayData display,
            GearPropertyMap properties
    ) {
        super(gearType, partType, crafting, display, properties);
        this.upgradeGearTypes = upgradeGearTypes;
    }

    @Override
    public PartType getType() {
        return PartTypes.MISC_UPGRADE.get();
    }

    @Override
    public PartSerializer<?> getSerializer() {
        return PartSerializers.UPGRADE.get();
    }

    @Override
    public int getColor(PartInstance part, GearType gearType, int layer, int animationFrame) {
        return Color.VALUE_WHITE;
    }

    @Override
    public void addInformation(PartInstance part, ItemStack gear, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(1, part.getDisplayName(this.partType));
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartInstance part) {
        return this.upgradeGearTypes.test(GearHelper.getType(gear));
    }

    public static class Serializer extends PartSerializer<UpgradeGearPart> {
        private static final MapCodec<UpgradeGearPart> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        GearType.CODEC.fieldOf("gear_type").forGetter(p -> p.gearType),
                        PartType.CODEC.fieldOf("part_type").forGetter(p -> p.partType),
                        GearTypeMatcher.CODEC.fieldOf("upgrade_gear_types").forGetter(p -> p.upgradeGearTypes),
                        PartCraftingData.CODEC.fieldOf("crafting").forGetter(p -> p.crafting),
                        PartDisplayData.CODEC.fieldOf("display").forGetter(p -> p.display),
                        GearPropertyMap.CODEC.fieldOf("properties").forGetter(p -> p.properties)
                ).apply(instance, UpgradeGearPart::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, UpgradeGearPart> STREAM_CODEC = StreamCodec.composite(
                GearType.STREAM_CODEC, p -> p.gearType,
                PartType.STREAM_CODEC, p -> p.partType,
                GearTypeMatcher.STREAM_CODEC, p -> p.upgradeGearTypes,
                PartCraftingData.STREAM_CODEC, p -> p.crafting,
                PartDisplayData.STREAM_CODEC, p -> p.display,
                GearPropertyMap.STREAM_CODEC, p -> p.properties,
                UpgradeGearPart::new
        );

        public Serializer() {
            super(CODEC, STREAM_CODEC);
        }
    }
}
