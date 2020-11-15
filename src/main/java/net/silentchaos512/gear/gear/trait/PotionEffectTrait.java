package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
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

// TODO: rename to WielderEffectTrait?
public final class PotionEffectTrait extends SimpleTrait {
    public static final ITraitSerializer<PotionEffectTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("potion_effect_trait"),
            PotionEffectTrait::new,
            PotionEffectTrait::readJson,
            PotionEffectTrait::readBuffer,
            PotionEffectTrait::writeBuffer
    );

    private final Map<String, List<PotionData>> potions = new HashMap<>();

    private PotionEffectTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        PlayerEntity player = context.getPlayer();
        if (player == null || !isEquipped) return;
        GearType gearType = ((ICoreItem) context.getGear().getItem()).getGearType();
        potions.forEach((type, list) -> applyEffects(player, gearType, type, list));
    }

    private void applyEffects(PlayerEntity player, GearType gearType, String type, Iterable<PotionData> effects) {
        if (gearType.matches(type) || "all".equals(type)) {
            int setPieceCount = getSetPieceCount(type, player);
            boolean hasFullSet = !"armor".equals(type) || setPieceCount >= 4;
            effects.forEach(d -> d.getEffect(setPieceCount, hasFullSet).ifPresent(player::addPotionEffect));
        }
    }

    private int getSetPieceCount(String type, PlayerEntity player) {
        if (!"armor".equals(type)) return 1;

        int count = 0;
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof ICoreArmor && TraitHelper.hasTrait(stack, this)) {
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

    private static void readBuffer(PotionEffectTrait trait, PacketBuffer buffer) {
        trait.potions.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<PotionData> list = new ArrayList<>();
            String gearType = buffer.readString();
            int potionDataCount = buffer.readByte();

            for (int potionIndex = 0; potionIndex < potionDataCount; ++potionIndex) {
                list.add(PotionData.read(buffer));
            }

            trait.potions.put(gearType, list);
        }
    }

    private static void writeBuffer(PotionEffectTrait trait, PacketBuffer buffer) {
        buffer.writeByte(trait.potions.size());
        for (Map.Entry<String, List<PotionData>> entry : trait.potions.entrySet()) {
            buffer.writeString(entry.getKey());
            buffer.writeByte(entry.getValue().size());

            for (PotionData potionData : entry.getValue()) {
                potionData.write(buffer);
            }
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        this.potions.forEach((type, list) -> {
            ret.add("  - " + type);
            list.forEach(mod -> {
                ret.add("    - " + mod.getWikiLine());
            });
        });
        return ret;
    }

    public static class PotionData {
        private boolean requiresFullSet;
        private ResourceLocation effectId;
        private int duration;
        private int[] levels;

        @SuppressWarnings("TypeMayBeWeakened")
        public static PotionData of(boolean requiresFullSet, Effect effect, int... levels) {
            PotionData ret = new PotionData();
            ret.requiresFullSet = requiresFullSet;
            ret.effectId = Objects.requireNonNull(effect.getRegistryName());
            ret.duration = TimeUtils.ticksFromSeconds(getDefaultDuration(ret.effectId));
            ret.levels = levels.clone();
            return ret;
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("full_set", this.requiresFullSet);
            json.addProperty("effect", this.effectId.toString());

            JsonArray levelsArray = new JsonArray();
            Arrays.stream(this.levels).forEach(levelsArray::add);
            json.add("level", levelsArray);
            return json;
        }

        static PotionData from(JsonObject json) {
            PotionData ret = new PotionData();
            ret.requiresFullSet = JSONUtils.getBoolean(json, "full_set", false);
            // Effect ID, get actual potion only when needed
            ret.effectId = new ResourceLocation(JSONUtils.getString(json, "effect", "unknown"));
            // Effects duration in seconds.
            float durationInSeconds = JSONUtils.getFloat(json, "duration", getDefaultDuration(ret.effectId));
            ret.duration = TimeUtils.ticksFromSeconds(durationInSeconds);

            // Level int or array
            JsonElement elementLevel = json.get("level");
            if (elementLevel == null) {
                throw new JsonParseException("level element not found, should be either int or array");
            }
            if (elementLevel.isJsonPrimitive()) {
                // Single level
                ret.levels = new int[]{JSONUtils.getInt(json, "level", 1)};
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

        static PotionData read(PacketBuffer buffer) {
            PotionData ret = new PotionData();
            ret.requiresFullSet = buffer.readBoolean();
            ret.effectId = buffer.readResourceLocation();
            ret.duration = buffer.readVarInt();
            ret.levels = buffer.readVarIntArray();
            return ret;
        }

        void write(PacketBuffer buffer) {
            buffer.writeBoolean(requiresFullSet);
            buffer.writeResourceLocation(effectId);
            buffer.writeVarInt(duration);
            buffer.writeVarIntArray(levels);
        }

        private static float getDefaultDuration(ResourceLocation effectId) {
            // Duration in seconds. The .5 should prevent flickering.
            return new ResourceLocation("night_vision").equals(effectId) ? 15.5f : 1.5f;
        }

        Optional<EffectInstance> getEffect(int pieceCount, boolean hasFullSet) {
            if (this.requiresFullSet && !hasFullSet) return Optional.empty();

            Effect potion = ForgeRegistries.POTIONS.getValue(effectId);
            if (potion == null) return Optional.empty();

            int effectLevel = levels[MathHelper.clamp(pieceCount - 1, 0, levels.length - 1)];
            if (effectLevel < 1) return Optional.empty();

            return Optional.of(new EffectInstance(potion, duration, effectLevel - 1, true, false));
        }

        public String getWikiLine() {
            String[] levelsText = new String[levels.length];
            for (int i = 0; i < levels.length; ++i) {
                levelsText[i] = Integer.toString(levels[i]);
            }

            Effect effect = ForgeRegistries.POTIONS.getValue(effectId);
            String effectName;
            if (effect != null) {
                effectName = effect.getDisplayName().getString();
            } else {
                effectName = effectId.toString();
            }

            return effectName + ": [" + String.join(", ", levelsText) + "]"
                    + (requiresFullSet ? " (full set required)" : "");
        }
    }
}
