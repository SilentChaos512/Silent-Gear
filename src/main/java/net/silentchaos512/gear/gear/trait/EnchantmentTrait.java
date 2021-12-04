package net.silentchaos512.gear.gear.trait;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.NameUtils;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.*;

public final class EnchantmentTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("enchantment");
    public static final ITraitSerializer<EnchantmentTrait> SERIALIZER = new Serializer<>(
            SERIALIZER_ID,
            EnchantmentTrait::new,
            EnchantmentTrait::readJson,
            EnchantmentTrait::readBuffer,
            EnchantmentTrait::writeBuffer
    );

    private final Map<String, List<EnchantmentData>> enchantments = new HashMap<>();

    private EnchantmentTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onRecalculatePost(TraitActionContext context) {
        ItemStack gear = context.getGear();
        GearType gearType = GearHelper.getType(gear);

        int traitLevel = context.getTraitLevel();
        enchantments.forEach((type, list) -> {
            if (gearType.matches(type)) {
                addEnchantments(gear, traitLevel, list);
            }
        });
    }

    /**
     * Removes all enchantments which were added by EnchantmentTraits
     *
     * @param gear The gear item
     */
    public static void removeTraitEnchantments(ItemStack gear) {
        Map<Enchantment, Triple<Integer, ResourceLocation, Integer>> enchants = getEnchantmentsOnGear(gear);
        Collection<Enchantment> toRemove = new ArrayList<>();

        for (Enchantment enchantment : enchants.keySet()) {
            Triple<Integer, ResourceLocation, Integer> info = enchants.get(enchantment);
            if (info.getRight() > 0) {
                toRemove.add(enchantment);
            }
        }

        for (Enchantment enchantment : toRemove) {
            enchants.remove(enchantment);
        }

        setEnchantmentsOnGear(gear, enchants);
    }

    private void addEnchantments(ItemStack gear, int traitLevel, Iterable<EnchantmentData> list) {
        Map<Enchantment, Triple<Integer, ResourceLocation, Integer>> enchants = getEnchantmentsOnGear(gear);

        for (EnchantmentData data : list) {
            Enchantment enchantment = data.getEnchantment();
            if (enchantment != null && !enchants.containsKey(enchantment)) {
                boolean compatible = true;
                for (Enchantment current : enchants.keySet()) {
                    if (!current.isCompatibleWith(enchantment)) {
                        compatible = false;
                        break;
                    }
                }

                if (compatible) {
                    int enchantmentLevel = data.getLevel(traitLevel);
                    enchants.put(enchantment, Triple.of(enchantmentLevel, this.getId(), traitLevel));
                    SilentGear.LOGGER.debug("Adding {} enchantment from {} trait to {}",
                            enchantment.getFullname(enchantmentLevel).getString(),
                            this.getDisplayName(traitLevel).getString(),
                            gear.getHoverName().getString());
                }
            }
        }

        setEnchantmentsOnGear(gear, enchants);
    }

    private static Map<Enchantment, Triple<Integer, ResourceLocation, Integer>> getEnchantmentsOnGear(ItemStack gear) {
        Map<Enchantment, Triple<Integer, ResourceLocation, Integer>> map = new LinkedHashMap<>();

        ListTag tagList = gear.getEnchantmentTags();
        for (int i = 0; i < tagList.size(); ++i) {
            CompoundTag nbt = tagList.getCompound(i);
            Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(nbt.getString("id")));

            if (enchantment != null) {
                int level = nbt.getInt("lvl");
                String traitKey = nbt.getString("SGearTrait");

                if (!traitKey.isEmpty()) {
                    try {
                        String[] parts = traitKey.split("#");
                        ResourceLocation traitId = new ResourceLocation(parts[0]);
                        Integer traitLevel = Integer.valueOf(parts[1]);
                        map.put(enchantment, Triple.of(level, traitId, traitLevel));
                    } catch (Exception ex) {
                        map.put(enchantment, Triple.of(level, new ResourceLocation("null"), 0));
                    }
                } else {
                    map.put(enchantment, Triple.of(level, new ResourceLocation("null"), 0));
                }
            }
        }

        return map;
    }

    private static void setEnchantmentsOnGear(ItemStack gear, Map<Enchantment, Triple<Integer, ResourceLocation, Integer>> map) {
        ListTag tagList = new ListTag();

        for (Map.Entry<Enchantment, Triple<Integer, ResourceLocation, Integer>> entry : map.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment != null) {
                int level = entry.getValue().getLeft();
                ResourceLocation traitId = entry.getValue().getMiddle();
                int traitLevel = entry.getValue().getRight();

                CompoundTag nbt = new CompoundTag();
                nbt.putString("id", NameUtils.from(enchantment).toString());
                nbt.putShort("lvl", (short) level);
                if (traitLevel > 0) {
                    nbt.putString("SGearTrait", traitId + "#" + traitLevel);
                }
                tagList.add(nbt);
            }
        }

        if (tagList.isEmpty()) {
            gear.removeTagKey("Enchantments");
        } else {
            gear.addTagElement("Enchantments", tagList);
        }
    }

    private static void readJson(EnchantmentTrait trait, JsonObject json) {
        if (!json.has("enchantments")) {
            throw new JsonParseException("Enchantment trait '" + trait.getId() + "' is missing 'enchantments' object");
        }

        // Parse potion effects array
        JsonObject jsonEffects = json.getAsJsonObject("enchantments");
        for (Map.Entry<String, JsonElement> entry : jsonEffects.entrySet()) {
            // Key (gear type)
            String key = entry.getKey();
            // Array of EnchantmentData objects
            JsonElement element = entry.getValue();

            if (!element.isJsonArray()) {
                throw new JsonParseException("Expected array, found " + element.getClass().getSimpleName());
            }

            JsonArray array = element.getAsJsonArray();
            List<EnchantmentData> list = new ArrayList<>();
            for (JsonElement elem : array) {
                if (!elem.isJsonObject()) {
                    throw new JsonParseException("Expected object, found " + elem.getClass().getSimpleName());
                }
                list.add(EnchantmentData.from(elem.getAsJsonObject()));
            }

            if (!list.isEmpty()) {
                trait.enchantments.put(key, list);
            }
        }
    }

    private static void readBuffer(EnchantmentTrait trait, FriendlyByteBuf buffer) {
        trait.enchantments.clear();
        int gearTypeCount = buffer.readByte();

        for (int typeIndex = 0; typeIndex < gearTypeCount; ++typeIndex) {
            List<EnchantmentData> list = new ArrayList<>();
            String gearType = buffer.readUtf();
            int dataCount = buffer.readByte();

            for (int dataIndex = 0; dataIndex < dataCount; ++dataIndex) {
                list.add(EnchantmentData.read(buffer));
            }

            trait.enchantments.put(gearType, list);
        }
    }

    private static void writeBuffer(EnchantmentTrait trait, FriendlyByteBuf buffer) {
        buffer.writeByte(trait.enchantments.size());
        for (Map.Entry<String, List<EnchantmentData>> entry : trait.enchantments.entrySet()) {
            buffer.writeUtf(entry.getKey());
            buffer.writeByte(entry.getValue().size());

            for (EnchantmentData data : entry.getValue()) {
                data.write(buffer);
            }
        }
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        this.enchantments.forEach((type, list) -> {
            ret.add("  - " + type);
            list.forEach(mod -> {
                ret.add("    - " + mod.getWikiLine());
            });
        });
        return ret;
    }

    public static class EnchantmentData {
        private ResourceLocation enchantmentId;
        private int[] levels;

        @SuppressWarnings("TypeMayBeWeakened")
        public static EnchantmentData of(Enchantment enchantment, int... levels) {
            EnchantmentData ret = new EnchantmentData();
            ret.enchantmentId = enchantment.getRegistryName();
            ret.levels = levels.clone();
            return ret;
        }

        public JsonObject serialize() {
            JsonObject json = new JsonObject();
            json.addProperty("enchantment", this.enchantmentId.toString());

            JsonArray levelsJson = new JsonArray();
            Arrays.stream(this.levels).forEach(levelsJson::add);
            json.add("level", levelsJson);
            return json;
        }

        static EnchantmentData from(JsonObject json) {
            EnchantmentData ret = new EnchantmentData();
            // Enchantment ID, get actual enchantment only when needed
            ret.enchantmentId = new ResourceLocation(GsonHelper.getAsString(json, "enchantment", "unknown"));

            // Level int or array
            JsonElement elementLevel = json.get("level");
            if (elementLevel == null) {
                throw new JsonParseException("level element not found, should be either int or array");
            }
            if (elementLevel.isJsonPrimitive()) {
                // Single level
                ret.levels = new int[]{GsonHelper.getAsInt(json, "level", 1)};
            } else if (elementLevel.isJsonArray()) {
                // Levels
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

        static EnchantmentData read(FriendlyByteBuf buffer) {
            EnchantmentData ret = new EnchantmentData();
            ret.enchantmentId = buffer.readResourceLocation();
            ret.levels = buffer.readVarIntArray();
            return ret;
        }

        void write(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(enchantmentId);
            buffer.writeVarIntArray(levels);
        }

        @Nullable
        Enchantment getEnchantment() {
            return ForgeRegistries.ENCHANTMENTS.getValue(enchantmentId);
        }

        int getLevel(int traitLevel) {
            int index = Mth.clamp(traitLevel, 1, levels.length) - 1;
            return levels[index];
        }

        public String getWikiLine() {
            String[] levelsText = new String[levels.length];
            for (int i = 0; i < levels.length; ++i) {
                levelsText[i] = Integer.toString(levels[i]);
            }
            return enchantmentId + ": [" + String.join(", ", levelsText) + "]";
        }
    }
}
