package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.core.SoundPlayback;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class BlockFillerTraitEffect extends TraitEffect {
    @Nullable private final Block targetBlock;
    @Nullable private final TagKey<Block> targetBlockTag;
    private final Block fillBlock;
    private final boolean replaceTileEntities;
    private final int fillRangeX;
    private final int fillRangeY;
    private final int fillRangeZ;
    private final boolean fillFacingPlaneOnly;
    private final SneakMode sneakMode;
    private final float damageOnUse;
    private final int cooldown;
    private final SoundPlayback sound;

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.BLOCK_FILLER.get();
    }

    @Override
    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        Player player = context.getPlayer();

        if (player != null) {
            if (player.isShiftKeyDown() && sneakMode == SneakMode.PASS) {
                return InteractionResult.PASS;
            }
        }

        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos center = context.getClickedPos();

        // Constrain area on facing axis to behave similar to AOE tools
        int rangeX = shouldConstrain(context, Direction.Axis.X) ? 0 : fillRangeX;
        int rangeY = shouldConstrain(context, Direction.Axis.Y) ? 0 : fillRangeY;
        int rangeZ = shouldConstrain(context, Direction.Axis.Z) ? 0 : fillRangeZ;

        // Simulates replacement to get replace count for durability cost
        int replaceCount = replaceBlocks(context, stack, world, center, rangeX, rangeY, rangeZ, true);
        int durabilityCost = Math.round(damageOnUse * replaceCount);
        boolean hasEnoughDurability = durabilityCost < 1 || stack.getDamageValue() < stack.getMaxDamage() - durabilityCost;

        if (player != null && player.level().isClientSide) {
            // Bale out here on client
            return replaceCount > 0 && hasEnoughDurability ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }

        if (hasEnoughDurability) {
            // Actually replace the blocks
            replaceBlocks(context, stack, world, center, rangeX, rangeY, rangeZ, false);
        }

        if (replaceCount > 0) {
            // Damage item, player effects
            if (damageOnUse > 0) {
                GearHelper.attemptDamage(stack, durabilityCost, player, context.getHand());
            }
            if (sound != null) {
                float pitch = (float) (soundPitch * (1 + 0.05 * SilentGear.RANDOM.nextGaussian()));
                world.playSound(null, center, sound, SoundSource.BLOCKS, soundVolume, pitch);
            }
            if (this.cooldown > 0 && player != null) {
                player.getCooldowns().addCooldown(stack.getItem(), this.cooldown);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private int replaceBlocks(UseOnContext context, ItemStack stack, Level world, BlockPos center, int rangeX, int rangeY, int rangeZ, boolean simulate) {
        int count = 0;
        for (int x = center.getX() - rangeX; x <= center.getX() + rangeX; ++x) {
            for (int y = center.getY() - rangeY; y <= center.getY() + rangeY; ++y) {
                for (int z = center.getZ() - rangeZ; z <= center.getZ() + rangeZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (canReplace(state) && (replaceTileEntities || world.getBlockEntity(pos) == null)) {
                        if (!simulate) {
                            world.setBlock(pos, fillBlock.defaultBlockState(), 11);
                        }
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    private boolean shouldConstrain(UseOnContext context, Direction.Axis axis) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown() && sneakMode == SneakMode.CONSTRAIN) {
            return true;
        }
        return fillFacingPlaneOnly && context.getClickedFace().getAxis() == axis;
    }

    private boolean canReplace(BlockState state) {
        return (targetBlockTag != null && state.is(targetBlockTag))
                || (targetBlock != null && state.is(targetBlock));
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();

        ret.add("  - Fills with: " + NameUtils.fromBlock(fillBlock));
        ret.add("  - Replaces");
        if (targetBlockTag != null) {
            ret.add("    - Tag: " + targetBlockTag.location());
        }
        if (targetBlock != null) {
            ret.add("    - Block: " + NameUtils.fromBlock(targetBlock));
        }
        ret.add("    - " + (replaceTileEntities ? "Replaces" : "Does not replace") + " tile entities");

        // Fill area
        int fillX = 2 * fillRangeX + 1;
        int fillY = 2 * fillRangeY + 1;
        int fillZ = 2 * fillRangeZ + 1;
        ret.add("  - Fill Area");
        ret.add("    - X: " + fillX + " (+" + fillRangeX + ")");
        ret.add("    - Y: " + fillY + " (+" + fillRangeY + ")");
        ret.add("    - Z: " + fillZ + " (+" + fillRangeZ + ")");
        if (fillFacingPlaneOnly) {
            ret.add("    - Fills facing plane only");
        }
        ret.add("    - On sneak: " + sneakMode.name());

        ret.add("  - Durability Cost: " + damageOnUse);
        if (cooldown > 0) {
            ret.add("  - Cooldown: " + cooldown);
        }

        return ret;
    }

    public enum SneakMode implements StringRepresentable {
        PASS, CONSTRAIN, IGNORE;

        static SneakMode byName(String name) {
            for (SneakMode value : values()) {
                if (value.name().equalsIgnoreCase(name)) {
                    return value;
                }
            }
            return IGNORE;
        }

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static final Codec<SneakMode> CODEC = StringRepresentable.fromEnum(SneakMode::values);
        public static final StreamCodec<FriendlyByteBuf, SneakMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(SneakMode.class);
    }
}
