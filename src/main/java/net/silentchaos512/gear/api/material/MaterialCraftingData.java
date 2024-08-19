package net.silentchaos512.gear.api.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialCategories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record MaterialCraftingData(
        Ingredient craftingItem,
        List<IMaterialCategory> categories,
        List<GearType> gearTypeBlacklist,
        Map<PartType, Ingredient> partSubstitutes,
        boolean canSalvage
) {
    public static final Codec<MaterialCraftingData> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(d -> d.craftingItem),
                    Codec.list(MaterialCategories.CODEC).fieldOf("categories").forGetter(d -> d.categories),
                    Codec.list(GearType.CODEC).fieldOf("gear_type_blacklist").forGetter(d -> d.gearTypeBlacklist),
                    Codec.unboundedMap(PartType.CODEC, Ingredient.CODEC_NONEMPTY).fieldOf("part_substitutes").forGetter(d -> d.partSubstitutes),
                    Codec.BOOL.fieldOf("can_salvage").forGetter(d -> d.canSalvage)
            ).apply(instance, MaterialCraftingData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialCraftingData> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, d -> d.craftingItem,
            MaterialCategories.STREAM_CODEC.apply(ByteBufCodecs.list()), d -> d.categories,
            GearType.STREAM_CODEC.apply(ByteBufCodecs.list()), d -> d.gearTypeBlacklist,
            ByteBufCodecs.map(
                    HashMap::new,
                    PartType.STREAM_CODEC,
                    Ingredient.CONTENTS_STREAM_CODEC
            ), d -> d.partSubstitutes,
            ByteBufCodecs.BOOL, d -> d.canSalvage,
            MaterialCraftingData::new
    );
}
