package net.silentchaos512.gear.gear.material;

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
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.network.SyncMaterialsPacket;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class MaterialManager implements IResourceManagerReloadListener {
    public static final MaterialManager INSTANCE = new MaterialManager();

    public static final Marker MARKER = MarkerManager.getMarker("MaterialManager");

    private static final String DATA_PATH = "silentgear_materials";
    private static final Map<ResourceLocation, IPartMaterial> MAP = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final Collection<String> ERROR_LIST = new ArrayList<>();

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
        Collection<ResourceLocation> resources = resourceManager.getAllResourceLocations(DATA_PATH, s -> s.endsWith(".json"));
        if (resources.isEmpty()) return;

        synchronized (MAP) {
            MAP.clear();
            ERROR_LIST.clear();
            SilentGear.LOGGER.info(MARKER, "Reloading material files");

            for (ResourceLocation id : resources) {
                String path = id.getPath().substring(DATA_PATH.length() + 1, id.getPath().length() - ".json".length());
                ResourceLocation name = new ResourceLocation(id.getNamespace(), path);

                String packName = "ERROR";
                try (IResource iresource = resourceManager.getResource(id)) {
                    packName = iresource.getPackName();
                    JsonObject json = JSONUtils.fromJson(gson, IOUtils.toString(iresource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                    if (json == null) {
                        SilentGear.LOGGER.error(MARKER, "Could not load material {} as it's null or empty", name);
                    } else if (!CraftingHelper.processConditions(json, "conditions")) {
                        SilentGear.LOGGER.info("Skipping loading material {} as its conditions were not met", name);
                    } else {
                        IPartMaterial material = PartMaterial.Serializer.deserialize(name, packName, json);
                        MAP.put(material.getId(), material);
                    }
                } catch (IllegalArgumentException | JsonParseException ex) {
                    SilentGear.LOGGER.error(MARKER, "Parsing error loading material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                } catch (IOException ex) {
                    SilentGear.LOGGER.error(MARKER, "Could not read material {}", name, ex);
                    ERROR_LIST.add(String.format("%s (%s)", name, packName));
                }
            }
        }
    }

    public static Collection<IPartMaterial> getValues() {
        synchronized (MAP) {
            return MAP.values();
        }
    }

    @Nullable
    public static IPartMaterial get(@Nullable ResourceLocation id) {
        if (id == null) return null;

        synchronized (MAP) {
            return MAP.get(id);
        }
    }

    @Nullable
    public static IPartMaterial from(ItemStack stack) {
        if (stack.isEmpty()) return null;

        for (IPartMaterial material : getValues()) {
            if (material.getIngredient(PartType.MAIN).test(stack)) {
                return material;
            }
        }

        return null;
    }

    public static void handleSyncPacket(SyncMaterialsPacket msg, Supplier<NetworkEvent.Context> ctx) {
        synchronized (MAP) {
            Map<ResourceLocation, IPartMaterial> oldMaterials = ImmutableMap.copyOf(MAP);
            MAP.clear();
            msg.getMaterials().forEach(mat -> {
                mat.retainData(oldMaterials.get(mat.getId()));
                MAP.put(mat.getId(), mat);
            });
            SilentGear.LOGGER.info("Read {} materials from server", MAP.size());
        }
        ctx.get().setPacketHandled(true);
    }

    public static Collection<ITextComponent> getErrorMessages(ServerPlayerEntity player) {
        if (!ERROR_LIST.isEmpty()) {
            String listStr = String.join(", ", ERROR_LIST);
            return ImmutableList.of(
                    new StringTextComponent("[Silent Gear] The following materials failed to load, check your log file:")
                            .applyTextStyle(TextFormatting.RED),
                    new StringTextComponent(listStr)
            );
        }
        return ImmutableList.of();
    }
}
