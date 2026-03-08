package net.silentchaos512.gear.api.property;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.util.CodecUtils;

import javax.annotation.Nullable;
import java.util.Optional;

public record HarvestTier(
        String name,
        Optional<String> levelHint,
        TagKey<Block> incorrectForTool
) {
    public static final HarvestTier ZERO = create("zero", "0", BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    // Some common tiers for lazy material creation (extra mod metals, etc.)
    public static final HarvestTier WOOD = create("wood", "0", BlockTags.INCORRECT_FOR_WOODEN_TOOL);;
    public static final HarvestTier STONE = create("stone", "1", BlockTags.INCORRECT_FOR_STONE_TOOL);
    public static final HarvestTier GOLD = create("gold", "0", BlockTags.INCORRECT_FOR_GOLD_TOOL);
    public static final HarvestTier IRON = create("iron", "2", BlockTags.INCORRECT_FOR_IRON_TOOL);
    public static final HarvestTier DIAMOND = create("diamond", "3", BlockTags.INCORRECT_FOR_DIAMOND_TOOL);
    public static final HarvestTier NETHERITE = create("netherite", "4", BlockTags.INCORRECT_FOR_NETHERITE_TOOL);

    public static final Codec<HarvestTier> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ht -> ht.name),
                    Codec.STRING.optionalFieldOf("level_hint").forGetter(ht -> ht.levelHint),
                    TagKey.codec(Registries.BLOCK).fieldOf("incorrect_blocks_for_tool").forGetter(ht -> ht.incorrectForTool)
            ).apply(instance, HarvestTier::new)
    );

    public static final StreamCodec<FriendlyByteBuf, HarvestTier> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ht -> ht.name,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional), ht -> ht.levelHint,
            CodecUtils.tagStreamCodec(Registries.BLOCK), ht -> ht.incorrectForTool,
            HarvestTier::new
    );

    public boolean isBetterThan(@Nullable HarvestTier other) {
        if (other == null) return true;

        var thisHolderSet = BuiltInRegistries.BLOCK.getOrCreateTag(this.incorrectForTool);
        var otherHolderSet = BuiltInRegistries.BLOCK.getOrCreateTag(other.incorrectForTool);
        return thisHolderSet.size() < otherHolderSet.size();
    }

    public static HarvestTier create(String name, String levelHint) {
        return create(name, levelHint, SilentGear.getId("incorrect_for_" + name + "_tools"));
    }

    public static HarvestTier create(String name, String levelHint, ResourceLocation incorrectForToolTagLocation) {
        return create(name, levelHint, TagKey.create(Registries.BLOCK, incorrectForToolTagLocation));
    }

    public static HarvestTier create(String name, String levelHint, TagKey<Block> incorrectForToolTag) {
        return new HarvestTier(name, Optional.of(levelHint), incorrectForToolTag);
    }

    public Component getFormattedName() {
        return this.levelHint
                .map(hint -> Component.translatable("property.silentgear.harvest_tier.withLevelHint", this.name, hint))
                .orElseGet(() -> Component.literal(this.name));
    }
}
