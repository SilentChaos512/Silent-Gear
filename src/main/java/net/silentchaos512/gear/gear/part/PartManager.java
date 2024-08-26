package net.silentchaos512.gear.gear.part;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.network.NetworkEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.IGearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.PartJsonException;
import net.silentchaos512.gear.network.SyncGearPartsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class PartManager implements ResourceManagerReloadListener {
    public static final PartManager INSTANCE = new PartManager();

    public static final Marker MARKER = MarkerManager.getMarker("PartManager");

    private static final String DATA_PATH = "silentgear_parts";
    private static final Map<ResourceLocation, IGearPart> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Map<Item, IGearPart> LOOKUP_MAP = Collections.synchronizedMap(new IdentityHashMap<>());
    private static int highestMainPartTier = 0;
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    private PartManager() {}

    public static int getHighestMainPartTier() {
        return highestMainPartTier;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Map<ResourceLocation, Resource> resources = resourceManager.listResources(DATA_PATH, s -> s.toString().endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (MAP) {
            MAP.clear();
            LOOKUP_MAP.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading part files");

            String packName = "ERROR";
            for (ResourceLocation id : resources.keySet()) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                Optional<Resource> resourceOptional = resourceManager.getResource(id);
                if (resourceOptional.isPresent()) {
                    Resource iresource = resourceOptional.get();
                    packName = iresource.sourcePackId();
                    if (SilentGear.LOGGER.isTraceEnabled()) {
                        SilentGear.LOGGER.trace(MARKER, "Found likely part file: {}, trying to read as part {}", id, name);
                    }

                    JsonObject json = null;
                    try {
                        json = GsonHelper.fromJson(gson, IOUtils.toString(iresource.open(), StandardCharsets.UTF_8), JsonObject.class);
                    } catch (IOException ex) {
                        SilentGear.LOGGER.error(MARKER, "Could not read gear part {}", name, ex);
                        ERROR_LIST.add(String.format("%s (%s)", name, packName));
                    }

                    if (json == null) {
                        SilentGear.LOGGER.error(MARKER, "Could not load part {} as it's null or empty", name);
                    } else {
                        IGearPart part = tryDeserialize(name, packName, json);
                        if (part instanceof AbstractGearPart) {
                            ((AbstractGearPart) part).packName = iresource.sourcePackId();
                        }
                        addPart(part);
                        highestMainPartTier = Math.max(highestMainPartTier, part.getTier());
                    }
                }
            }

            SilentGear.LOGGER.info(MARKER, "Registered {} parts", MAP.size());
        }
    }

    @NotNull
    private static IGearPart tryDeserialize(ResourceLocation name, String packName, JsonObject json) {
        SilentGear.LOGGER.info("Deserializing part {} in pack {}", name, packName);
        try {
            return PartSerializers.deserialize(name, json);
        } catch (JsonSyntaxException ex) {
            throw new PartJsonException(name, packName, ex);
        }
    }

    private static void addPart(IGearPart part) {
        if (MAP.containsKey(part.getId())) {
            throw new IllegalStateException("Duplicate gear part " + part.getId());
        } else {
            MAP.put(part.getId(), part);
            for (ItemStack itemStack : part.getIngredient().getItems()) {
                Item item = itemStack.getItem();
                if (Blocks.BARRIER.asItem().equals(item)) {
                    // When a material has no valid ingredients for it (e.g. no items with the tag
                    // `forge:ingots/bismuth_brass` exist for bismuth_brass material), Ingredient.getItems() returns
                    // a single Barrier item. Just ignore it because it means the material isn't obtainable anyway.
                    continue;
                }

                IGearPart prevPart = LOOKUP_MAP.put(item, part);
                if (prevPart != null) {
                    SilentGear.LOGGER.error("Registered more than one part ({}, {}) for the same item ({})!", part, prevPart, item);
                }
            }
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
        ResourceLocation partId = SilentGear.getIdWithDefaultNamespace(id);
        return partId != null ? get(partId) : null;
    }

    @Nullable
    public static IGearPart from(ItemStack stack) {
        if (stack.isEmpty()) return null;

        IGearPart lookup = LOOKUP_MAP.get(stack.getItem());
        if (lookup != null && lookup.getIngredient().test(stack)) {
            return lookup;
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

    public static Collection<Component> getErrorMessages(ServerPlayer player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            return ImmutableList.of(
                    Component.literal("[Silent Gear] The following gear parts failed to load, check your log file:")
                            .withStyle(ChatFormatting.RED),
                    Component.literal(listStr)
            );
        }
        return ImmutableList.of();
    }
}
