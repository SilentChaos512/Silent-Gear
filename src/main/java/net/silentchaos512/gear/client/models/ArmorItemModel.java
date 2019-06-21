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
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.parts.IGearPart;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.client.ColorHandlers;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartManager;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ArmorItemModel implements IUnbakedModel {
    private static final IUnbakedModel MODEL = new ArmorItemModel();

    @Nullable
    private final ResourceLocation textureMain;

    public ArmorItemModel() {
        this.textureMain = null;
    }

    public ArmorItemModel(@Nullable ResourceLocation textureMain) {
        this.textureMain = textureMain;
    }

    @Override
    public ArmorItemModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation main = null;

        if (textures.containsKey("main"))
            main = new ResourceLocation(textures.get("main"));

        return new ArmorItemModel(main);
    }

    @Override
    public ArmorItemModel process(ImmutableMap<String, String> customData) {
        ResourceLocation main = null;

        if (customData.containsKey("main"))
            main = new ResourceLocation(customData.get("main"));

        return new ArmorItemModel(main);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        for (ICoreArmor item : ModItems.armorClasses.values()) {
            GearType type = item.getGearType();
            for (IGearPart part : PartManager.getMains()) {
                PartData partData = PartData.of(part);
                // Basic texture
                ResourceLocation textureMain = partData.getTexture(ItemStack.EMPTY, type, PartPositions.ARMOR, 0);
                if (textureMain != null)
                    builder.add(textureMain);

                // Broken texture
                ResourceLocation textureBroken = partData.getBrokenTexture(ItemStack.EMPTY, type, PartPositions.ARMOR);
                if (textureBroken != null)
                    builder.add(textureBroken);
            }
        }
        return builder.build();
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function<ResourceLocation, TextureAtlasSprite> spriteGetter, ISprite sprite, VertexFormat format) {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(ItemCameraTransforms.DEFAULT);

        TRSRTransformation transform = TRSRTransformation.identity();

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();
        if (this.textureMain != null)
            texBuilder.add(this.textureMain);

        ImmutableList<ResourceLocation> textures = texBuilder.build();
        int layerCount = textures.size();
        IBakedModel model = (new ItemLayerModel(textures)).bake(bakery, spriteGetter, sprite, format);
        builder.addAll(model.getQuads(null, null, SilentGear.random));

        return new ArmorItemModel.Baked(this, createQuadsMap(model, layerCount), format, Maps.immutableEnumMap(transformMap), new HashMap<>());
    }

    private ImmutableList<ImmutableList<BakedQuad>> createQuadsMap(IBakedModel model, int layerCount) {
        List<ImmutableList.Builder<BakedQuad>> list = new ArrayList<>();
        for (int i = 0; i < layerCount; ++i)
            list.add(ImmutableList.builder());

        for (BakedQuad quad : model.getQuads(null, null, SilentGear.random))
            list.get(quad.getTintIndex()).add(quad);

        ImmutableList.Builder<ImmutableList<BakedQuad>> builder = ImmutableList.builder();
        for (ImmutableList.Builder<BakedQuad> b : list)
            builder.add(b.build());

        return builder.build();
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    public static final class Loader implements ICustomModelLoader {
        public static Loader INSTANCE = new Loader();

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) { }

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            boolean matchesPath = ModItems.armorClasses.keySet().stream().anyMatch(s -> modelLocation.getPath().equals(s));
            return modelLocation.getNamespace().equals(SilentGear.MOD_ID) && matchesPath;
        }

        @Override
        public IUnbakedModel loadModel(ResourceLocation modelLocation) {
            return MODEL;
        }
    }

    private static final class OverrideHandler extends ItemOverrideList {
        static final OverrideHandler INSTANCE = new OverrideHandler();

        @Nullable
        @Override
        public IBakedModel getModelWithOverrides(IBakedModel parentModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            if (!(stack.getItem() instanceof ICoreArmor)) return parentModel;

            ArmorItemModel.Baked model = (ArmorItemModel.Baked) parentModel;

            ICoreItem itemArmor = (ICoreArmor) stack.getItem();

            String key = GearData.getCachedModelKey(stack, 0);
            stack.getOrCreateTag().putString("debug_modelkey", key);

            if (!GearClientHelper.modelCache.containsKey(key)) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

                processTexture(stack, itemArmor.getGearType(), PartPositions.ARMOR, itemArmor.getPrimaryPart(stack), GearHelper.isBroken(stack), builder);

                IModel parent = model.getParent().retexture(builder.build());
                // TODO: What's this? Do we need to get a model and if so, how?
                Function<ResourceLocation, IUnbakedModel> modelGetter = location -> null;
                Function<ResourceLocation, TextureAtlasSprite> spriteGetter = location ->
                        Minecraft.getInstance().getTextureMap().getAtlasSprite(location.toString());
                /*IBakedModel bakedModel = parent.bake(
                        modelGetter,
                        spriteGetter,
                        new SimpleModelState(model.transforms),
                        false,
                        model.getVertexFormat());
                GearClientHelper.modelCache.put(key, bakedModel);
                return bakedModel;*/
                return null;
            }

            // Color cache
            ColorHandlers.gearColorCache.put(key, new Integer[]{itemArmor.getPrimaryPart(stack).getColor(stack, 0)});

            return GearClientHelper.modelCache.get(key);
        }

        private void processTexture(ItemStack stack, GearType toolClass, IPartPosition position, PartData part, boolean isBroken, ImmutableMap.Builder<String, String> builder) {
            if (part != null) {
                ResourceLocation texture;
                if (isBroken)
                    texture = part.getBrokenTexture(stack, toolClass, position);
                else
                    texture = part.getTexture(stack, toolClass, position, 0);

                if (texture != null)
                    builder.put(position.getModelIndex(), texture.toString());
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static final class Baked extends AbstractToolModel {
        public static Baked instance;

        public Baked(IModel parent, ImmutableList<ImmutableList<BakedQuad>> immutableList, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
            super(parent, immutableList, format, transforms, cache);
            instance = this;
        }

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
