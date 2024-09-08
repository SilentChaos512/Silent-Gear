package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
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
import java.util.Optional;
import java.util.function.Predicate;

public class BlockFillerTraitEffect extends TraitEffect {
    public static final MapCodec<BlockFillerTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TargetBlock.CODEC.fieldOf("target").forGetter(e -> e.targetBlock),
                    FillProperties.CODEC.fieldOf("fill_properties").forGetter(e -> e.fillProperties),
                    UseProperties.CODEC.fieldOf("use_properties").forGetter(e -> e.useProperties),
                    SoundPlayback.CODEC.fieldOf("sound").forGetter(e -> e.sound)
            ).apply(instance, BlockFillerTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockFillerTraitEffect> STREAM_CODEC = StreamCodec.composite(
            TargetBlock.STREAM_CODEC, e -> e.targetBlock,
            FillProperties.STREAM_CODEC, e -> e.fillProperties,
            UseProperties.STREAM_CODEC, e -> e.useProperties,
            SoundPlayback.STREAM_CODEC, e -> e.sound,
            BlockFillerTraitEffect::new
    );

    private final TargetBlock targetBlock;
    private final FillProperties fillProperties;
    private final UseProperties useProperties;
    private final SoundPlayback sound;

    public BlockFillerTraitEffect(TargetBlock targetBlock, FillProperties fillProperties, UseProperties useProperties, SoundPlayback sound) {
        this.targetBlock = targetBlock;
        this.fillProperties = fillProperties;
        this.useProperties = useProperties;
        this.sound = sound;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.BLOCK_FILLER.get();
    }

    @Override
    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        Player player = context.getPlayer();

        if (player != null) {
            if (player.isShiftKeyDown() && useProperties.sneakMode == SneakMode.PASS) {
                return InteractionResult.PASS;
            }
        }

        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos center = context.getClickedPos();

        // Constrain area on facing axis to behave similar to AOE tools
        int rangeX = shouldConstrain(context, Direction.Axis.X) ? 0 : fillProperties.rangeX;
        int rangeY = shouldConstrain(context, Direction.Axis.Y) ? 0 : fillProperties.rangeY;
        int rangeZ = shouldConstrain(context, Direction.Axis.Z) ? 0 : fillProperties.rangeZ;

        // Simulates replacement to get replace count for durability cost
        int replaceCount = replaceBlocks(context, stack, world, center, rangeX, rangeY, rangeZ, true);
        int durabilityCost = Math.round(useProperties.damagePerBlock * replaceCount);
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
            if (useProperties.damagePerBlock > 0 && player != null) {
                GearHelper.attemptDamage(stack, durabilityCost, player, context.getHand());
            }
            if (sound != null) {
                sound.playAt(player.level(), context.getClickedPos(), SoundSource.BLOCKS);
            }
            if (useProperties.cooldown > 0) {
                player.getCooldowns().addCooldown(stack.getItem(), useProperties.cooldown);
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

                    if (targetBlock.test(state) && (fillProperties.replaceBlockEntities || world.getBlockEntity(pos) == null)) {
                        if (!simulate) {
                            world.setBlock(pos, fillProperties.block.defaultBlockState(), 11);
                        }
                        ++count;
                    }
                }
            }
        }
        return count;
    }

    private boolean shouldConstrain(UseOnContext context, Direction.Axis axis) {
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown() && useProperties.sneakMode == SneakMode.CONSTRAIN) {
            return true;
        }
        return fillProperties.facingPlaneOnly && context.getClickedFace().getAxis() == axis;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();

        ret.add("  - Fills with: " + NameUtils.fromBlock(fillProperties.block));
        ret.add("  - Replaces");
        if (targetBlock.tag != null) {
            ret.add("    - Tag: " + targetBlock.tag.location());
        }
        if (targetBlock.block != null) {
            ret.add("    - Block: " + NameUtils.fromBlock(targetBlock.block));
        }
        ret.add("    - " + (fillProperties.replaceBlockEntities ? "Replaces" : "Does not replace") + " block entities");

        // Fill area
        int fillX = 2 * fillProperties.rangeX + 1;
        int fillY = 2 * fillProperties.rangeY + 1;
        int fillZ = 2 * fillProperties.rangeZ + 1;
        ret.add("  - Fill Area");
        ret.add("    - X: " + fillX + " (+" + fillProperties.rangeX + ")");
        ret.add("    - Y: " + fillY + " (+" + fillProperties.rangeY + ")");
        ret.add("    - Z: " + fillZ + " (+" + fillProperties.rangeZ + ")");
        if (fillProperties.facingPlaneOnly) {
            ret.add("    - Fills facing plane only");
        }
        ret.add("    - On sneak: " + useProperties.sneakMode.name());

        ret.add("  - Durability Cost: " + useProperties.damagePerBlock);
        if (useProperties.cooldown > 0) {
            ret.add("  - Cooldown: " + useProperties.cooldown);
        }

        return ret;
    }

    public enum SneakMode implements StringRepresentable {
        PASS, CONSTRAIN, IGNORE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static final Codec<SneakMode> CODEC = StringRepresentable.fromEnum(SneakMode::values);
        public static final StreamCodec<FriendlyByteBuf, SneakMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(SneakMode.class);
    }

    public record TargetBlock(
            @Nullable Block block,
            @Nullable TagKey<Block> tag
    ) implements Predicate<BlockState> {
        public static final Codec<TargetBlock> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().optionalFieldOf("block").forGetter(t -> Optional.ofNullable(t.block)),
                        TagKey.codec(Registries.BLOCK).optionalFieldOf("tag").forGetter(t -> Optional.ofNullable(t.tag))
                ).apply(instance, (block1, tag1) -> new TargetBlock(block1.orElse(null), tag1.orElse(null)))
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, TargetBlock> STREAM_CODEC = StreamCodec.of(
                (buf, val) -> {
                    buf.writeBoolean(val.block != null);
                    if (val.block != null) {
                        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(val.block));
                    }
                    buf.writeBoolean(val.tag != null);
                    if (val.tag != null) {
                        buf.writeResourceLocation(val.tag.location());
                    }
                },
                buf -> {
                    Block block = null;
                    TagKey<Block> tag = null;
                    if (buf.readBoolean()) {
                        block = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
                    }
                    if (buf.readBoolean()) {
                        tag = BlockTags.create(buf.readResourceLocation());
                    }
                    return new TargetBlock(block, tag);
                }
        );

        @Override
        public boolean test(BlockState state) {
            return (this.block != null && state.is(this.block))
                    || (this.tag != null && state.is(this.tag));
        }
    }

    public record FillProperties(
            Block block,
            boolean replaceBlockEntities,
            int rangeX,
            int rangeY,
            int rangeZ,
            boolean facingPlaneOnly
    ) {
        public static final Codec<FillProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(p -> p.block),
                        Codec.BOOL.fieldOf("replace_block_entities").forGetter(p -> p.replaceBlockEntities),
                        Codec.INT.fieldOf("range_x").forGetter(p -> p.rangeX),
                        Codec.INT.fieldOf("range_y").forGetter(p -> p.rangeY),
                        Codec.INT.fieldOf("range_z").forGetter(p -> p.rangeZ),
                        Codec.BOOL.fieldOf("facing_plane_only").forGetter(p -> p.facingPlaneOnly)
                ).apply(instance, FillProperties::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FillProperties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(Registries.BLOCK), p -> p.block,
                ByteBufCodecs.BOOL, p -> p.replaceBlockEntities,
                ByteBufCodecs.VAR_INT, p -> p.rangeX,
                ByteBufCodecs.VAR_INT, p -> p.rangeY,
                ByteBufCodecs.VAR_INT, p -> p.rangeZ,
                ByteBufCodecs.BOOL, p -> p.facingPlaneOnly,
                FillProperties::new
        );
    }

    public record UseProperties(
            SneakMode sneakMode,
            float damagePerBlock,
            int cooldown
    ) {
        public static final Codec<UseProperties> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        SneakMode.CODEC.fieldOf("sneak_mode").forGetter(p -> p.sneakMode),
                        Codec.FLOAT.fieldOf("damage_per_block").forGetter(p -> p.damagePerBlock),
                        Codec.INT.optionalFieldOf("cooldown", 0).forGetter(p -> p.cooldown)
                ).apply(instance, UseProperties::new)
        );
        public static final StreamCodec<FriendlyByteBuf, UseProperties> STREAM_CODEC = StreamCodec.composite(
                SneakMode.STREAM_CODEC, p -> p.sneakMode,
                ByteBufCodecs.FLOAT, p -> p.damagePerBlock,
                ByteBufCodecs.VAR_INT, p -> p.cooldown,
                UseProperties::new
        );
    }
}
