package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.block.PhantomLight;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.item.FakeItemUseContext;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

public class BlockPlacerTraitEffect extends TraitEffect {
    public static final MapCodec<BlockPlacerTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BlockState.CODEC.fieldOf("block").forGetter(e -> e.blockState),
                    Codec.INT.fieldOf("damage_on_use").forGetter(e -> e.damageOnUse),
                    Codec.INT.fieldOf("cooldown").forGetter(e -> e.cooldown),
                    SoundPlayback.CODEC.optionalFieldOf("sound", null).forGetter(e -> e.sound)
            ).apply(instance, BlockPlacerTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPlacerTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(Registries.BLOCK), e -> e.blockState.getBlock(),
            ByteBufCodecs.VAR_INT, e -> e.damageOnUse,
            ByteBufCodecs.VAR_INT, e -> e.cooldown,
            SoundPlayback.STREAM_CODEC, e -> e.sound,
            (block, damageOnUse, cooldown, sound) ->
                    new BlockPlacerTraitEffect(block.defaultBlockState(), damageOnUse, cooldown, sound)
    );

    private final BlockState blockState;
    private final int damageOnUse;
    private final int cooldown;
    @Nullable
    private final SoundPlayback sound;

    public BlockPlacerTraitEffect(
            BlockState blockState,
            int damageOnUse,
            int cooldown,
            @Nullable SoundPlayback sound
    ) {
        this.blockState = blockState;
        this.damageOnUse = damageOnUse;
        this.cooldown = cooldown;
        this.sound = sound;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.BLOCK_PLACER.get();
    }

    @Override
    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        ItemStack stack = context.getItemInHand();

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!world.isClientSide && (damageOnUse < 1 || stack.getDamageValue() < stack.getMaxDamage() - damageOnUse - 1)) {
            // Try place block, damage tool if successful
            ItemStack fakeBlockStack = new ItemStack(blockState.getBlock());
            InteractionResult result = fakeBlockStack.useOn(new FakeItemUseContext(context, fakeBlockStack));
            if (result.consumesAction()) {
                if (damageOnUse > 0 && context.getPlayer() != null) {
                    GearHelper.attemptDamage(stack, damageOnUse, context.getPlayer(), context.getHand());
                }
                if (sound != null) {
                    this.sound.playAt(world, pos, SoundSource.BLOCKS);
                }
                if (this.cooldown > 0 && context.getPlayer() != null) {
                    context.getPlayer().getCooldowns().addCooldown(stack.getItem(), this.cooldown);
                }
            }
            return result;
        }

        for (int i = 0; i < 5; i++) {
            PhantomLight.spawnParticle(world, pos.relative(context.getClickedFace()), SilentGear.RANDOM_SOURCE);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        ret.add("  - Places: " + NameUtils.fromBlock(blockState));
        ret.add("  - Durability Cost: " + damageOnUse);
        if (cooldown > 0) {
            ret.add("  - Cooldown: " + cooldown);
        }
        return ret;
    }
}
