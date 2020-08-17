package net.silentchaos512.gear.network;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialManager;

import java.util.*;
import java.util.function.Supplier;

public class SyncMaterialCraftingItemsPacket {
    private final Map<ResourceLocation, Ingredient> craftingItems;
    private final Map<ResourceLocation, Map<PartType, Ingredient>> partSubs;

    public SyncMaterialCraftingItemsPacket() {
        this(Collections.emptyList());
    }

    public SyncMaterialCraftingItemsPacket(Collection<IMaterial> materials) {
        this.craftingItems = new HashMap<>();
        materials.forEach(mat -> this.craftingItems.put(mat.getId(), mat.getIngredient()));

        this.partSubs = new HashMap<>();
        MaterialManager.getValues().forEach(mat -> {
            PartType.getValues().forEach(type -> {
                mat.getPartSubstitute(type).ifPresent(ing -> {
                    this.partSubs.computeIfAbsent(mat.getId(), id -> new HashMap<>()).put(type, ing);
                });
            });
        });
    }

    public boolean isValid() {
        return !craftingItems.isEmpty();
    }

    public Optional<Ingredient> getIngredient(ResourceLocation materialId) {
        return Optional.ofNullable(craftingItems.get(materialId));
    }

    public Map<PartType, Ingredient> getPartSubstitutes(ResourceLocation materialId) {
        return partSubs.getOrDefault(materialId, Collections.emptyMap());
    }

    public static SyncMaterialCraftingItemsPacket decode(PacketBuffer buffer) {
        SyncMaterialCraftingItemsPacket packet = new SyncMaterialCraftingItemsPacket();
        int count = buffer.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.craftingItems.put(buffer.readResourceLocation(), Ingredient.read(buffer));
        }

        int subCount = buffer.readVarInt();
        for (int i = 0; i < subCount; ++i) {
            Map<PartType, Ingredient> map = new HashMap<>();
            ResourceLocation id = buffer.readResourceLocation();
            int mapCount = buffer.readByte();

            for (int j = 0; j < mapCount; ++j) {
                PartType type = PartType.get(buffer.readResourceLocation());
                Ingredient ingredient = Ingredient.read(buffer);
                map.put(type, ingredient);
            }

            packet.partSubs.put(id, map);
        }

        return packet;
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeVarInt(this.craftingItems.size());
        this.craftingItems.forEach((id, ingredient) -> {
            buffer.writeResourceLocation(id);
            ingredient.write(buffer);
        });

        buffer.writeVarInt(this.partSubs.size());
        for (ResourceLocation id : this.partSubs.keySet()) {
            Map<PartType, Ingredient> map = this.partSubs.get(id);
            buffer.writeResourceLocation(id);
            buffer.writeByte(map.size());

            map.forEach((type, ingredient) -> {
                buffer.writeResourceLocation(type.getName());
                ingredient.write(buffer);
            });
        }
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        SilentGear.LOGGER.debug("Correcting material crafting items");
        MaterialManager.getValues().forEach(m -> m.updateIngredient(this));
        context.get().setPacketHandled(true);
    }
}
