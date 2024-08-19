package net.silentchaos512.gear.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.silentchaos512.gear.setup.gear.GearTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class GearTypeMatcher implements Predicate<GearType> {
    public static final GearTypeMatcher ALL = new GearTypeMatcher(true, GearTypes.ALL.get());

    public static final Codec<GearTypeMatcher> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.BOOL.fieldOf("match_parents").forGetter(m -> m.matchParents),
                    Codec.list(GearType.CODEC).fieldOf("types").forGetter(m -> m.types)
            ).apply(instance, GearTypeMatcher::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, GearTypeMatcher> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, m -> m.matchParents,
            GearType.STREAM_CODEC.apply(ByteBufCodecs.list()), m -> m.types,
            GearTypeMatcher::new
    );

    private final List<GearType> types = new ArrayList<>();
    private final boolean matchParents;

    public GearTypeMatcher(boolean matchParents, GearType... typesIn) {
        this(matchParents, Arrays.asList(typesIn));
    }

    public GearTypeMatcher(boolean matchParents, List<GearType> typesIn) {
        this.matchParents = matchParents;
        this.types.addAll(typesIn);
    }

    @Override
    public boolean test(GearType gearType) {
        for (GearType type : this.types) {
            if (this.matchParents) {
                if (gearType.matches(type)) {
                    return true;
                }
            } else if (gearType == type) {
                return true;
            }
        }
        return false;
    }
}
