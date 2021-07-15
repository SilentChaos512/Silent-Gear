package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Objects;

public class BlockFillerTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("block_filler");
    public static final ITraitSerializer<BlockFillerTrait> SERIALIZER = new Serializer<>(SERIALIZER_ID,
            BlockFillerTrait::new,
            BlockFillerTrait::readJson,
            BlockFillerTrait::read,
            BlockFillerTrait::write);

    @Nullable private Block targetBlock;
    @Nullable private ITag.INamedTag<Block> targetBlockTag;
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
    public ActionResultType onItemUse(ItemUseContext context, int traitLevel) {
        PlayerEntity player = context.getPlayer();

        if (player != null) {
            if (player.isShiftKeyDown() && sneakMode == SneakMode.PASS) {
                return ActionResultType.PASS;
            }
        }

        ItemStack stack = context.getItemInHand();
        World world = context.getLevel();
        BlockPos center = context.getClickedPos();

        // Constrain area on facing axis to behave similar to AOE tools
        int rangeX = shouldConstrain(context, Direction.Axis.X) ? 0 : fillRangeX;
        int rangeY = shouldConstrain(context, Direction.Axis.Y) ? 0 : fillRangeY;
        int rangeZ = shouldConstrain(context, Direction.Axis.Z) ? 0 : fillRangeZ;

        // Simulates replacement to get replace count for durability cost
        int replaceCount = replaceBlocks(context, stack, world, center, rangeX, rangeY, rangeZ, true);
        int durabilityCost = Math.round(damageOnUse * replaceCount);
        boolean hasEnoughDurability = durabilityCost < 1 || stack.getDamageValue() < stack.getMaxDamage() - durabilityCost;

        if (player != null && player.level.isClientSide) {
            // Bale out here on client
            return replaceCount > 0 && hasEnoughDurability ? ActionResultType.SUCCESS : ActionResultType.PASS;
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
                world.playSound(null, center, sound, SoundCategory.BLOCKS, soundVolume, pitch);
            }
            if (this.cooldown > 0 && player != null) {
                player.getCooldowns().addCooldown(stack.getItem(), this.cooldown);
            }

            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    private int replaceBlocks(ItemUseContext context, ItemStack stack, World world, BlockPos center, int rangeX, int rangeY, int rangeZ, boolean simulate) {
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

    private boolean shouldConstrain(ItemUseContext context, Direction.Axis axis) {
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
        JsonObject targetJson = JSONUtils.getAsJsonObject(json, "target");
        if (targetJson.has("tag")) {
            trait.targetBlockTag = BlockTags.bind(JSONUtils.getAsString(targetJson, "tag"));
        }
        if (targetJson.has("block")) {
            trait.targetBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getAsString(targetJson, "block")));
        }
        trait.fillBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(JSONUtils.getAsString(json, "fill_block")));
        trait.replaceTileEntities = JSONUtils.getAsBoolean(json, "replace_tile_entities", false);
        trait.fillRangeX = JSONUtils.getAsInt(json, "fill_spread_x", 0);
        trait.fillRangeY = JSONUtils.getAsInt(json, "fill_spread_y", 0);
        trait.fillRangeZ = JSONUtils.getAsInt(json, "fill_spread_z", 0);
        trait.fillFacingPlaneOnly = JSONUtils.getAsBoolean(json, "fill_facing_plane_only", false);
        trait.sneakMode = SneakMode.byName(JSONUtils.getAsString(json, "sneak_mode", "pass"));
        trait.damageOnUse = JSONUtils.getAsFloat(json, "damage_on_use");
        trait.cooldown = JSONUtils.getAsInt(json, "cooldown", 0);
        trait.sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(JSONUtils.getAsString(json, "sound")));
        trait.soundVolume = JSONUtils.getAsFloat(json, "sound_volume");
        trait.soundPitch = JSONUtils.getAsFloat(json, "sound_pitch");
    }

    private static void read(BlockFillerTrait trait, PacketBuffer buffer) {
        if (buffer.readBoolean()) {
            trait.targetBlockTag = BlockTags.bind(buffer.readResourceLocation().toString());
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

    private static void write(BlockFillerTrait trait, PacketBuffer buffer) {
        buffer.writeBoolean(trait.targetBlockTag != null);
        if (trait.targetBlockTag != null) {
            buffer.writeResourceLocation(trait.targetBlockTag.getName());
        }
        buffer.writeBoolean(trait.targetBlock != null);
        if (trait.targetBlock != null) {
            buffer.writeResourceLocation(NameUtils.from(trait.targetBlock));
        }
        buffer.writeResourceLocation(Objects.requireNonNull(trait.fillBlock.getRegistryName()));
        buffer.writeBoolean(trait.replaceTileEntities);
        buffer.writeByte(trait.fillRangeX);
        buffer.writeByte(trait.fillRangeY);
        buffer.writeByte(trait.fillRangeZ);
        buffer.writeBoolean(trait.fillFacingPlaneOnly);
        buffer.writeEnum(trait.sneakMode);
        buffer.writeFloat(trait.damageOnUse);
        buffer.writeVarInt(trait.cooldown);
        buffer.writeResourceLocation(Objects.requireNonNull(trait.sound.getRegistryName()));
        buffer.writeFloat(trait.soundVolume);
        buffer.writeFloat(trait.soundPitch);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();

        ret.add("  - Fills with: " + NameUtils.from(fillBlock));
        ret.add("  - Replaces");
        if (targetBlockTag != null) {
            ret.add("    - Tag: " + targetBlockTag.getName());
        }
        if (targetBlock != null) {
            ret.add("    - Block: " + NameUtils.from(targetBlock));
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
