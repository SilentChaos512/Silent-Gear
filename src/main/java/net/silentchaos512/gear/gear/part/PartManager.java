package net.silentchaos512.gear.gear.part;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.network.SyncGearPartsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class PartManager implements IResourceManagerReloadListener {
    public static final PartManager INSTANCE = new PartManager();

    public static final Marker MARKER = MarkerManager.getMarker("PartManager");

    private static final String DATA_PATH = "silentgear_parts";
    private static final String DATA_PATH_OLD = "silentgear/parts";
    private static final Map<ResourceLocation, IGearPart> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static int highestMainPartTier = 0;
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    private PartManager() {}

    public static int getHighestMainPartTier() {
        return highestMainPartTier;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = getAllResources(resourceManager);
        if (resources.isEmpty()) return;

        synchronized (MAP) {
            MAP.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading part files");

            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                String packName = "ERROR";
                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getPackName();
                    if (SilentGear.LOGGER.isTraceEnabled()) {
                        SilentGear.LOGGER.trace(MARKER, "Found likely part file: {}, trying to read as part {}", id, name);
                    }

                    JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (json == null) {
                        SilentGear.LOGGER.error(MARKER, "Could not load part {} as it's null or empty", name);
                    } else if (!CraftingHelper.processConditions(json, "conditions")) {
                        SilentGear.LOGGER.info("Skipping loading gear part {} as it's conditions were not met", name);
                    } else {
                        IGearPart part = PartSerializers.deserialize(name, json);
                        if (part instanceof AbstractGearPart) {
                            ((AbstractGearPart) part).packName = iresource.getPackName();
                        }
                        addPart(part);
                        highestMainPartTier = Math.max(highestMainPartTier, part.getTier());
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error(MARKER, "Parsing error loading gear part {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error(MARKER, "Could not read gear part {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                }
            }

            SilentGear.LOGGER.info(MARKER, "Registered {} parts", MAP.size());
        }
    }

    private static Collection<ResourceLocation> getAllResources(IResourceManager resourceManager) {
        Collection<ResourceLocation> list = new ArrayList<>();
        list.addAll(resourceManager.getAllResourceLocations(DATA_PATH, s -> s.endsWith(".json")));
        list.addAll(resourceManager.getAllResourceLocations(DATA_PATH_OLD, s -> s.endsWith(".json")));
        return list;
    }

    private static void addPart(IGearPart part) {
        if (MAP.containsKey(part.getId())) {
            throw new IllegalStateException("Duplicate gear part " + part.getId());
        } else {
            MAP.put(part.getId(), part);
        }
    }

    public static Collection<IGearPart> getValues() {
        synchronized (MAP) {
            return MAP.values();
        }
    }

    public static List<IGearPart> getPartsOfType(PartType type) {
        return getValues().stream()
                .filter(part -> part.getType() == type)
                .collect(Collectors.toList());
    }

    @Deprecated
    public static Collection<IGearPart> getMains() {
        return getPartsOfType(PartType.MAIN);
    }

    @Deprecated
    public static Collection<IGearPart> getRods() {
        return getPartsOfType(PartType.ROD);
    }

    @Nullable
    public static IGearPart get(ResourceLocation id) {
        synchronized (MAP) {
            return MAP.get(id);
        }
    }

    @Nullable
    public static IGearPart get(String id) {
        ResourceLocation partId = ResourceLocation.tryCreate(id);
        return partId != null ? get(partId) : null;
    }

    @Nullable
    public static IGearPart from(ItemStack stack) {
        if (stack.isEmpty()) return null;

        // We can't reliable keep an IItemProvider -> IGearPart map anymore
        for (IGearPart part : getValues()) {
            if (part.getIngredient().test(stack)) {
                return part;
            }
        }

        return null;
    }

    public static void handlePartSyncPacket(SyncGearPartsPacket packet, Supplier<NetworkEvent.Context> context) {
        synchronized (MAP) {
            Map<ResourceLocation, IGearPart> oldParts = ImmutableMap.copyOf(MAP);
            MAP.clear();
            for (IGearPart part : packet.getParts()) {
                part.retainData(oldParts.get(part.getId()));
                MAP.put(part.getId(), part);
            }
            SilentGear.LOGGER.info("Read {} parts from server", MAP.size());
        }
        context.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            return ImmutableList.of(
                    new StringTextComponent("[Silent Gear] The following gear parts failed to load, check your log file:")
                            .mergeStyle(TextFormatting.RED),
                    new StringTextComponent(listStr)
            );
        }
        return ImmutableList.of();
    }
}
