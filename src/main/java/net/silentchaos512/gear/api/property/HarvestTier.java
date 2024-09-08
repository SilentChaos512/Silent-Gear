package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.CodecUtils;

import javax.annotation.Nullable;

public record HarvestTier(
        String name,
        TagKey<Block> incorrectForTool
) {
    public static final HarvestTier ZERO = create("wood", BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    // Some common tiers for lazy material creation (extra mod metals, etc.)
    public static final HarvestTier WOOD = ZERO;
    public static final HarvestTier STONE = create("stone", BlockTags.INCORRECT_FOR_STONE_TOOL);
    public static final HarvestTier GOLD = create("gold", BlockTags.INCORRECT_FOR_GOLD_TOOL);
    public static final HarvestTier IRON = create("iron", BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final HarvestTier DIAMOND = create("diamond", BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
    public static final HarvestTier NETHERITE = create("netherite", BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

    public static final Codec<HarvestTier> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ht -> ht.name),
                    TagKey.codec(Registries.BLOCK).fieldOf("incorrect_blocks_for_tool").forGetter(ht -> ht.incorrectForTool)
            ).apply(instance, HarvestTier::new)
    );

    public static final StreamCodec<FriendlyByteBuf, HarvestTier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ht -> ht.name,
            CodecUtils.tagStreamCodec(Registries.BLOCK), ht -> ht.incorrectForTool,
            HarvestTier::new
    );

    public boolean isBetterThan(@Nullable HarvestTier other) {
        if (other == null) return true;

        var thisHolderSet = BuiltInRegistries.BLOCK.getOrCreateTag(this.incorrectForTool);
        var otherHolderSet = BuiltInRegistries.BLOCK.getOrCreateTag(other.incorrectForTool);
        return thisHolderSet.size() < otherHolderSet.size();
    }

    public static HarvestTier create(String name) {
        return create(name, SilentGear.getId("incorrect_for_" + name + "_tools"));
    }

    public static HarvestTier create(String name, ResourceLocation incorrectForToolTagLocation) {
        return create(name, TagKey.create(Registries.BLOCK, incorrectForToolTagLocation));
    }

    public static HarvestTier create(String name, TagKey<Block> incorrectForToolTag) {
        return new HarvestTier(name, incorrectForToolTag);
    }
}
