package net.silentchaos512.gear.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.crafting.Ingredient;
import net.silentchaos512.gear.SilentGear;

public class JsonHelper {
    public static JsonElement encodeIngredient(Ingredient ingredient) {
        return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow(true, SilentGear.LOGGER::error);
    }
}
