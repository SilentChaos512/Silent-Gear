package net.silentchaos512.gear.data.trait;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.gear.trait.BlockMiningSpeedTrait;
import net.silentchaos512.gear.util.DataResource;

import java.util.Collection;
import java.util.Collections;

public class BlockMiningSpeedTraitBuilder extends TraitBuilder {
    private final float speedMultiplier;
    private final Collection<TagKey<Block>> blocks;

    public BlockMiningSpeedTraitBuilder(DataResource<ITrait> trait, int maxLevel, float speedMultiplier, TagKey<Block> block) {
        this(trait, maxLevel, speedMultiplier, Collections.singleton(block));
    }

    public BlockMiningSpeedTraitBuilder(DataResource<ITrait> trait, int maxLevel, float speedMultiplier, Collection<TagKey<Block>> blocks) {
        super(trait, maxLevel, BlockMiningSpeedTrait.SERIALIZER);
        this.speedMultiplier = speedMultiplier;
        this.blocks = Sets.newHashSet(blocks);
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = super.serialize();

        json.addProperty("speed_multiplier", this.speedMultiplier);
        JsonArray array = new JsonArray();
        blocks.forEach(tag -> array.add(tag.location().toString()));
        json.add("blocks", array);

        return json;
    }
}
