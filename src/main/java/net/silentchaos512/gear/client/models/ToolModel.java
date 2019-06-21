package net.silentchaos512.gear.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.PartDataList;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.gear.CoreBow;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public final class ToolModel implements IUnbakedModel {
    private static final IUnbakedModel MODEL = new ToolModel();

    public static final Map<UUID, Float> bowPull = new HashMap<>();

    private final ImmutableMap<String, String> textures;

    private ToolModel() {
        textures = ImmutableMap.of();
    }

    private ToolModel(ImmutableMap<String, String> textures) {
        this.textures = textures;
    }

    @Override
    public ToolModel retexture(ImmutableMap<String, String> textures) {
        return new ToolModel(textures);
    }

    @Override
    public ToolModel process(ImmutableMap<String, String> customData) {
        return new ToolModel(customData);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

//        SilentGear.log.info("Getting item part textures... what could go wrong?");
        for (ICoreTool item : ModItems.toolClasses.values()) {
            GearType toolClass = item.getGearType();
            boolean hasGuard = item.hasSwordGuard();

            for (int frame = 0; frame < item.getAnimationFrames(); ++frame)
                for (IGearPart part : PartManager.getValues()) {
//                    if (part.isBlacklisted()) continue;
                    PartData partData = PartData.of(part);

                    // Basic texture
                    // position could be HEAD, but I added ANY to make it clear this is not just mains.
                    ResourceLocation texBasic = partData.getTexture(ItemStack.EMPTY, toolClass, part.getPartPosition(), frame);
//                    SilentGear.log.info(String.format("    %s, frame=%d, part=%s, tex=%s", toolClass, frame, part.getKey().getResourcePath(), texBasic));
                    if (texBasic != null)
                        builder.add(texBasic);

                    // Broken texture
                    ResourceLocation texBroken = partData.getBrokenTexture(ItemStack.EMPTY, toolClass, part.getPartPosition());
//                    SilentGear.log.info("      +broken: " + texBroken);
                    if (texBroken != null)
                        builder.add(texBroken);

                    // Guard texture for swords
                    if (hasGuard && part.getType() == PartType.MAIN) {
                        ResourceLocation texGuard = partData.getTexture(ItemStack.EMPTY, toolClass, PartPositions.GUARD, frame);
//                        SilentGear.log.info("      +guard: " + texGuard);
                        if (texGuard != null)
                            builder.add(texGuard);
                    }
                }
        }

        return builder.build();
    }


    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(ItemCameraTransforms.DEFAULT);
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        // Get textures in proper render order
        ImmutableList<ResourceLocation> textures = IPartPosition.RENDER_LAYERS.stream()
                .map(IPartPosition::getModelIndex)
                .map(this.textures::get)
                .filter(Objects::nonNull)
                .map(ResourceLocation::new)
                .collect(ImmutableList.toImmutableList());

        IBakedModel model = (new ItemLayerModel(textures)).bake(bakery, spriteGetter, sprite, format);
        builder.addAll(model.getQuads(null, null, SilentGear.random));

        int layerCount = textures.size();
        return new ToolModel.Baked(this, createQuadsMap(model, layerCount), format, Maps.immutableEnumMap(transformMap), new HashMap<>());
    }

    private static ImmutableList<ImmutableList<BakedQuad>> createQuadsMap(IBakedModel model, int layerCount) {
        List<ImmutableList.Builder<BakedQuad>> list = new ArrayList<>();
        for (int i = 0; i < layerCount; ++i) {
            list.add(ImmutableList.builder());
        }

        for (BakedQuad quad : model.getQuads(null, null, SilentGear.random)) {
            list.get(quad.getTintIndex()).add(quad);
        }

        ImmutableList.Builder<ImmutableList<BakedQuad>> builder = ImmutableList.builder();
        for (ImmutableList.Builder<BakedQuad> b : list) {
            builder.add(b.build());
        }

        return builder.build();
    }

    @Nonnull
    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    public static final class Loader implements ICustomModelLoader {
        public static Loader INSTANCE = new Loader();

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            SilentGear.LOGGER.debug("ToolModel.Loader#onResourceManagerReload");
        }

        @Override
        public boolean accepts(@Nonnull ResourceLocation modelLocation) {
            boolean matchesPath = ModItems.toolClasses.keySet().stream().anyMatch(s -> modelLocation.getPath().equals(s));
            return modelLocation.getNamespace().equals(SilentGear.MOD_ID) && matchesPath;
        }

        @Nonnull
        @Override
        public IUnbakedModel loadModel(@Nonnull ResourceLocation modelLocation) {
            return MODEL;
        }
    }

    private static final class OverrideHandler extends ItemOverrideList {
        public static final OverrideHandler INSTANCE = new OverrideHandler();

        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel parentModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            if (!(stack.getItem() instanceof ICoreTool)) return parentModel;

            int animationFrame = getAnimationFrame(stack, world, entity);
            String key = GearData.getCachedModelKey(stack, animationFrame);
            stack.getOrCreateTag().putString("debug_modelkey", key);

            // DEBUG:
            // model.cache.clear();

            if (!GearClientHelper.modelCache.containsKey(key)) {
                ICoreTool itemTool = (ICoreTool) stack.getItem();
                GearType toolClass = itemTool.getGearType();
                boolean isBroken = GearHelper.isBroken(stack);

                PartDataList parts = GearData.getConstructionParts(stack);
                Map<IPartPosition, PartData> renderLayers = new LinkedHashMap<>();
                for (IPartPosition position : IPartPosition.RENDER_LAYERS) {
                    // We have a few special cases. These return a default part for rendering if the
                    // part is missing or invalid.
                    if (position == PartPositions.HEAD) {
                        renderLayers.put(PartPositions.HEAD, itemTool.getPrimaryPart(stack));
                    } else if (position == PartPositions.GUARD && itemTool.hasSwordGuard()) {
                        renderLayers.put(PartPositions.GUARD, itemTool.getSecondaryPart(stack));
                    } else if (position == PartPositions.ROD) {
                        renderLayers.put(PartPositions.ROD, itemTool.getRodPart(stack));
                    } else {
                        // For most cases just get the first (usually only) matching part in the list
                        PartData part = parts.firstInPosition(position);
                        if (part != null) renderLayers.put(position, part);
                    }
                }

                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                // Populate the map builder with textures, function handles null checks
                renderLayers.forEach((pos, part) ->
                        processTexture(stack, toolClass, pos, part, animationFrame, isBroken, builder));

                ToolModel.Baked model = (ToolModel.Baked) parentModel;
                IModel parent = model.getParent().retexture(builder.build());
                // TODO: What's this? Do we need to get a model and if so, how?
                Function<ResourceLocation, IUnbakedModel> modelGetter = location -> null;
                Function<ResourceLocation, TextureAtlasSprite> spriteGetter = location ->
                        Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
/*                IBakedModel bakedModel = parent.bake(
                        modelGetter,
                        spriteGetter,
                        new SimpleModelState(ImmutableMap.of()),
                        false,
                        model.getVertexFormat());
                GearClientHelper.modelCache.put(key, bakedModel);

                // Color cache
                ColorHandlers.gearColorCache.put(key, renderLayers.values().stream()
                        .map(part -> part.getColor(stack, animationFrame))
                        .toArray(Integer[]::new));

                return bakedModel;*/
                return null;
            }

            return GearClientHelper.modelCache.get(key);
        }

        private static void processTexture(ItemStack stack, GearType toolClass, IPartPosition position, @Nullable PartData part, int animationFrame, boolean isBroken, ImmutableMap.Builder<String, String> builder) {
            if (part != null) {
                ResourceLocation texture = isBroken
                        ? part.getBrokenTexture(stack, toolClass, position)
                        : part.getTexture(stack, toolClass, position, animationFrame);
                if (texture != null) {
//                    SilentGear.log.debug("{}: {}", texture, part);
                    builder.put(position.getModelIndex(), texture.toString());
                }
            }
        }

        private int getAnimationFrame(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            if (stack.getItem() instanceof CoreBow) {
                UUID uuid = GearData.getUUID(stack);
                if (bowPull.containsKey(uuid)) {
                    float pull = bowPull.get(uuid);
                    if (pull > 0.9f)
                        return 3;
                    else if (pull > 0.65f)
                        return 2;
                    else
                        return 1;
                }
            }

            return 0;
        }
    }

    public static final class Baked extends AbstractToolModel {
        public static Baked instance;

        Baked(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
            super(parent, immutableList, format, transforms, cache);
            instance = this;
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return OverrideHandler.INSTANCE;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
            return null;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return true;
        }
    }
}
