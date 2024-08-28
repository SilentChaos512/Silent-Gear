package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.CodecUtils;

import java.util.Collection;
import java.util.List;

public class BlockMiningSpeedTraitEffect extends TraitEffect {
    public static final MapCodec<BlockMiningSpeedTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TagKey.codec(Registries.BLOCK).fieldOf("block_tag").forGetter(e -> e.blocks),
                    Codec.FLOAT.fieldOf("speed_modifier").forGetter(e -> e.speedModifier)
            ).apply(instance, BlockMiningSpeedTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockMiningSpeedTraitEffect> STREAM_CODEC = StreamCodec.composite(
            CodecUtils.tagStreamCodec(Registries.BLOCK), e -> e.blocks,
            ByteBufCodecs.FLOAT, e -> e.speedModifier,
            BlockMiningSpeedTraitEffect::new
    );

    private final TagKey<Block> blocks;
    private final float speedModifier;

    public BlockMiningSpeedTraitEffect(TagKey<Block> blocks, float speedModifier) {
        this.blocks = blocks;
        this.speedModifier = speedModifier;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.BLOCK_MINING_SPEED.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        // TODO
        return List.of();
    }

    @Override
    public float getMiningSpeedModifier(int traitLevel, BlockState state) {
        if (state.is(this.blocks)) {
            return traitLevel * this.speedModifier;
        }
        return 0f;
    }
}
