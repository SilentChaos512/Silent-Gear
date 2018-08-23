package net.silentchaos512.gear.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.parts.*;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.item.gear.CoreBow;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class ToolModel implements IModel {
    private static final IModel MODEL = new ToolModel();
    private static final Set<IPartPosition> RENDER_ORDER = ImmutableSet.of(
            PartPositions.ROD,
            PartPositions.GRIP,
            PartPositions.HEAD,
            PartPositions.GUARD,
            PartPositions.TIP,
            PartPositions.BOWSTRING
    );

    public static final Map<UUID, Float> bowPull = new HashMap<>();

    private final ImmutableMap<String, String> textures;

    private ToolModel() {
        textures = ImmutableMap.of();
    }

    private ToolModel(ImmutableMap<String, String> textures) {
        this.textures = textures;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        return new ToolModel(textures);
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        return new ToolModel(customData);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();

//        SilentGear.log.info("Getting item part textures... what could go wrong?");
        for (String toolClass : ModItems.toolClasses.keySet()) {
            boolean hasGuard = "sword".equals(toolClass);
            ICoreItem item = ModItems.toolClasses.get(toolClass);

            for (int frame = 0; frame < item.getAnimationFrames(); ++frame)
                for (ItemPart part : PartRegistry.getValues()) {
                    if (part.isBlacklisted()) continue;
                    ItemPartData partData = ItemPartData.instance(part);

                    // Basic texture
                    // position could be HEAD, but I added ANY to make it clear this is not just mains.
                    ResourceLocation texBasic = partData.getTexture(ItemStack.EMPTY, toolClass, PartPositions.ANY, frame);
//                    SilentGear.log.info(String.format("    %s, frame=%d, part=%s, tex=%s", toolClass, frame, part.getKey().getResourcePath(), texBasic));
                    if (texBasic != null)
                        builder.add(texBasic);

                    // Broken texture
                    ResourceLocation texBroken = partData.getBrokenTexture(ItemStack.EMPTY, toolClass, PartPositions.ANY);
//                    SilentGear.log.info("      +broken: " + texBroken);
                    if (texBroken != null)
                        builder.add(texBroken);

                    // Guard texture for swords
                    if (hasGuard && part instanceof PartMain) {
                        ResourceLocation texGuard = partData.getTexture(ItemStack.EMPTY, toolClass, PartPositions.GUARD, frame);
//                        SilentGear.log.info("      +guard: " + texGuard);
                        if (texGuard != null)
                            builder.add(texGuard);
                    }
                }
        }

        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        // Get textures in proper render order
        ImmutableList<ResourceLocation> textures = RENDER_ORDER.stream()
                .map(IPartPosition::getModelIndex)
                .map(this.textures::get)
                .filter(Objects::nonNull)
                .map(ResourceLocation::new)
                .collect(ImmutableList.toImmutableList());
        int layerCount = textures.size();

        IBakedModel model = (new ItemLayerModel(textures)).bake(state, format, bakedTextureGetter);
        builder.addAll(model.getQuads(null, null, 0));

        return new ToolModel.Baked(this, createQuadsMap(model, layerCount), format, Maps.immutableEnumMap(transformMap), new HashMap<>());
    }

    private ImmutableList<ImmutableList<BakedQuad>> createQuadsMap(IBakedModel model, int layerCount) {
        List<ImmutableList.Builder<BakedQuad>> list = new ArrayList<>();
        for (int i = 0; i < layerCount; ++i)
            list.add(ImmutableList.builder());

        for (BakedQuad quad : model.getQuads(null, null, 0))
            list.get(quad.getTintIndex()).add(quad);

        ImmutableList.Builder<ImmutableList<BakedQuad>> builder = ImmutableList.builder();
        for (ImmutableList.Builder<BakedQuad> b : list)
            builder.add(b.build());

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
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
        }

        @Override
        public boolean accepts(@Nonnull ResourceLocation modelLocation) {
            boolean matchesPath = ModItems.toolClasses.keySet().stream().anyMatch(s -> modelLocation.getPath().equals(s));
            return modelLocation.getNamespace().equals(SilentGear.MOD_ID) && matchesPath;
        }

        @Nonnull
        @Override
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) {
            return MODEL;
        }
    }

    private static final class OverrideHandler extends ItemOverrideList {

        public static final OverrideHandler INSTANCE = new OverrideHandler();

        OverrideHandler() {
            super(ImmutableList.of());
        }

        @Override
        @Nonnull
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            if (!(stack.getItem() instanceof ICoreTool))
                return originalModel;

            int animationFrame = getAnimationFrame(stack, world, entity);
            String key = GearData.getCachedModelKey(stack, animationFrame);
            StackHelper.getTagCompound(stack, true).setString("debug_modelkey", key);

            // DEBUG:
            // model.cache.clear();

            if (!GearClientHelper.modelCache.containsKey(key)) {
                ICoreTool itemTool = (ICoreTool) stack.getItem();
                String toolClass = itemTool.getGearClass();
                boolean hasGuard = "sword".equals(toolClass);
                boolean isBroken = GearHelper.isBroken(stack);

                ItemPartData partHead = itemTool.getPrimaryPart(stack);
                ItemPartData partGuard = hasGuard ? itemTool.getSecondaryPart(stack) : null;
                ItemPartData partRod = itemTool.getRodPart(stack);
                ItemPartData partGrip = itemTool.getGripPart(stack);
                ItemPartData partTip = itemTool.getTipPart(stack);
                ItemPartData partBowstring = itemTool.getBowstringPart(stack);

                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                // Populate the map builder with textures, function handles null checks
                processTexture(stack, toolClass, PartPositions.HEAD, partHead, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.GUARD, partGuard, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.ROD, partRod, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.GRIP, partGrip, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.TIP, partTip, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.BOWSTRING, partBowstring, animationFrame, isBroken, builder);

                ToolModel.Baked model = (ToolModel.Baked) originalModel;
                IModel parent = model.getParent().retexture(builder.build());
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.getVertexFormat(), textureGetter);
                GearClientHelper.modelCache.put(key, bakedModel);

                // Color cache
                ColorHandlers.gearColorCache.put(key,
                        Stream.of(partRod, partGrip, partHead, partGuard, partTip, partBowstring)
                                .filter(Objects::nonNull)
                                .map(part -> part.getColor(stack, animationFrame))
                                .toArray(Integer[]::new));

                return bakedModel;
            }

            return GearClientHelper.modelCache.get(key);
        }

        private void processTexture(ItemStack stack, String toolClass, IPartPosition position, @Nullable ItemPartData part, int animationFrame, boolean isBroken, ImmutableMap.Builder<String, String> builder) {
            if (part != null) {
                ResourceLocation texture;
                if (isBroken)
                    texture = part.getBrokenTexture(stack, toolClass, position);
                else
                    texture = part.getTexture(stack, toolClass, position, animationFrame);

                if (texture != null)
                    builder.put(position.getModelIndex(), texture.toString());
            }
        }

        private int getAnimationFrame(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
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

        Baked(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
            super(parent, immutableList, format, transforms, cache);
            instance = this;
        }

        @Nonnull
        @Override
        public ItemOverrideList getOverrides() {
            return OverrideHandler.INSTANCE;
        }

        @Override
        public boolean isBuiltInRenderer() {
            return true;
        }
    }
}
