package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

public class BlockFillerTrait extends SimpleTrait {
    public static final ITraitSerializer<BlockFillerTrait> SERIALIZER = new Serializer<>(ApiConst.BLOCK_FILLER_TRAIT_ID,
            BlockFillerTrait::new,
            BlockFillerTrait::readJson,
            BlockFillerTrait::read,
            BlockFillerTrait::write);

    @Nullable private Block targetBlock;
    @Nullable private TagKey<Block> targetBlockTag;
    private Block fillBlock;
    private boolean replaceTileEntities;
    private int fillRangeX;
    private int fillRangeY;
    private int fillRangeZ;
    private boolean fillFacingPlaneOnly;
    private SneakMode sneakMode;
    private float damageOnUse;
    private int cooldown;
    private SoundEvent sound = SoundEvents.ITEM_PICKUP;
    private float soundVolume = 1.0f;
    private float soundPitch = 1.0f;

    public BlockFillerTrait(ResourceLocation id) {
        super(id, SERIALIZER);
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

    private static void readJson(BlockFillerTrait trait, JsonObject json) {
        JsonObject targetJson = GsonHelper.getAsJsonObject(json, "target");
        if (targetJson.has("tag")) {
            trait.targetBlockTag = BlockTags.create(new ResourceLocation(GsonHelper.getAsString(targetJson, "tag")));
        }
        if (targetJson.has("block")) {
            trait.targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(GsonHelper.getAsString(targetJson, "block")));
        }
        trait.fillBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "fill_block")));
        trait.replaceTileEntities = GsonHelper.getAsBoolean(json, "replace_tile_entities", false);
        trait.fillRangeX = GsonHelper.getAsInt(json, "fill_spread_x", 0);
        trait.fillRangeY = GsonHelper.getAsInt(json, "fill_spread_y", 0);
        trait.fillRangeZ = GsonHelper.getAsInt(json, "fill_spread_z", 0);
        trait.fillFacingPlaneOnly = GsonHelper.getAsBoolean(json, "fill_facing_plane_only", false);
        trait.sneakMode = SneakMode.byName(GsonHelper.getAsString(json, "sneak_mode", "pass"));
        trait.damageOnUse = GsonHelper.getAsFloat(json, "damage_on_use");
        trait.cooldown = GsonHelper.getAsInt(json, "cooldown", 0);
        trait.sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(GsonHelper.getAsString(json, "sound")));
        trait.soundVolume = GsonHelper.getAsFloat(json, "sound_volume");
        trait.soundPitch = GsonHelper.getAsFloat(json, "sound_pitch");
    }

    private static void read(BlockFillerTrait trait, FriendlyByteBuf buffer) {
        if (buffer.readBoolean()) {
            trait.targetBlockTag = BlockTags.create(buffer.readResourceLocation());
        }
        if (buffer.readBoolean()) {
            trait.targetBlock = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
        }
        trait.fillBlock = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
        trait.replaceTileEntities = buffer.readBoolean();
        trait.fillRangeX = buffer.readByte();
        trait.fillRangeY = buffer.readByte();
        trait.fillRangeZ = buffer.readByte();
        trait.fillFacingPlaneOnly = buffer.readBoolean();
        trait.sneakMode = buffer.readEnum(SneakMode.class);
        trait.damageOnUse = buffer.readFloat();
        trait.cooldown = buffer.readVarInt();
        trait.sound = ForgeRegistries.SOUND_EVENTS.getValue(buffer.readResourceLocation());
        trait.soundVolume = buffer.readFloat();
        trait.soundPitch = buffer.readFloat();
    }

    private static void write(BlockFillerTrait trait, FriendlyByteBuf buffer) {
        buffer.writeBoolean(trait.targetBlockTag != null);
        if (trait.targetBlockTag != null) {
            buffer.writeResourceLocation(trait.targetBlockTag.location());
        }
        buffer.writeBoolean(trait.targetBlock != null);
        if (trait.targetBlock != null) {
            buffer.writeResourceLocation(NameUtils.fromBlock(trait.targetBlock));
        }
        buffer.writeResourceLocation(NameUtils.fromBlock(trait.fillBlock));
        buffer.writeBoolean(trait.replaceTileEntities);
        buffer.writeByte(trait.fillRangeX);
        buffer.writeByte(trait.fillRangeY);
        buffer.writeByte(trait.fillRangeZ);
        buffer.writeBoolean(trait.fillFacingPlaneOnly);
        buffer.writeEnum(trait.sneakMode);
        buffer.writeFloat(trait.damageOnUse);
        buffer.writeVarInt(trait.cooldown);
        buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getKey(trait.sound)));
        buffer.writeFloat(trait.soundVolume);
        buffer.writeFloat(trait.soundPitch);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();

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

    public enum SneakMode {
        PASS, CONSTRAIN, IGNORE;

        static SneakMode byName(String name) {
            for (SneakMode value : values()) {
                if (value.name().equalsIgnoreCase(name)) {
                    return value;
                }
            }
            return IGNORE;
        }
    }
}
