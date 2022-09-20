package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.block.PhantomLight;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.item.FakeItemUseContext;
import net.silentchaos512.lib.util.NameUtils;

import java.util.Collection;
import java.util.Objects;

public class BlockPlacerTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("block_placer");
    public static final ITraitSerializer<BlockPlacerTrait> SERIALIZER = new Serializer<>(SERIALIZER_ID, BlockPlacerTrait::new, BlockPlacerTrait::readJson, BlockPlacerTrait::read, BlockPlacerTrait::write);

    private Block block;
    private int damageOnUse;
    private int cooldown;
    private SoundEvent sound = SoundEvents.ITEM_PICKUP;
    private float soundVolume = 1.0f;
    private float soundPitch = 1.0f;

    public BlockPlacerTrait(ResourceLocation name) {
        super(name, SERIALIZER);
    }

    @Override
    public InteractionResult onItemUse(UseOnContext context, int traitLevel) {
        ItemStack stack = context.getItemInHand();

        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!world.isClientSide && (damageOnUse < 1 || stack.getDamageValue() < stack.getMaxDamage() - damageOnUse - 1)) {
            // Try place block, damage tool if successful
            ItemStack fakeBlockStack = new ItemStack(block);
            InteractionResult result = fakeBlockStack.useOn(new FakeItemUseContext(context, fakeBlockStack));
            if (result.consumesAction()) {
                if (damageOnUse > 0) {
                    GearHelper.attemptDamage(stack, damageOnUse, context.getPlayer(), context.getHand());
                }
                if (sound != null) {
                    float pitch = (float) (soundPitch * (1 + 0.05 * SilentGear.RANDOM.nextGaussian()));
                    world.playSound(null, pos, sound, SoundSource.BLOCKS, soundVolume, pitch);
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

    private static void readJson(BlockPlacerTrait trait, JsonObject json) {
        ResourceLocation blockId = new ResourceLocation(GsonHelper.getAsString(json, "block"));
        trait.block = ForgeRegistries.BLOCKS.getValue(blockId);
        if (trait.block == null) {
            throw new JsonParseException("Unknown block: " + blockId);
        }
        trait.damageOnUse = GsonHelper.getAsInt(json, "damage_on_use");
        trait.cooldown = GsonHelper.getAsInt(json, "cooldown", 0);
        ResourceLocation soundId = new ResourceLocation(GsonHelper.getAsString(json, "sound"));
        trait.sound = ForgeRegistries.SOUND_EVENTS.getValue(soundId);
        if (trait.sound == null) {
            throw new JsonParseException("Unknown sound: " + soundId);
        }
        trait.soundVolume = GsonHelper.getAsFloat(json, "sound_volume");
        trait.soundPitch = GsonHelper.getAsFloat(json, "sound_pitch");
    }

    private static void read(BlockPlacerTrait trait, FriendlyByteBuf buffer) {
        trait.block = ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation());
        trait.damageOnUse = buffer.readVarInt();
        trait.cooldown = buffer.readVarInt();
        trait.sound = ForgeRegistries.SOUND_EVENTS.getValue(buffer.readResourceLocation());
        trait.soundVolume = buffer.readFloat();
        trait.soundPitch = buffer.readFloat();
    }

    private static void write(BlockPlacerTrait trait, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(NameUtils.fromBlock(trait.block));
        buffer.writeVarInt(trait.damageOnUse);
        buffer.writeVarInt(trait.cooldown);
        buffer.writeResourceLocation(Objects.requireNonNull(ForgeRegistries.SOUND_EVENTS.getKey(trait.sound)));
        buffer.writeFloat(trait.soundVolume);
        buffer.writeFloat(trait.soundPitch);
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        ret.add("  - Places: " + NameUtils.fromBlock(block));
        ret.add("  - Durability Cost: " + damageOnUse);
        if (cooldown > 0) {
            ret.add("  - Cooldown: " + cooldown);
        }
        return ret;
    }
}
