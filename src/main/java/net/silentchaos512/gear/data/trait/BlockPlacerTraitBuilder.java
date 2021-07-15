package net.silentchaos512.gear.data.trait;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.BlockPlacerTrait;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.lib.util.NameUtils;

public class BlockPlacerTraitBuilder extends TraitBuilder {
    private final Block block;
    private final int damageOnUse;
    private int cooldown;
    private SoundEvent sound;
    private float soundVolume = 1f;
    private float soundPitch = 1f;

    public BlockPlacerTraitBuilder(DataResource<ITrait> trait, int maxLevel, Block block, int damageOnUse) {
        this(trait.getId(), maxLevel, block, damageOnUse);
    }

    public BlockPlacerTraitBuilder(ResourceLocation traitId, int maxLevel, Block block, int damageOnUse) {
        super(traitId, maxLevel, BlockPlacerTrait.SERIALIZER);
        this.block = block;
        this.damageOnUse = damageOnUse;
        this.sound = this.block.defaultBlockState().getSoundType().getPlaceSound();
    }

    public BlockPlacerTraitBuilder cooldown(int timeInTicks) {
        this.cooldown = timeInTicks;
        return this;
    }

    public BlockPlacerTraitBuilder sound(SoundEvent sound, float volume, float pitch) {
        this.sound = sound;
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }

    public BlockPlacerTraitBuilder sound(float volume, float pitch) {
        // Retains default sound
        this.soundVolume = volume;
        this.soundPitch = pitch;
        return this;
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();

        json.addProperty("block", NameUtils.from(this.block).toString());
        json.addProperty("damage_on_use", this.damageOnUse);
        json.addProperty("cooldown", this.cooldown);
        json.addProperty("sound", NameUtils.from(this.sound).toString());
        json.addProperty("sound_volume", this.soundVolume);
        json.addProperty("sound_pitch", this.soundPitch);

        return json;
    }
}
