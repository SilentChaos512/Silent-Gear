package net.silentchaos512.gear.api.data.trait;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.lib.util.NameUtils;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;

public class BlockFillerTraitBuilder extends TraitBuilder {
    @Nullable private Block targetBlock;
    @Nullable private TagKey<Block> targetBlockTag;
    private final Block fillBlock;
    private boolean replaceTileEntities = false;
    private int fillRangeX = 0;
    private int fillRangeY = 0;
    private int fillRangeZ = 0;
    private boolean fillFacingPlaneOnly = false;
    private SneakMode sneakMode = SneakMode.PASS;
    private final float damageOnUse;
    private int cooldown;
    private SoundEvent sound;
    private float soundVolume = 1f;
    private float soundPitch = 1f;

    public BlockFillerTraitBuilder(DataResource<ITrait> trait, int maxLevel, Block fillBlock, float damageOnUse) {
        this(trait.getId(), maxLevel, fillBlock, damageOnUse);
    }

    public BlockFillerTraitBuilder(ResourceLocation traitId, int maxLevel, Block fillBlock, float damageOnUse) {
        super(traitId, maxLevel, ApiConst.BLOCK_FILLER_TRAIT_ID);
        this.fillBlock = fillBlock;
        this.damageOnUse = damageOnUse;
        this.sound = this.fillBlock.defaultBlockState().getSoundType().getBreakSound();
    }

    public BlockFillerTraitBuilder target(TagKey<Block> tag) {
        this.targetBlockTag = tag;
        return this;
    }

    public BlockFillerTraitBuilder target(Block block) {
        this.targetBlock = block;
        return this;
    }

    public BlockFillerTraitBuilder replaceTileEntities(boolean value) {
        this.replaceTileEntities = value;
        return this;
    }

    public BlockFillerTraitBuilder fillRange(int x, int y, int z, boolean fillFacingPlaneOnly) {
        this.fillRangeX = x;
        this.fillRangeY = y;
        this.fillRangeZ = z;
        this.fillFacingPlaneOnly = fillFacingPlaneOnly;
        return this;
    }

    public BlockFillerTraitBuilder sneakMode(SneakMode mode) {
        this.sneakMode = mode;
        return this;
    }

    public BlockFillerTraitBuilder cooldown(int timeInTicks) {
        this.cooldown = timeInTicks;
        return this;
    }

    public BlockFillerTraitBuilder sound(SoundEvent sound, float volume, float pitch) {
        this.sound = sound;
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }

    public BlockFillerTraitBuilder sound(float volume, float pitch) {
        // Retains default sound
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();

        JsonObject targetJson = new JsonObject();
        if (this.targetBlockTag != null) {
            targetJson.addProperty("tag", this.targetBlockTag.location().toString());
        }
        if (this.targetBlock != null) {
            targetJson.addProperty("block", NameUtils.fromBlock(this.targetBlock).toString());
        }
        json.add("target", targetJson);

        json.addProperty("fill_block", NameUtils.fromBlock(this.fillBlock).toString());
        json.addProperty("replace_tile_entities", replaceTileEntities);
        json.addProperty("fill_spread_x", fillRangeX);
        json.addProperty("fill_spread_y", fillRangeY);
        json.addProperty("fill_spread_z", fillRangeZ);
        json.addProperty("fill_facing_plane_only", fillFacingPlaneOnly);
        json.addProperty("sneak_mode", sneakMode.name().toLowerCase(Locale.ROOT));
        json.addProperty("damage_on_use", this.damageOnUse);
        json.addProperty("cooldown", this.cooldown);
        json.addProperty("sound", Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getKey(this.sound)).toString());
        json.addProperty("sound_volume", this.soundVolume);
        json.addProperty("sound_pitch", this.soundPitch);

        return json;
    }

    public enum SneakMode {
        PASS, CONSTRAIN, IGNORE;
    }
}
