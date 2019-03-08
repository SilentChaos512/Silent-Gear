package net.silentchaos512.gear.traits;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.*;

public final class PotionEffectTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("potion_effect_trait");
    static final ITraitSerializer<PotionEffectTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            PotionEffectTrait::new,
            PotionEffectTrait::readJson
    );

    private final Map<String, List<PotionData>> potions = new HashMap<>();

    private PotionEffectTrait(ResourceLocation id) {
        super(id);
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        EntityPlayer player = context.getPlayer();
        if (player == null || !isEquipped) return;
        GearType gearType = ((ICoreItem) context.getGear().getItem()).getGearType();
        potions.forEach((type, list) -> applyEffects(player, gearType, type, list));
    }

    private void applyEffects(EntityPlayer player, GearType gearType, String type, Iterable<PotionData> effects) {
        if (gearType.matches(type) || "all".equals(type)) {
            int setPieceCount = getSetPieceCount(type, player);
            boolean hasFullSet = !"armor".equals(type) || setPieceCount >= 4;
            effects.forEach(d -> d.getEffect(setPieceCount, hasFullSet).ifPresent(player::addPotionEffect));
        }
    }

    private int getSetPieceCount(String type, EntityPlayer player) {
        if (!"armor".equals(type)) return 1;

        int count = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ICoreArmor && TraitHelper.getTraitLevel(stack, this) > 0) {
                ++count;
            }
        }
        return count;
    }

    private static void readJson(PotionEffectTrait trait, JsonObject json) {
        if (!json.has("potion_effects")) {
            throw new JsonParseException("Potion effect trait '" + trait.getId() + "' is missing 'potion_effects' object");
        }

        // Parse potion effects array
        JsonObject jsonEffects = json.getAsJsonObject("potion_effects");
        for (Map.Entry<String, JsonElement> entry : jsonEffects.entrySet()) {
            // Key (gear type)
            String key = entry.getKey();
            // Array of PotionData objects
            JsonElement element = entry.getValue();

            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array, found " + element.getClass().getSimpleName());
            }

            JsonArray array = element.getAsJsonArray();
            List<PotionData> list = new ArrayList<>();
            for (JsonElement elem : array) {
                if (!elem.isJsonObject()) {
                    throw new JsonParseException("Expected object, found " + elem.getClass().getSimpleName());
                }
                list.add(PotionData.from(elem.getAsJsonObject()));
            }

            if (!list.isEmpty()) {
                trait.potions.put(key, list);
            }
        }
    }

    public static class PotionData {
        private boolean requiresFullSet;
        private ResourceLocation effectId;
        private int duration;
        private int[] levels;

        static PotionData from(JsonObject json) {
            PotionData ret = new PotionData();
            ret.requiresFullSet = JsonUtils.getBoolean(json, "full_set", false);
            // Effect ID, get actual potion only when needed
            ret.effectId = new ResourceLocation(JsonUtils.getString(json, "effect", "unknown"));
            // Effects duration in seconds.
            float durationInSeconds = JsonUtils.getFloat(json, "duration", getDefaultDuration(ret));
            ret.duration = TimeUtils.ticksFromSeconds(durationInSeconds);

            // Level int or array
            JsonElement elementLevel = json.get("level");
            if (elementLevel == null) {
                throw new JsonParseException("level element not found, should be either int or object");
            }
            if (elementLevel.isJsonPrimitive()) {
                // Single level
                ret.levels = new int[]{JsonUtils.getInt(json, "level", 1)};
            } else if (elementLevel.isJsonArray()) {
                // Levels by piece count
                JsonArray array = elementLevel.getAsJsonArray();
                ret.levels = new int[array.size()];
                for (int i = 0; i < ret.levels.length; ++i) {
                    ret.levels[i] = array.get(i).getAsInt();
                }
            } else {
                throw new JsonParseException("Expected level to be int or array, was " + elementLevel.getClass().getSimpleName());
            }

            return ret;
        }

        private static float getDefaultDuration(PotionData ret) {
            // Duration in seconds. The .5 should prevent flickering.
            return "night_vision".equals(ret.effectId.getPath()) ? 15.5f : 1.5f;
        }

        Optional<PotionEffect> getEffect(int pieceCount, boolean hasFullSet) {
            if (this.requiresFullSet && !hasFullSet) return Optional.empty();

            Potion potion = ForgeRegistries.POTIONS.getValue(effectId);
            if (potion == null) return Optional.empty();

            int effectLevel = levels[MathHelper.clamp(pieceCount - 1, 0, levels.length - 1)];
            if (effectLevel < 1) return Optional.empty();

            return Optional.of(new PotionEffect(potion, duration, effectLevel - 1, true, false));
        }
    }
}
