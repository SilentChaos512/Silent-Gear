package net.silentchaos512.gear.gear.material;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialSerializer;
import net.silentchaos512.gear.api.material.MaterialDisplay;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.network.SyncMaterialCraftingItemsPacket;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A temporary material type to allow older data packs to work with the new material system. Will
 * remove in 1.16.x.
 */
public class SimplePartAdapterMaterial implements IMaterial {
    private final ResourceLocation materialId;
    private final Map<PartType, IGearPart> parts = new HashMap<>();
    private final Map<PartType, IMaterialDisplay> display = new HashMap<>();

    public SimplePartAdapterMaterial(ResourceLocation materialId) {
        this.materialId = materialId;
    }

    public void addPart(IGearPart part) {
        if (this.parts.containsKey(part.getType())) {
            throw new IllegalArgumentException("Already have part of type " + part.getType() + " for adapter material " + this.materialId);
        }
        this.parts.put(part.getType(), part);
        // FIXME: MaterialDisplay for adapter mats
        this.display.put(part.getType(), MaterialDisplay.DEFAULT);
    }

    private Optional<IGearPart> getPart(PartType partType) {
        return Optional.ofNullable(this.parts.get(partType));
    }

    @Override
    public String getPackName() {
        return "AUTO_ADAPTER";
    }

    @Override
    public ResourceLocation getId() {
        return materialId;
    }

    @Override
    public IMaterialSerializer<?> getSerializer() {
        return MaterialSerializers.SIMPLE_ADAPTER;
    }

    @Nullable
    @Override
    public IMaterial getParent() {
        return null;
    }

    @Override
    public int getTier(PartType partType) {
        return getPart(partType)
                .map(IGearPart::getTier)
                .orElse(-1);
    }

    @Override
    public Ingredient getIngredient(PartType partType) {
        IGearPart part = getPart(PartType.MAIN).orElse(getPart(partType).orElse(null));
        return part != null ? part.getMaterials().getNormal() : Ingredient.EMPTY;
    }

    @Override
    public Set<PartType> getPartTypes() {
        return parts.keySet();
    }

    @Override
    public boolean allowedInPart(PartType partType) {
        return getPart(partType).isPresent();
    }

    @Override
    public void retainData(@Nullable IMaterial oldMaterial) {
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStat stat, PartType partType, ItemStack gear) {
        return getPart(partType)
                .map(p -> p.getStatModifiers(gear, stat, PartData.of(p)))
                .orElse(Collections.emptyList());
    }

    @Override
    public Collection<PartTraitInstance> getTraits(PartType partType, ItemStack gear) {
        return getPart(partType)
                .map(p -> p.getTraits(gear, PartData.of(p)))
                .orElse(Collections.emptyList());
    }

    @Override
    public boolean isCraftingAllowed(PartType partType, GearType gearType) {
        return true;
    }

    @Override
    public int getPrimaryColor(ItemStack gear, PartType partType) {
        return getPart(partType)
                .map(p -> PartData.of(p).getColor(gear, 0))
                .orElse(Color.VALUE_WHITE);
    }

    @Override
    public PartTextureType getTexture(PartType partType, ItemStack gear) {
        return getPart(partType)
                .map(p -> p.getLiteTexture(PartData.of(p), gear))
                .orElse(PartTextureType.ABSENT);
    }

    @Override
    public IMaterialDisplay getMaterialDisplay(ItemStack gear, PartType partType) {
        return this.display.getOrDefault(partType, MaterialDisplay.DEFAULT);
    }

    @Override
    public ITextComponent getDisplayName(PartType partType, ItemStack gear) {
        return getPart(partType)
                .map(p -> p.getDisplayName(PartData.of(p), gear))
                .orElse(new StringTextComponent("INVALID"));
    }

    @Override
    public void updateIngredient(SyncMaterialCraftingItemsPacket msg) {
    }

    public static class Serializer implements IMaterialSerializer<SimplePartAdapterMaterial> {
        @Override
        public SimplePartAdapterMaterial deserialize(ResourceLocation id, String packName, JsonObject json) {
            // Not needed, adapters don't load from JSON
            return new SimplePartAdapterMaterial(id);
        }

        @Override
        public SimplePartAdapterMaterial read(ResourceLocation id, PacketBuffer buffer) {
            SimplePartAdapterMaterial ret = new SimplePartAdapterMaterial(id);
            int partCount = buffer.readByte();
            for (int i = 0; i < partCount; ++i) {
                ResourceLocation partId = buffer.readResourceLocation();
                IGearPart part = PartManager.get(partId);
                if (part != null) {
                    ret.addPart(part);
                } else {
                    SilentGear.LOGGER.warn("Read unknown gear part '{}' in adapter material '{}'", partId, id);
                }
            }
            return ret;
        }

        @Override
        public void write(PacketBuffer buffer, SimplePartAdapterMaterial material) {
            buffer.writeByte(material.parts.size());
            material.parts.values().forEach(part -> buffer.writeResourceLocation(part.getId()));
        }

        @Override
        public ResourceLocation getName() {
            return SilentGear.getId("simple_adapter");
        }
    }
}
