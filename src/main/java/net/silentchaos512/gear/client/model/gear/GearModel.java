package net.silentchaos512.gear.client.model.gear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
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
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.FakePartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.Color;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GearModel extends LayeredModel<GearModel> {
    private final ItemTransforms cameraTransforms;
    final GearType gearType;
    private final ICoreItem item;
    private GearModelOverrideList overrideList;
    private final String texturePath;
    private final String brokenTexturePath;
    private final Set<PartType> brokenTextureTypes = new HashSet<>();

    GearModel(ItemTransforms cameraTransforms,
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
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        overrideList = new GearModelOverrideList(this, owner, bakery, spriteGetter, modelTransform, modelLocation);
        return new BakedWrapper(this, owner, bakery, spriteGetter, modelTransform, modelLocation, overrideList);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public BakedModel bake(ItemStack stack,
                            List<MaterialLayer> layers,
                            int animationFrame,
                            String transformVariant,
                            IModelConfiguration owner,
                            ModelBakery bakery,
                            Function<Material, TextureAtlasSprite> spriteGetter,
                            ModelState modelTransform,
                            ItemOverrides overrideList,
                            ResourceLocation modelLocation) {
        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        Transformation rotation = modelTransform.getRotation();
        ImmutableMap<ItemTransforms.TransformType, Transformation> transforms = PerspectiveMapWrapper.getTransforms(modelTransform);

        boolean broken = GearHelper.isBroken(stack);

        for (int i = 0; i < layers.size(); i++) {
            MaterialLayer layer = layers.get(i);
            Material renderMaterial = getTexture(layer, animationFrame, broken);
            TextureAtlasSprite texture = spriteGetter.apply(renderMaterial);
            builder.addAll(getQuadsForSprite(i, texture, rotation, layer.getColor()));

            if (GearModelOverrideList.isDebugLoggingEnabled()) {
                SilentGear.LOGGER.info("  - {} -> {}", layer.getTextureId(), renderMaterial.texture());
            }
        }

        // No layers?
        if (layers.isEmpty()) {
            if (Const.Materials.EXAMPLE.isPresent()) {
                buildFakeModel(spriteGetter, builder, rotation, Const.Materials.EXAMPLE.get());
            } else {
                // Shouldn't happen, but...
                SilentGear.LOGGER.error("Example material is missing?");
                TextureAtlasSprite texture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, SilentGear.getId("item/error")));
                builder.addAll(getQuadsForSprite(0, texture, rotation, 0xFFFFFF));
            }
        }

        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

        return new BakedPerspectiveModel(builder.build(), particle, transforms, overrideList, rotation.isIdentity(), owner.isSideLit(), getCameraTransforms(transformVariant));
    }

    private void buildFakeModel(Function<Material, TextureAtlasSprite> spriteGetter, ImmutableList.Builder<BakedQuad> builder, Transformation rotation, IMaterial material) {
        // This method will display an example tool for items with no data (ie, for advancements)
        MaterialInstance mat = MaterialInstance.of(material);
        IMaterialDisplay model = MaterialDisplayManager.get(mat);
        if (!gearType.isArmor()) {
            MaterialLayer exampleRod = model.getLayerList(this.gearType, PartType.ROD, mat).getFirstLayer();
            if (exampleRod != null) {
                builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, exampleRod.getTexture(gearType, 0))), rotation, exampleRod.getColor()));
            }
        }

        MaterialLayer exampleMain = model.getLayerList(this.gearType, PartType.MAIN, mat).getFirstLayer();
        if (exampleMain != null) {
            builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, exampleMain.getTexture(gearType, 0))), rotation, exampleMain.getColor()));
        }

        if (gearType.matches(GearType.RANGED_WEAPON)) {
            MaterialLayer exampleBowstring = model.getLayerList(this.gearType, PartType.BOWSTRING, mat).getFirstLayer();
            if (exampleBowstring != null) {
                builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, exampleBowstring.getTexture(gearType, 0))), rotation, exampleBowstring.getColor()));
            }
        }
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> ret = new HashSet<>();

        ret.add(new Material(InventoryMenu.BLOCK_ATLAS, SilentGear.getId("item/error")));

        // Generic built-in textures
        for (PartTextures tex : PartTextures.getTextures(this.gearType)) {
            int animationFrames = tex.isAnimated() ? item.getAnimationFrames() : 1;
            for (int i = 0; i < animationFrames; ++i) {
                MaterialLayer layer = new MaterialLayer(tex, Color.VALUE_WHITE);
                ret.add(getTexture(layer, i, false));
                ret.add(getTexture(layer, 0, true));
            }
        }

        // Custom textures
        for (IMaterialDisplay materialDisplay : MaterialDisplayManager.getMaterials()) {
            for (PartType partType : PartType.getValues()) {
                if (item.hasTexturesFor(partType)) {
                    for (MaterialLayer layer : materialDisplay.getLayerList(gearType, partType, LazyMaterialInstance.of(materialDisplay.getMaterialId()))) {
                        int animationFrames = layer.isAnimated() ? item.getAnimationFrames() : 1;
                        ret.addAll(this.getTexturesForAllFrames(layer, animationFrames, false));
                        ret.addAll(this.getTexturesForAllFrames(layer, 1, true));
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

        if (GearModelOverrideList.isDebugLoggingEnabled()) {
            SilentGear.LOGGER.info("Textures for gear model '{}' ({})", getTexturePath(false), this.gearType.getName());
            for (Material mat : ret) {
                SilentGear.LOGGER.info("- {}", mat.texture());
            }
        }

        return ret;
    }

    private Collection<Material> getTexturesForAllFrames(MaterialLayer layer, int animationFrameCount, boolean broken) {
        return IntStream.range(0, animationFrameCount)
                .mapToObj(frame -> getTexture(layer, frame, broken))
                .collect(Collectors.toList());
    }

    private Material getTexture(MaterialLayer layer, int animationFrame, boolean broken) {
        ResourceLocation tex = layer.getTextureId();
        String path = "item/" + this.getTexturePath(broken) + "/" + tex.getPath();
        String suffix = animationFrame > 0 && layer.isAnimated() ? "_" + animationFrame : "";
        ResourceLocation location = new ResourceLocation(tex.getNamespace(), path + suffix);
        if (broken && !hasBrokenTexture(layer.getPartType())) {
            return getTexture(layer, animationFrame, false);
        }
        return getMaterial(location);
    }

    private boolean hasBrokenTexture(PartType type) {
        return this.brokenTextureTypes.contains(type);
    }

    private static Material getMaterial(ResourceLocation tex) {
        return new Material(InventoryMenu.BLOCK_ATLAS, tex);
    }

    @Override
    public Collection<? extends IModelGeometryPart> getParts() {
        return Collections.emptyList();
    }

    @Override
    public Optional<? extends IModelGeometryPart> getPart(String name) {
        return Optional.empty();
    }

    private ItemTransforms getCameraTransforms(String transformVariant) {
        return cameraTransforms;
    }
}
