package net.silentchaos512.gear.gear.material.modifier;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ChargedProperties;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class ChargedMaterialModifier implements IMaterialModifier {
    protected final int level;

    protected ChargedMaterialModifier(int level) {
        this.level = level;
    }

    public ChargedProperties getChargedProperties(IMaterialInstance material) {
        return new ChargedProperties(level, material.getStat(PartType.MAIN, ItemStats.CHARGING_VALUE));
    }

    public static class Type<T extends ChargedMaterialModifier> implements IMaterialModifierType<T> {
        private final Function<Integer, T> factory;
        private final String nbtTagName;

        public Type(Function<Integer, T> factory, String nbtTagName) {
            this.factory = factory;
            this.nbtTagName = nbtTagName;
        }

        public int checkLevel(ItemStack stack) {
            return stack.getOrCreateTag().getShort(nbtTagName);
        }

        public T create(int level) {
            return factory.apply(level);
        }

        @Override
        public ResourceLocation getId() {
            return Const.STARCHARGED;
        }

        @Override
        public void removeModifier(ItemStack stack) {
            if (!stack.isEmpty()) {
                stack.getOrCreateTag().remove(nbtTagName);
                stack.getOrCreateTag().remove(Const.NBT_IS_FOIL);
            }
        }

        boolean causesFoilEffect() {
            return true;
        }

        @Nullable
        @Override
        public T read(CompoundTag tag) {
            int level = tag.getShort(nbtTagName);

            if (level == 0) {
                return null;
            }

            return factory.apply(level);
        }

        @Override
        public void write(T modifier, CompoundTag tag) {
            tag.putShort(nbtTagName, (short) modifier.level);
            if (causesFoilEffect()) {
                tag.putBoolean(Const.NBT_IS_FOIL, true);
            }
        }

        @Override
        public T readFromNetwork(FriendlyByteBuf buf) {
            int level = buf.readByte();
            return create(level);
        }

        @Override
        public void writeToNetwork(T modifier, FriendlyByteBuf buf) {
            buf.writeByte(modifier.level);
        }

        @Override
        public T deserialize(JsonObject json) {
            int level = GsonHelper.getAsInt(json, "level", 0);
            return create(level);
        }

        @Override
        public JsonObject serialize(T modifier) {
            JsonObject json = new JsonObject();
            json.addProperty("level", modifier.level);
            return json;
        }
    }
}
