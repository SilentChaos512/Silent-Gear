package net.silentchaos512.gear.api.part;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;

import java.util.List;

public record PartCraftingData(
        Ingredient craftingItem,
        List<GearType> gearTypeBlacklist,
        boolean canSalvage
) {
    public static final Codec<PartCraftingData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(d -> d.craftingItem),
                    Codec.list(GearType.CODEC).fieldOf("gear_type_blacklist").forGetter(d -> d.gearTypeBlacklist),
                    Codec.BOOL.fieldOf("can_salvage").forGetter(d -> d.canSalvage)
            ).apply(instance, PartCraftingData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PartCraftingData> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, d -> d.craftingItem,
            GearType.STREAM_CODEC.apply(ByteBufCodecs.list()), d -> d.gearTypeBlacklist,
            ByteBufCodecs.BOOL, d -> d.canSalvage,
            PartCraftingData::new
    );
}
