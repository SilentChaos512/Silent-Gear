package net.silentchaos512.gear.gear.material;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.gear.material.modifier.ChargedMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.GradeMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.*;

public class MaterialModifiers {
    public static final Codec<IMaterialModifierType<?>> BY_NAME_CODEC = ResourceLocation.CODEC.flatXmap(
            id -> Optional.ofNullable(getType(id))
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown material modifier key: " + id)),
            mod -> Optional.of(mod.getId())
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown material modifier:" + mod))
    );

    public static final Codec<IMaterialModifier> CODEC = BY_NAME_CODEC
            .dispatch("type", IMaterialModifier::getType, IMaterialModifierType::codec);

    private static final Map<ResourceLocation, IMaterialModifierType<?>> MODIFIERS = new LinkedHashMap<>();

    public static final GradeMaterialModifier.Type GRADE = new GradeMaterialModifier.Type();
    public static final ChargedMaterialModifier.Type<StarchargedMaterialModifier> STARCHARGED = new ChargedMaterialModifier.Type<>(
            StarchargedMaterialModifier::new,
            "SG_Starcharged"
    );

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

    public static Collection<IMaterialModifier> readFromMaterial(MaterialInstance material) {
        Collection<IMaterialModifier> ret = new ArrayList<>();

        for (IMaterialModifierType<?> type : MODIFIERS.values()) {
            IMaterialModifier modifier = type.read(material);
            if (modifier != null) {
                ret.add(modifier);
            }
        }

        return ret;
    }

    public static <T extends IMaterialModifier> void writeToItem(T modifier, ItemStack stack) {
        //noinspection unchecked
        IMaterialModifierType<T> type = (IMaterialModifierType<T>) modifier.getType();
        type.write(modifier, stack);
    }
}
