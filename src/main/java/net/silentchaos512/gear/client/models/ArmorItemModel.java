package net.silentchaos512.gear.client.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
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
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class ArmorItemModel implements IModel {

    public static final IModel MODEL = new ArmorItemModel();

    @Nullable
    private final ResourceLocation textureMain;

    public ArmorItemModel() {
        this.textureMain = null;
    }

    public ArmorItemModel(@Nullable ResourceLocation textureMain) {
        this.textureMain = textureMain;
    }

    @Override
    public IModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation main = null;

        if (textures.containsKey("main"))
            main = new ResourceLocation(textures.get("main"));

        return new ArmorItemModel(main);
    }

    @Override
    public IModel process(ImmutableMap<String, String> customData) {
        ResourceLocation main = null;

        if (customData.containsKey("main"))
            main = new ResourceLocation("main");

        return new ArmorItemModel(main);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        for (String armorClass : ModItems.armorClasses.keySet()) {
            for (PartMain part : PartRegistry.getMains()) {
                // Basic texture
                ResourceLocation textureMain = part.getTexture(ItemStack.EMPTY, armorClass, 0, "main");
                if (textureMain != null)
                    builder.add(textureMain);

                // Broken texture
                ResourceLocation textureBroken = part.getBrokenTexture(ItemStack.EMPTY, armorClass);
                if (textureBroken != null)
                    builder.add(textureBroken);
            }
        }
        return builder.build();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

        TRSRTransformation transform = TRSRTransformation.identity();

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();
        if (this.textureMain != null)
            texBuilder.add(this.textureMain);

        ImmutableList<ResourceLocation> textures = texBuilder.build();
        int layerCount = textures.size();
        IBakedModel model = (new ItemLayerModel(textures)).bake(state, format, bakedTextureGetter);
        builder.addAll(model.getQuads(null, null, 0));

        return new ArmorItemModel.Baked(this, createQuadsMap(model, layerCount), format, Maps.immutableEnumMap(transformMap), new HashMap<>());
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
            boolean matchesPath = ModItems.armorClasses.keySet().stream().anyMatch(s -> modelLocation.getResourcePath().equals(s));
            return modelLocation.getResourceDomain().equals(SilentGear.MOD_ID) && matchesPath;
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) {
            return MODEL;
        }
    }

    private static final class OverrideHandler extends ItemOverrideList {

        static final OverrideHandler INSTANCE = new OverrideHandler();

        OverrideHandler() {
            super(ImmutableList.of());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            if (!(stack.getItem() instanceof ICoreArmor))
                return originalModel;

            ArmorItemModel.Baked model = (ArmorItemModel.Baked) originalModel;

            ICoreArmor itemArmor = (ICoreArmor) stack.getItem();
            String armorClass = itemArmor.getGearClass();
            boolean isBroken = GearHelper.isBroken(stack);

            PartMain primaryPart = itemArmor.getPrimaryPart(stack);

            String key = itemArmor.getModelKey(stack, 0, primaryPart);
            StackHelper.getTagCompound(stack, true).setString("debug_modelkey", key);

            if (!GearClientHelper.modelCache.containsKey(key)) {
                ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

                processTexture(stack, armorClass, "main", primaryPart, isBroken, builder);

                IModel parent = model.getParent().retexture(builder.build());
                Function<ResourceLocation, TextureAtlasSprite> textureGetter = location ->
                        Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.getVertexFormat(), textureGetter);
                GearClientHelper.modelCache.put(key, bakedModel);
                return bakedModel;
            }

            return GearClientHelper.modelCache.get(key);
        }

        private void processTexture(ItemStack stack, String toolClass, String partPosition, ItemPart part, boolean isBroken, ImmutableMap.Builder<String, String> builder) {
            if (part != null) {
                ResourceLocation texture;
                if (isBroken)
                    texture = part.getBrokenTexture(stack, toolClass);
                else if (part instanceof PartMain)
                    texture = ((PartMain) part).getTexture(stack, toolClass, 0, partPosition);
                else
                    texture = part.getTexture(stack, toolClass, 0);

                if (texture != null)
                    builder.put(partPosition, texture.toString());
                SilentGear.log.debug(texture);
            }
        }
    }

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
        public boolean isBuiltInRenderer() {
            return true;
        }
    }
}
