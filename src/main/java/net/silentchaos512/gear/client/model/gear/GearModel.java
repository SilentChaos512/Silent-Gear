package net.silentchaos512.gear.client.model.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.registries.ForgeRegistries;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.IPartDisplay;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.client.model.BakedPerspectiveModel;
import net.silentchaos512.gear.client.model.BakedWrapper;
import net.silentchaos512.gear.client.model.LayeredModel;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.part.FakePartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.Color;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearModel extends LayeredModel<GearModel> {
    private final ItemCameraTransforms cameraTransforms;
    final GearType gearType;
    private final ICoreItem item;
    private GearModelOverrideList overrideList;
    private final String texturePath;
    private final String brokenTexturePath;
    private final Set<PartType> brokenTextureTypes = new HashSet<>();

    GearModel(ItemCameraTransforms cameraTransforms,
              GearType gearType,
              String texturePath,
              String brokenTexturePath,
              Collection<PartType> brokenTextureTypes) {
        this.cameraTransforms = cameraTransforms;
        this.gearType = gearType;
        this.texturePath = texturePath;
        this.brokenTexturePath = brokenTexturePath;
        this.brokenTextureTypes.addAll(brokenTextureTypes);
        this.item = ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> item instanceof ICoreItem && ((ICoreItem) item).getGearType() == this.gearType)
                .map(item -> (ICoreItem) item)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("No item for gear type: " + this.gearType.getName()));
    }

    public void clearCache() {
        if (overrideList != null) {
            overrideList.clearCache();
        }
    }

    private String getTexturePath(boolean broken) {
        return broken ? brokenTexturePath : texturePath;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation) {
        overrideList = new GearModelOverrideList(this, owner, bakery, spriteGetter, modelTransform, modelLocation);
        return new BakedWrapper(this, owner, bakery, spriteGetter, modelTransform, modelLocation, overrideList);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public IBakedModel bake(ItemStack stack,
                            List<MaterialLayer> layers,
                            int animationFrame,
                            String transformVariant,
                            IModelConfiguration owner,
                            ModelBakery bakery,
                            Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                            IModelTransform modelTransform,
                            ItemOverrideList overrideList,
                            ResourceLocation modelLocation) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        TransformationMatrix rotation = modelTransform.getRotation();
        ImmutableMap<ItemCameraTransforms.TransformType, TransformationMatrix> transforms = PerspectiveMapWrapper.getTransforms(modelTransform);

        boolean broken = GearHelper.isBroken(stack);

        for (int i = 0; i < layers.size(); i++) {
            MaterialLayer layer = layers.get(i);
            RenderMaterial renderMaterial = getTexture(layer, animationFrame, broken);
            SilentGear.LOGGER.debug("  - {} -> {}", layer.getTextureId(), renderMaterial.getTextureLocation());
            TextureAtlasSprite texture = spriteGetter.apply(renderMaterial);
            builder.addAll(getQuadsForSprite(i, texture, rotation, layer.getColor()));
        }

        // No layers?
        if (layers.isEmpty()) {
            if (Const.Materials.EXAMPLE.isPresent()) {
                buildFakeModel(spriteGetter, builder, rotation, Const.Materials.EXAMPLE.get());
            } else {
                // Shouldn't happen, but...
                SilentGear.LOGGER.error("Example material is missing?");
                TextureAtlasSprite texture = spriteGetter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, SilentGear.getId("item/error")));
                builder.addAll(getQuadsForSprite(0, texture, rotation, 0xFFFFFF));
            }
        }

        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

        return new BakedPerspectiveModel(builder.build(), particle, transforms, overrideList, rotation.isIdentity(), owner.isSideLit(), getCameraTransforms(transformVariant));
    }

    private void buildFakeModel(Function<RenderMaterial, TextureAtlasSprite> spriteGetter, ImmutableList.Builder<BakedQuad> builder, TransformationMatrix rotation, IMaterial material) {
        // This method will display an example tool for items with no data (ie, for advancements)
        IMaterialDisplay model = MaterialDisplayManager.get(material);
        if (!gearType.isArmor()) {
            MaterialLayer exampleRod = model.getLayers(this.gearType, PartType.ROD).getFirstLayer();
            if (exampleRod != null) {
                builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, exampleRod.getTexture(gearType, 0))), rotation, exampleRod.getColor()));
            }
        }

        MaterialLayer exampleMain = model.getLayers(this.gearType, PartType.MAIN).getFirstLayer();
        if (exampleMain != null) {
            builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, exampleMain.getTexture(gearType, 0))), rotation, exampleMain.getColor()));
        }

        if (gearType.matches(GearType.RANGED_WEAPON)) {
            MaterialLayer exampleBowstring = model.getLayers(this.gearType, PartType.BOWSTRING).getFirstLayer();
            if (exampleBowstring != null) {
                builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, exampleBowstring.getTexture(gearType, 0))), rotation, exampleBowstring.getColor()));
            }
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<RenderMaterial> ret = new HashSet<>();

        ret.add(new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, SilentGear.getId("item/error")));

        // Generic built-in textures
        for (PartTextures tex : PartTextures.getTextures(this.gearType)) {
            int animationFrames = tex.isAnimated() ? item.getAnimationFrames() : 1;
            for (int i = 0; i < animationFrames; ++i) {
                MaterialLayer layer = new MaterialLayer(tex, Color.VALUE_WHITE);
                ret.add(getTexture(layer, i, false));
                ret.add(getTexture(layer, i, true));
            }
        }

        // Custom textures
        for (IMaterialDisplay materialDisplay : MaterialDisplayManager.getMaterials()) {
            for (PartType partType : PartType.getValues()) {
                if (item.hasTexturesFor(partType)) {
                    for (MaterialLayer layer : materialDisplay.getLayers(gearType, partType)) {
                        int animationFrames = layer.isAnimated() ? item.getAnimationFrames() : 1;
                        ret.addAll(this.getTexturesForAllFrames(layer, animationFrames, false));
                        ret.addAll(this.getTexturesForAllFrames(layer, animationFrames, true));
                    }
                }
            }
        }
        for (IPartDisplay partDisplay : MaterialDisplayManager.getParts()) {
            for (MaterialLayer layer : partDisplay.getLayers(gearType, FakePartData.of(PartType.NONE))) {
                int animationFrames = layer.isAnimated() ? item.getAnimationFrames() : 1;
                ret.addAll(this.getTexturesForAllFrames(layer, animationFrames, false));
                ret.addAll(this.getTexturesForAllFrames(layer, animationFrames, true));
            }
        }

        SilentGear.LOGGER.info("Textures for gear model '{}' ({})", getTexturePath(false), this.gearType.getName());
        for (RenderMaterial mat : ret) {
            SilentGear.LOGGER.info("- {}", mat.getTextureLocation());
        }

        return ret;
    }

    private Collection<RenderMaterial> getTexturesForAllFrames(MaterialLayer layer, int animationFrameCount, boolean broken) {
        return IntStream.range(0, animationFrameCount)
                .mapToObj(frame -> getTexture(layer, frame, broken))
                .collect(Collectors.toList());
    }

    private RenderMaterial getTexture(MaterialLayer layer, int animationFrame, boolean broken) {
        ResourceLocation tex = layer.getTextureId();
        String path = "item/" + this.getTexturePath(broken) + "/" + tex.getPath();
        String suffix = animationFrame > 0 ? "_" + animationFrame : "";
        ResourceLocation location = new ResourceLocation(tex.getNamespace(), path + suffix);
        if (broken && !hasBrokenTexture(layer.getPartType())) {
            return getTexture(layer, animationFrame, false);
        }
        return getMaterial(location);
    }

    private boolean hasBrokenTexture(PartType type) {
        return this.brokenTextureTypes.contains(type);
    }

    private static RenderMaterial getMaterial(ResourceLocation tex) {
        return new RenderMaterial(PlayerContainer.LOCATION_BLOCKS_TEXTURE, tex);
    }

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
        return Collections.emptyList();
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        return Optional.empty();
    }

    private ItemCameraTransforms getCameraTransforms(String transformVariant) {
        return cameraTransforms;
    }
}
