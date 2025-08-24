package net.silentchaos512.gear.client.color.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.setup.SgRegistries;
import org.jetbrains.annotations.Nullable;

public record GearPartColor(Holder<PartType> partType) implements ItemTintSource {
    public static final MapCodec<GearPartColor> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    SgRegistries.PART_TYPE.holderByNameCodec().fieldOf("part_type").forGetter(GearPartColor::partType)
            ).apply(instance, GearPartColor::new)
    );

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (this.partType.isBound()) {
            return ColorUtils.getBlendedColorForPartInGear(stack, this.partType.value());
        }
        // Part type not valid?
        return 0xFFFF00FF;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
