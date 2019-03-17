package net.silentchaos512.gear.parts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.network.SyncGearPartsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class PartManager implements IResourceManagerReloadListener {
    public static final PartManager INSTANCE = new PartManager();

    public static final Marker MARKER = MarkerManager.getMarker("PartManager");

    private static final Map<ResourceLocation, IGearPart> MAP = new LinkedHashMap<>();
    private static final Map<IItemProvider, IGearPart> ITEM_TO_PART = new HashMap<>();
    private static int highestMainPartTier = 0;

    private PartManager() {}

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(
                "silentgear/parts", s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        MAP.clear();
        ITEM_TO_PART.clear();
        SilentGear.LOGGER.info(MARKER, "Reloading part files");

        for (ResourceLocation id : resources) {
            try (IResource iresource = resourceManager.getResource(id)) {
                String path = id.getPath().substring("silentgear/parts/".length(), id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);
                SilentGear.LOGGER.debug(MARKER, "Found likely part file: {}, trying to read as part {}", id, name);

                JsonObject json = JsonUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                if (json == null) {
                    SilentGear.LOGGER.error(MARKER, "Could not load part {} as it's null or empty", name);
                } else {
                    addPart(PartSerializers.deserialize(name, json));
                }
            } catch (IllegalArgumentException | JsonParseException ex) {
                SilentGear.LOGGER.error(MARKER, "Parsing error loading gear part {}", id, ex);
            } catch (IOException ex) {
                SilentGear.LOGGER.error(MARKER, "Could not read gear part {}", id, ex);
            }
        }

        SilentGear.LOGGER.info(MARKER, "Finished! Registered {} parts", MAP.size());
    }

    private static void addPart(IGearPart part) {
        if (MAP.containsKey(part.getId())) {
            throw new IllegalStateException("Duplicate gear part " + part.getId());
        } else {
            MAP.put(part.getId(), part);
        }
    }

    public static Collection<IGearPart> getValues() {
        return MAP.values();
    }

    public static Collection<IGearPart> getPartsOfType(PartType type) {
        // TODO: cache this?
        return getValues().stream()
                .filter(part -> part.getType() == type)
                .collect(Collectors.toList());
    }

    public static Collection<IGearPart> getMains() {
        // TODO: cache this?
        return getPartsOfType(PartType.MAIN);
    }

    public static Collection<IGearPart> getRods() {
        // TODO: cache this?
        return getPartsOfType(PartType.ROD);
    }

    @Nullable
    public static IGearPart get(ResourceLocation id) {
        return MAP.get(id);
    }

    @Nullable
    public static IGearPart from(ItemStack stack) {
        if (stack.isEmpty()) return null;

        IItemProvider item = stack.getItem();
        if (ITEM_TO_PART.containsKey(item)) {
            return ITEM_TO_PART.get(item);
        }

        for (IGearPart part : MAP.values()) {
            if (part.getMaterials().matches(item)) {
                ITEM_TO_PART.put(item, part);
                return part;
            }
        }

        return null;
    }

    /**
     * Try to get a fallback part for the part type. Some types may not have a fallback.
     *
     * @param type The part type
     * @return The fallback part if it exists
     * @throws NullPointerException If the fallback is unknown or cannot be found
     */
    public static IGearPart tryGetFallback(PartType type) {
        // TODO: Probably needs a lot of work...
        ResourceLocation name = null;
        if (type == PartType.MAIN)
            name = PartConst.FALLBACK_MAIN;
        else if (type == PartType.ROD)
            name = PartConst.FALLBACK_ROD;
        else if (type == PartType.BOWSTRING)
            name = PartConst.FALLBACK_BOWSTRING;

        if (name == null)
            throw new NullPointerException("Don't know fallback for part type '" + type + "'");

        IGearPart part = get(name);
        if (part == null)
            throw new NullPointerException("Fallback part '" + name + "' was not found");
        return part;
    }

    public static void handlePartSyncPacket(SyncGearPartsPacket packet, Supplier<NetworkEvent.Context> context) {
        MAP.clear();
        packet.getParts().forEach(part -> MAP.put(part.getId(), part));
        SilentGear.LOGGER.info("Read {} parts from server", MAP.size());
        context.get().setPacketHandled(true);
    }
}
