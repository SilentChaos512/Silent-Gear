package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.gear.material.modifier.ChargedMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.GradeMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class MaterialModifiers {
    private static final Map<ResourceLocation, IMaterialModifierType<?>> MODIFIERS = new LinkedHashMap<>();

    public static final GradeMaterialModifier.Type GRADE = new GradeMaterialModifier.Type();
    public static final ChargedMaterialModifier.Type<StarchargedMaterialModifier> STARCHARGED = new ChargedMaterialModifier.Type<>(StarchargedMaterialModifier::new, "SG_Starcharged");

    static {
        registerType(Const.GRADE, GRADE);
        registerType(Const.STARCHARGED, STARCHARGED);
    }

    public static void registerType(ResourceLocation id, IMaterialModifierType<?> type) {
        if (MODIFIERS.containsKey(id)) {
            throw new IllegalArgumentException("Already have material modifier with ID " + id);
        }

        MODIFIERS.put(id, type);
        SilentGear.LOGGER.info("Registered material modifier {}", id);
    }

    @Nullable
    public static IMaterialModifierType<?> getType(ResourceLocation typeName) {
        return MODIFIERS.get(typeName);
    }

    public static Collection<IMaterialModifierType<?>> getTypes() {
        return MODIFIERS.values();
    }

    public static Collection<IMaterialModifier> readFromMaterial(IMaterialInstance material) {
        Collection<IMaterialModifier> ret = new ArrayList<>();

        for (IMaterialModifierType<?> type : MODIFIERS.values()) {
            IMaterialModifier modifier = type.read(material);
            if (modifier != null) {
                ret.add(modifier);
            }
        }

        return ret;
    }

    public static Collection<IMaterialModifier> readFromJson(JsonArray array) {
        Collection<IMaterialModifier> ret = Lists.newArrayList();

        for (JsonElement je : array) {
            JsonObject jo = je.getAsJsonObject();
            ResourceLocation typeName = new ResourceLocation(GsonHelper.getAsString(jo, "type"));
            IMaterialModifierType<?> type = getType(typeName);
            if (type == null) {
                throw new JsonSyntaxException("Unknown material modifier type: " + typeName);
            }
            ret.add(type.deserialize(jo));
        }

        return ret;
    }

    public static IMaterialModifier readFromNetwork(FriendlyByteBuf buf) {
        ResourceLocation typeName = buf.readResourceLocation();
        IMaterialModifierType<?> type = getType(typeName);
        if (type == null) {
            throw new IllegalStateException("Unknown material modifier type: " + typeName);
        }
        return type.readFromNetwork(buf);
    }

    public static <T extends IMaterialModifier> void writeToNetwork(T modifier, FriendlyByteBuf buf) {
        //noinspection unchecked
        IMaterialModifierType<T> type = (IMaterialModifierType<T>) modifier.getType();
        buf.writeResourceLocation(type.getId());
        type.writeToNetwork(modifier, buf);
    }

    @Nullable
    public static IMaterialModifier readNbt(CompoundTag tag) {
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("ID"));
        if (id == null) {
            return null;
        }

        IMaterialModifierType<?> type = MaterialModifiers.getType(id);
        if (type == null) {
            return null;
        }

        return type.read(tag);
    }

    @Nullable
    public static <T extends IMaterialModifier> CompoundTag writeNbt(T modifier) {
        //noinspection unchecked
        IMaterialModifierType<T> type = (IMaterialModifierType<T>) modifier.getType();
        CompoundTag tag = new CompoundTag();
        type.write(modifier, tag);
        if (!tag.isEmpty()) {
            tag.putString("ID", type.getId().toString());
            return tag;
        }
        return null;
    }

    public static <T extends IMaterialModifier> void writeToItem(T modifier, ItemStack stack) {
        //noinspection unchecked
        IMaterialModifierType<T> type = (IMaterialModifierType<T>) modifier.getType();
        type.write(modifier, stack);
    }
}
