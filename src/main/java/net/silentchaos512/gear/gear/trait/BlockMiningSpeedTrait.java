package net.silentchaos512.gear.gear.trait;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.util.TraitHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Mod.EventBusSubscriber
public class BlockMiningSpeedTrait extends SimpleTrait {
    public static final ITraitSerializer<BlockMiningSpeedTrait> SERIALIZER = new Serializer<>(
            ApiConst.BLOCK_MINING_SPEED_TRAIT_ID,
            BlockMiningSpeedTrait::new,
            (trait, json) -> {
                trait.speedMultiplier = GsonHelper.getAsFloat(json, "speed_multiplier", 1f);
                trait.blocks = deserializeBlocks(json);
            },
            (trait, buf) -> {
                trait.speedMultiplier = buf.readFloat();
                trait.blocks = readBlocksFromNetwork(buf);
            },
            (trait, buf) -> {
                buf.writeFloat(trait.speedMultiplier);
                writeBlocksToNetwork(buf, trait.blocks);
            }
    );

    private Collection<TagKey<Block>> blocks;
    private float speedMultiplier;

    public BlockMiningSpeedTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        Map<ITrait, Integer> traits = TraitHelper.getCachedTraits(tool);
        for (Map.Entry<ITrait, Integer> entry : traits.entrySet()) {
            ITrait trait = entry.getKey();
            if (trait instanceof BlockMiningSpeedTrait) {
                int level = entry.getValue();
                float multiplier = ((BlockMiningSpeedTrait) trait).speedMultiplier;
                float newSpeed = event.getNewSpeed() * level * (1f + multiplier);
                event.setNewSpeed(newSpeed);
                SilentGear.LOGGER.debug(newSpeed);
            }
        }
    }

    private static Collection<TagKey<Block>> deserializeBlocks(JsonObject json) {
        JsonElement je = json.get("blocks");
        if (je.isJsonArray()) {
            Collection<TagKey<Block>> ret = Sets.newHashSet();
            for (JsonElement e : je.getAsJsonArray()) {
                ret.add(BlockTags.create(new ResourceLocation(e.getAsString())));
            }
            return ret;
        } else {
            return Collections.singleton(BlockTags.create(new ResourceLocation(je.getAsString())));
        }
    }

    private static Collection<TagKey<Block>> readBlocksFromNetwork(FriendlyByteBuf buf) {
        Collection<TagKey<Block>> blocks = Sets.newHashSet();
        int count = buf.readVarInt();
        for (int i = 0; i < count; ++i) {
            blocks.add(BlockTags.create(buf.readResourceLocation()));
        }
        return blocks;
    }

    private static void writeBlocksToNetwork(FriendlyByteBuf buf, Collection<TagKey<Block>> blocks) {
        buf.writeVarInt(blocks.size());
        for (TagKey<Block> tag : blocks) {
            buf.writeResourceLocation(tag.location());
        }
    }
}
