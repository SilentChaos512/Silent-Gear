package net.silentchaos512.gear.network;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.gear.material.MaterialManager;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncMaterialCraftingItemsPacket {
    private final Map<ResourceLocation, Ingredient> craftingItems = new HashMap<>();

    public SyncMaterialCraftingItemsPacket() {
        this(Util.make(() -> {
            Map<ResourceLocation, Ingredient> map = new HashMap<>();
            MaterialManager.getValues().forEach(m -> map.put(m.getId(), m.getIngredient(PartType.MAIN)));
            return map;
        }));
    }

    public SyncMaterialCraftingItemsPacket(Map<ResourceLocation, Ingredient> craftingItems) {
        this.craftingItems.putAll(craftingItems);
    }

    @Nullable
    public Ingredient getIngredient(ResourceLocation materialId) {
        return craftingItems.get(materialId);
    }

    public static SyncMaterialCraftingItemsPacket fromBytes(PacketBuffer buffer) {
        SyncMaterialCraftingItemsPacket packet = new SyncMaterialCraftingItemsPacket();
        int count = buffer.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.craftingItems.put(buffer.readResourceLocation(), Ingredient.read(buffer));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeVarInt(this.craftingItems.size());
        this.craftingItems.forEach((id, ingredient) -> {
            buffer.writeResourceLocation(id);
            ingredient.write(buffer);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        SilentGear.LOGGER.debug("Correcting material crafting items");
        MaterialManager.getValues().forEach(m -> m.updateIngredient(this));
        context.get().setPacketHandled(true);
    }
}
