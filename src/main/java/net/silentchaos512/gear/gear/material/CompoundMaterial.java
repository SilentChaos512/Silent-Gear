package net.silentchaos512.gear.gear.material;

import com.google.gson.*;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetMaterialStatsEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.utils.Color;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CompoundMaterial implements IMaterial {
    private final ResourceLocation materialId;
    private final String packName;
    private final Collection<IMaterialCategory> categories = new ArrayList<>();
    private Ingredient ingredient = Ingredient.EMPTY;
    private boolean visible = true;
    private boolean canSalvage = true;
    private ITextComponent displayName;
    @Nullable private ITextComponent namePrefix = null;

    public CompoundMaterial(ResourceLocation id, String packName) {
        this.materialId = id;
        this.packName = packName;
    }

    public static List<MaterialInstance> getSubMaterials(IMaterialInstance material) {
        return CompoundMaterialItem.getSubMaterials(material.getItem());
    }

    @Override
    public String getPackName() {
        return packName;
    }

    @Override
    public ResourceLocation getId() {
        return materialId;
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.COMPOUND;
    }

    @Nullable
    @Override
    public IMaterial getParent() {
        return null;
    }

    @Override
    public Collection<IMaterialCategory> getCategories(MaterialInstance material) {
        Set<IMaterialCategory> set = new HashSet<>(categories);
        for (MaterialInstance mat : getSubMaterials(material)) {
            set.addAll(mat.getCategories());
        }
        return set;
    }

    @Override
    public int getTier(PartType partType) {
        return 0;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public Optional<Ingredient> getPartSubstitute(PartType partType) {
        return Optional.empty();
    }

    @Override
    public boolean hasPartSubstitutes() {
        return false;
    }

    @Override
    public boolean canSalvage() {
        return false;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public Set<PartType> getPartTypes(IMaterialInstance material) {
        List<MaterialInstance> subMaterials = getSubMaterials(material);
        if (subMaterials.isEmpty()) {
            return Collections.emptySet();
        } else if (subMaterials.size() == 1) {
            return subMaterials.get(0).getPartTypes();
        }

        Set<PartType> set = new LinkedHashSet<>(subMaterials.get(0).getPartTypes());
        for (int i = 1; i < subMaterials.size(); ++i) {
            Set<PartType> set1 = subMaterials.get(i).getPartTypes();
            Set<PartType> toRemove = new HashSet<>();
            for (PartType type : set) {
                if (!set1.contains(type)) {
                    toRemove.add(type);
                }
            }
            set.removeAll(toRemove);
        }

        return set;
    }

    @Override
    public boolean allowedInPart(IMaterialInstance material, PartType partType) {
        return getPartTypes(material).contains(partType);
    }

    @Override
    public Collection<StatInstance> getStatModifiers(IMaterialInstance material, PartType partType, StatGearKey key, ItemStack gear) {
        // Get the materials and all the stat modifiers they provide for this stat
        Collection<MaterialInstance> materials = getSubMaterials(material);
        List<StatInstance> statMods = materials.stream()
                .flatMap(m -> m.getStatModifiers(partType, key).stream())
                .collect(Collectors.toList());

        ItemStat stat = key.getStat() instanceof ItemStat ? (ItemStat) key.getStat() : null;

        // Get any base modifiers for this material (could be none)
        //statMods.addAll(this.stats.get(stat));

        if (stat == null || statMods.isEmpty()) {
            // No modifiers for this stat, so doing anything else is pointless
            return statMods;
        }

        MaterialInstance matInst = material instanceof MaterialInstance ? (MaterialInstance) material : null;
        GetMaterialStatsEvent event = null;
        if (matInst != null) {
            // FIXME: Potentially bad cast, need to rework event
            event = new GetMaterialStatsEvent(matInst, stat, partType, statMods);
            MinecraftForge.EVENT_BUS.post(event);
        }

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        List<StatInstance> ret = new ArrayList<>(event != null ? event.getModifiers() : Collections.emptyList());
        for (StatInstance.Operation op : StatInstance.Operation.values()) {
            Collection<StatInstance> modsForOp = ret.stream().filter(s -> s.getOp() == op).collect(Collectors.toList());
            if (modsForOp.size() > 1) {
                StatInstance mod = compressModifiers(modsForOp, op);
                ret.removeIf(inst -> inst.getOp() == op);
                ret.add(mod);
            }
        }

        // Synergy
        if (stat.doesSynergyApply() && matInst != null) {
            final float synergy = SynergyUtils.getSynergy(partType, new ArrayList<>(materials), getTraits(matInst, partType, key.getGearType(), gear));
            if (!MathUtils.floatsEqual(synergy, 1.0f)) {
                final float multi = synergy - 1f;
                for (int i = 0; i < ret.size(); ++i) {
                    StatInstance oldMod = ret.get(i);
                    float value = oldMod.getValue();
                    // Taking the abs of value times multi makes negative mods become less negative
                    StatInstance newMod = oldMod.copySetValue(value + Math.abs(value) * multi);
                    ret.remove(i);
                    ret.add(i, newMod);
                }
            }
        }

        return ret;
    }

    @Override
    public Collection<StatGearKey> getStatKeys(PartType type) {
        return getSubMaterials(MaterialInstance.of(this)).stream()
                .flatMap(mat -> mat.get().getStatKeys(type).stream())
                .collect(Collectors.toSet());
    }

    private static StatInstance compressModifiers(Collection<StatInstance> mods, StatInstance.Operation operation) {
        // We do NOT want to average together max modifiers...
        if (operation == StatInstance.Operation.MAX) {
            return mods.stream()
                    .max((o1, o2) -> Float.compare(o1.getValue(), o2.getValue()))
                    .orElse(StatInstance.of(0, operation))
                    .copy();
        }

        return StatInstance.getWeightedAverageMod(mods, operation);
    }

    @Override
    public Collection<TraitInstance> getTraits(IMaterialInstance material, PartType partType, GearType gearType, ItemStack gear) {
        List<TraitInstance> ret = new ArrayList<>();
        List<MaterialInstance> list = new ArrayList<>(getSubMaterials(material));

        TraitHelper.getTraits(list, gearType, partType, ItemStack.EMPTY).forEach((trait, level) -> {
            TraitInstance inst = TraitInstance.of(trait, level);
            if (inst.conditionsMatch(list, gearType, partType, ItemStack.EMPTY)) {
                ret.add(inst);
            }
        });

        return ret;
    }

    @Override
    public boolean isCraftingAllowed(IMaterialInstance material, PartType partType, GearType gearType, @Nullable IInventory inventory) {
        if (!allowedInPart(material, partType)) {
            return false;
        }

        if (partType == PartType.MAIN) {
            StatGearKey key = StatGearKey.of(gearType.getDurabilityStat(), gearType);
            return !getStatModifiers(material, partType, key).isEmpty() && getStatUnclamped(material, partType, key, ItemStack.EMPTY) > 0;
        }

        return true;
    }

    @Override
    public IFormattableTextComponent getDisplayName(PartType partType, ItemStack gear) {
        return displayName.deepCopy();
    }

    @Nullable
    @Override
    public IFormattableTextComponent getDisplayNamePrefix(ItemStack gear, PartType partType) {
        return namePrefix != null ? namePrefix.deepCopy() : null;
    }

    @Override
    public int getNameColor(PartType partType, GearType gearType, IMaterialInstance material) {
        IMaterialDisplay model = MaterialDisplayManager.get(this);
        int color = model.getLayerColor(gearType, partType, material, 0);
        return Color.blend(color, Color.VALUE_WHITE, 0.25f) & 0xFFFFFF;
    }

    @Override
    public String getModelKey(IMaterialInstance material) {
        StringBuilder str = new StringBuilder(IMaterial.super.getModelKey(material) + "[");
        List<String> elements = new ArrayList<>();

        MaterialInstance last = null;
        IMaterial previous = null;
        int prevCount = 0;

        for (MaterialInstance mat : getSubMaterials(material)) {
            last = mat;
            if (mat.get() != previous) {
                if (previous != null) {
                    elements.add(mat.getModelKey() + (prevCount > 1 ? "*" + prevCount : ""));
                }
                previous = mat.get();
                prevCount = 0;
            }
            ++prevCount;
        }

        if (prevCount > 0) {
            elements.add(last.getModelKey());
        }

        return str.append(String.join(",", elements)).append("]").toString();
    }

    @Override
    public void updateIngredient(SyncMaterialCraftingItemsPacket msg) {
        if (msg.isValid()) {
            msg.getIngredient(this.materialId).ifPresent(ing -> this.ingredient = ing);
        }
    }

    @Override
    public String toString() {
        return "CompoundMaterial{" +
                "id=" + materialId +
                ", ingredient=" + ingredient +
                '}';
    }

    public static final class Serializer implements IMaterialSerializer<CompoundMaterial> {
        static final int PACK_NAME_MAX_LENGTH = 32;
        public static final ModResourceLocation NAME = SilentGear.getId("compound");

        @Override
        public CompoundMaterial deserialize(ResourceLocation id, String packName, JsonObject json) {
            CompoundMaterial ret = new CompoundMaterial(id, packName);

            deserializeCraftingItems(json, ret);
            deserializeNames(json, ret);
            deserializeAvailability(json, ret);

            return ret;
        }

        private static void deserializeAvailability(JsonObject json, CompoundMaterial ret) {
            JsonElement elementAvailability = json.get("availability");
            if (elementAvailability != null && elementAvailability.isJsonObject()) {
                JsonObject obj = elementAvailability.getAsJsonObject();

                deserializeCategories(obj.get("categories"), ret);
                ret.visible = JSONUtils.getBoolean(obj, "visible", ret.visible);
                ret.canSalvage = JSONUtils.getBoolean(obj, "can_salvage", ret.canSalvage);
            }
        }

        private static void deserializeCategories(@Nullable JsonElement json, CompoundMaterial material) {
            if (json != null) {
                if (json.isJsonArray()) {
                    JsonArray array = json.getAsJsonArray();
                    for (JsonElement elem : array) {
                        material.categories.add(MaterialCategories.get(elem.getAsString()));
                    }
                } else if (json.isJsonPrimitive()) {
                    material.categories.add(MaterialCategories.get(json.getAsString()));
                } else {
                    throw new JsonParseException("Expected 'categories' to be array or string");
                }
            }
        }

        private static void deserializeCraftingItems(JsonObject json, CompoundMaterial ret) {
            JsonElement craftingItems = json.get("crafting_items");
            if (craftingItems != null && craftingItems.isJsonObject()) {
                JsonElement main = craftingItems.getAsJsonObject().get("main");
                if (main != null) {
                    ret.ingredient = Ingredient.deserialize(main);
                }
            } else {
                throw new JsonSyntaxException("Expected 'crafting_items' to be an object");
            }
        }

        private static void deserializeNames(JsonObject json, CompoundMaterial ret) {
            // Name
            JsonElement elementName = json.get("name");
            if (elementName != null && elementName.isJsonObject()) {
                ret.displayName = deserializeText(elementName);
            } else {
                throw new JsonSyntaxException("Expected 'name' element");
            }

            // Name Prefix
            JsonElement elementNamePrefix = json.get("name_prefix");
            if (elementNamePrefix != null) {
                ret.namePrefix = deserializeText(elementNamePrefix);
            }
        }

        private static ITextComponent deserializeText(JsonElement json) {
            return Objects.requireNonNull(ITextComponent.Serializer.getComponentFromJson(json));
        }

        @Override
        public CompoundMaterial read(ResourceLocation id, PacketBuffer buffer) {
            CompoundMaterial material = new CompoundMaterial(id, buffer.readString(PACK_NAME_MAX_LENGTH));

            int categoryCount = buffer.readByte();
            for (int i = 0; i < categoryCount; ++i) {
                material.categories.add(MaterialCategories.get(buffer.readString()));
            }

            material.displayName = buffer.readTextComponent();
            if (buffer.readBoolean())
                material.namePrefix = buffer.readTextComponent();

            material.visible = buffer.readBoolean();
            material.canSalvage = buffer.readBoolean();
            material.ingredient = Ingredient.read(buffer);

            return material;
        }

        @Override
        public void write(PacketBuffer buffer, CompoundMaterial material) {
            buffer.writeString(material.packName.substring(0, Math.min(PACK_NAME_MAX_LENGTH, material.packName.length())), PACK_NAME_MAX_LENGTH);

            buffer.writeByte(material.categories.size());
            material.categories.forEach(cat -> buffer.writeString(cat.getName()));

            buffer.writeTextComponent(material.displayName);
            buffer.writeBoolean(material.namePrefix != null);
            if (material.namePrefix != null)
                buffer.writeTextComponent(material.namePrefix);

            buffer.writeBoolean(material.visible);
            buffer.writeBoolean(material.canSalvage);
            material.ingredient.write(buffer);
        }

        @Override
        public ResourceLocation getName() {
            return NAME;
        }
    }
}
