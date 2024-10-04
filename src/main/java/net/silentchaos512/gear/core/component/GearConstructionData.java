package net.silentchaos512.gear.core.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.CoreGearPart;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public record GearConstructionData(
        PartList parts,
        boolean isExample,
        int brokenCount,
        int repairedCount
) {
    public static final GearConstructionData EMPTY = new GearConstructionData(PartList.empty(), true, 0, 0);

    public static final Codec<GearConstructionData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    PartList.CODEC.fieldOf("parts").forGetter(d -> d.parts),
                    Codec.BOOL.fieldOf("example").forGetter(d -> d.isExample),
                    Codec.INT.fieldOf("broken_count").forGetter(d -> d.brokenCount),
                    Codec.INT.fieldOf("repairedCount").forGetter(d -> d.repairedCount)
            ).apply(instance, GearConstructionData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GearConstructionData> STREAM_CODEC = StreamCodec.composite(
            PartList.STREAM_CODEC, d -> d.parts,
            ByteBufCodecs.BOOL, d -> d.isExample,
            ByteBufCodecs.VAR_INT, d -> d.brokenCount,
            ByteBufCodecs.VAR_INT, d -> d.repairedCount,
            GearConstructionData::new
    );

    @Nullable
    public PartInstance getPartOfType(Supplier<PartType> type) {
        return getPartOfType(type.get());
    }

    @Nullable
    public PartInstance getPartOfType(PartType type) {
        for (PartInstance part : parts) {
            if (part.getType() == type) {
                return part;
            }
        }
        return null;
    }

    @Nullable
    public PartInstance getPrimaryPart() {
        return getPartOfType(PartTypes.MAIN);
    }

    @Nullable
    public PartInstance getCoatingOrMainPart() {
        var coating = getPartOfType(PartTypes.COATING);
        return coating != null ? coating : getPrimaryPart();
    }

    @Nullable
    public MaterialInstance getMainTextureMaterial() {
        var part = getCoatingOrMainPart();
        if (part != null && part.isValid() && part.get() instanceof CoreGearPart) {
            return CompoundPartItem.getPrimaryMaterial(part.getItem());
        }
        return null;
    }
}
