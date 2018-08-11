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

    public static final IModel MODEL = new ToolModel();

    public static final Map<UUID, Float> bowPull = new HashMap<>();

    @Nullable
    private final ResourceLocation headTexture;
    @Nullable
    private final ResourceLocation guardTexture;
    @Nullable
    private final ResourceLocation rodTexture;
    @Nullable
    private final ResourceLocation tipTexture;
    @Nullable
    private final ResourceLocation bowstringTexture;

    public ToolModel() {
        this.headTexture = null;
        this.guardTexture = null;
        this.rodTexture = null;
        this.tipTexture = null;
        this.bowstringTexture = null;
    }

    public ToolModel(@Nullable ResourceLocation head, @Nullable ResourceLocation guard, @Nullable ResourceLocation rod,
                     @Nullable ResourceLocation tip, @Nullable ResourceLocation bowstring) {
        this.headTexture = head;
        this.guardTexture = guard;
        this.rodTexture = rod;
        this.tipTexture = tip;
        this.bowstringTexture = bowstring;
    }

    @Nonnull
    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation head = null;
        ResourceLocation guard = null;
        ResourceLocation rod = null;
        ResourceLocation tip = null;
        ResourceLocation bowstring = null;

        if (textures.containsKey("head"))
            head = new ResourceLocation(textures.get("head"));
        if (textures.containsKey("guard"))
            guard = new ResourceLocation(textures.get("guard"));
        if (textures.containsKey("rod"))
            rod = new ResourceLocation(textures.get("rod"));
        if (textures.containsKey("tip"))
            tip = new ResourceLocation(textures.get("tip"));
        if (textures.containsKey("bowstring"))
            bowstring = new ResourceLocation(textures.get("bowstring"));

        return new ToolModel(head, guard, rod, tip, bowstring);
    }

    @Nonnull
    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        ResourceLocation head = null;
        ResourceLocation guard = null;
        ResourceLocation rod = null;
        ResourceLocation tip = null;
        ResourceLocation bowstring = null;

        if (customData.containsKey("head"))
            head = new ResourceLocation(customData.get("head"));
        if (customData.containsKey("guard"))
            guard = new ResourceLocation(customData.get("guard"));
        if (customData.containsKey("rod"))
            rod = new ResourceLocation(customData.get("rod"));
        if (customData.containsKey("tip"))
            tip = new ResourceLocation(customData.get("tip"));
        if (customData.containsKey("bowstring"))
            bowstring = new ResourceLocation(customData.get("bowstring"));

        return new ToolModel(head, guard, rod, tip, bowstring);
    }

    @Nonnull
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

        TRSRTransformation transform = TRSRTransformation.identity();

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();
        if (this.rodTexture != null)
            texBuilder.add(this.rodTexture);
        if (this.headTexture != null)
            texBuilder.add(this.headTexture);
        if (this.guardTexture != null)
            texBuilder.add(this.guardTexture);
        if (this.tipTexture != null)
            texBuilder.add(this.tipTexture);
        if (this.bowstringTexture != null)
            texBuilder.add(this.bowstringTexture);

        ImmutableList<ResourceLocation> textures = texBuilder.build();
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

        public OverrideHandler() {
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
                ItemPartData partTip = itemTool.getTipPart(stack);
                ItemPartData partBowstring = itemTool.getBowstringPart(stack);

                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                // Populate the map builder with textures, function handles null checks
                processTexture(stack, toolClass, PartPositions.HEAD, partHead, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.GUARD, partGuard, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.ROD, partRod, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.TIP, partTip, animationFrame, isBroken, builder);
                processTexture(stack, toolClass, PartPositions.BOWSTRING, partBowstring, animationFrame, isBroken, builder);

                ToolModel.Baked model = (ToolModel.Baked) originalModel;
                IModel parent = model.getParent().retexture(builder.build());
                Function<ResourceLocation, TextureAtlasSprite> textureGetter;
                textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.getVertexFormat(), textureGetter);
                GearClientHelper.modelCache.put(key, bakedModel);

                // Color cache
                ColorHandlers.gearColorCache.put(key, Stream.of(partRod, partHead, partGuard, partTip, partBowstring)
                        .filter(Objects::nonNull)
                        .map(part -> part.getColor(stack, animationFrame)).toArray(Integer[]::new));

                return bakedModel;
            }

            return GearClientHelper.modelCache.get(key);
        }

        private void processTexture(ItemStack stack, String toolClass, IPartPosition position, ItemPartData part, int animationFrame, boolean isBroken, ImmutableMap.Builder<String, String> builder) {
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

        private int getAnimationFrame(ItemStack stack, World world, EntityLivingBase entity) {
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

        public Baked(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
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
