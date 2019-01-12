package net.silentchaos512.gear.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartPositions;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.ToolHead;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public class ToolHeadModel implements IModel {
    public static final IModel MODEL = new ToolHeadModel();

    @Nullable private final ResourceLocation textureHead;
    @Nullable private final ResourceLocation textureGuard;
    @Nullable private final ResourceLocation textureHighlight;

    public ToolHeadModel() {
        this.textureHead = null;
        this.textureGuard = null;
        this.textureHighlight = null;
    }

    public ToolHeadModel(@Nullable ResourceLocation textureHead, @Nullable ResourceLocation textureGuard, @Nullable ResourceLocation textureHighlight) {
        this.textureHead = textureHead;
        this.textureGuard = textureGuard;
        this.textureHighlight = textureHighlight;
    }

    @Nonnull
    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation head = null;
        if (textures.containsKey("head"))
            head = new ResourceLocation(textures.get("head"));

        ResourceLocation guard = null;
        if (textures.containsKey("guard"))
            guard = new ResourceLocation(textures.get("guard"));
        
        ResourceLocation highlight = null;
        if (textures.containsKey("highlight"))
            highlight = new ResourceLocation(textures.get("highlight"));

        return new ToolHeadModel(head, guard, highlight);
    }

    @Nonnull
    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        ResourceLocation head = null;
        if (customData.containsKey("head"))
            head = new ResourceLocation(customData.get("head"));

        ResourceLocation guard = null;
        if (customData.containsKey("guard"))
            guard = new ResourceLocation(customData.get("guard"));

        ResourceLocation highlight = null;
        if (customData.containsKey("highlight"))
            highlight = new ResourceLocation(customData.get("highlight"));

        return new ToolHeadModel(head, guard, highlight);
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
        for (String toolClass : ModItems.toolClasses.keySet()) {
            for (PartMain part : PartRegistry.getMains()) {
                ItemPartData partData = ItemPartData.instance(part);
                ResourceLocation textureHead = partData.getTexture(ItemStack.EMPTY, toolClass, PartPositions.HEAD, 0);
                if (textureHead != null)
                    builder.add(textureHead);

                if ("sword".equals(toolClass)) {
                    ResourceLocation textureGuard = partData.getTexture(ItemStack.EMPTY, toolClass, PartPositions.GUARD, 0);
                    if (textureGuard != null)
                        builder.add(textureGuard);
                }
            }

            final ItemPartData highlight = ItemPartData.instance(ModMaterials.highlight);
            final ResourceLocation highlightTexture = highlight.getTexture(ItemStack.EMPTY, toolClass, PartPositions.HIGHLIGHT, 0);
            if (highlightTexture != null)
                builder.add(highlightTexture);
        }
        return builder.build();
    }

    @Nonnull
    @Override
    public IBakedModel bake(@Nonnull IModelState state, @Nonnull VertexFormat format, @Nonnull Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();
        if (this.textureHead != null)
            texBuilder.add(this.textureHead);
        if (this.textureGuard != null)
            texBuilder.add(this.textureGuard);
        if (this.textureHighlight != null)
            texBuilder.add(this.textureHighlight);

        ImmutableList<ResourceLocation> textures = texBuilder.build();

        IBakedModel model = (new ItemLayerModel(textures)).bake(state, format, bakedTextureGetter);
        builder.addAll(model.getQuads(null, null, 0));

        return new ToolHeadModel.Baked(this, builder.build(), format, Maps.immutableEnumMap(transformMap), new HashMap<>());
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
        }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(SilentGear.MOD_ID) && modelLocation.getPath().equals("tool_head");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
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
            if (stack.getItem() != ModItems.toolHead)
                return originalModel;

            ToolHeadModel.Baked model = (ToolHeadModel.Baked) originalModel;

            String toolClass = ToolHead.getToolClass(stack);
            boolean hasGuard = "sword".equals(toolClass);

            ItemPartData primaryPart = ToolHead.getPrimaryPart(stack);
            ItemPartData secondaryPart = hasGuard ? ToolHead.getSecondaryPart(stack) : null;
            ItemPartData highlightPart = getHighlightPart(primaryPart, stack);

            String key = ToolHead.getModelKey(toolClass, primaryPart, secondaryPart);

            if (!GearClientHelper.modelCache.containsKey(key)) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
                ResourceLocation textureHead = primaryPart == null ? null : primaryPart.getTexture(stack, toolClass, PartPositions.HEAD, 0);
                ResourceLocation textureGuard = secondaryPart == null ? null : secondaryPart.getTexture(stack, toolClass, PartPositions.GUARD, 0);
                ResourceLocation textureHighlight = highlightPart == null ? null : highlightPart.getTexture(stack, toolClass, PartPositions.HIGHLIGHT, 0);

                if (textureHead != null)
                    builder.put("head", textureHead.toString());
                if (textureGuard != null)
                    builder.put("guard", textureGuard.toString());
                if (textureHighlight != null)
                    builder.put("highlight", textureHighlight.toString());

                IModel parent = model.getParent().retexture(builder.build());
                Function<ResourceLocation, TextureAtlasSprite> textureGetter = location ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.getVertexFormat(), textureGetter);
                GearClientHelper.modelCache.put(key, bakedModel);

                // Color cache
                ColorHandlers.gearColorCache.put(key, Stream.of(primaryPart, secondaryPart)
                        .filter(Objects::nonNull)
                        .map(part -> part.getColor(stack, 0)).toArray(Integer[]::new));

                return bakedModel;
            }

            return GearClientHelper.modelCache.get(key);
        }
    }

    @Nullable
    private static ItemPartData getHighlightPart(@Nullable ItemPartData primary, ItemStack stack) {
        if (primary == null) return null;
        if (primary.getPart().getDisplayProperties(primary, stack, 0).hasHighlight())
            return ItemPartData.instance(ModMaterials.highlight);
        return null;
    }

    public static final class Baked extends AbstractToolModel {

        public static Baked instance;

        public Baked(IModel parent, ImmutableList<BakedQuad> quads, VertexFormat format, ImmutableMap<TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
            super(parent, buildQuadList(quads), format, transforms, cache);
            instance = this;
        }

        @Override
        public ItemOverrideList getOverrides() {
            return OverrideHandler.INSTANCE;
        }

        private static ImmutableList<ImmutableList<BakedQuad>> buildQuadList(ImmutableList<BakedQuad> quads) {
            ImmutableList.Builder<ImmutableList<BakedQuad>> builder = ImmutableList.builder();
            builder.add(quads);
            return builder.build();
        }

        @Override
        public boolean isBuiltInRenderer() {
            return false;
        }
    }
}
