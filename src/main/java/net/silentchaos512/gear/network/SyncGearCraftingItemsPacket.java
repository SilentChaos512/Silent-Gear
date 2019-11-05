package net.silentchaos512.gear.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartMaterial;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.PartMaterial;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SyncGearCraftingItemsPacket {
    private final Map<ResourceLocation, IPartMaterial> craftingItems = new HashMap<>();

    public SyncGearCraftingItemsPacket() {
        this(Util.make(() -> {
            Map<ResourceLocation, IPartMaterial> map = new HashMap<>();
            PartManager.getValues().forEach(p -> map.put(p.getId(), p.getMaterials()));
            return map;
        }));
    }

    public SyncGearCraftingItemsPacket(Map<ResourceLocation, IPartMaterial> craftingItems) {
        this.craftingItems.putAll(craftingItems);
    }

    public static SyncGearCraftingItemsPacket fromBytes(PacketBuffer buffer) {
        SilentGear.LOGGER.debug("Gear parts crafting items packet: {} bytes", buffer.readableBytes());
        SyncGearCraftingItemsPacket packet = new SyncGearCraftingItemsPacket();
        int count = buffer.readVarInt();

        for (int i = 0; i < count; ++i) {
            packet.craftingItems.put(buffer.readResourceLocation(), PartMaterial.read(buffer));
        }

        return packet;
    }

    public void toBytes(PacketBuffer buffer) {
        buffer.writeVarInt(this.craftingItems.size());
        this.craftingItems.forEach((id, material) -> {
            buffer.writeResourceLocation(id);
            material.write(buffer);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        SilentGear.LOGGER.debug("Correcting part crafting items");
        this.craftingItems.forEach((id, material) -> {
            IGearPart part = PartManager.get(id);
            if (part instanceof AbstractGearPart && material instanceof PartMaterial) {
                ((AbstractGearPart) part).updateCraftingItems((PartMaterial) material);
            }
        });
        context.get().setPacketHandled(true);
    }
}
