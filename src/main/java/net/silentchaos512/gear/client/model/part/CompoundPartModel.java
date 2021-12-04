package net.silentchaos512.gear.client.model.part;

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
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.StaticLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.PartGearKey;
import net.silentchaos512.gear.client.material.GearDisplayManager;
import net.silentchaos512.gear.client.model.BakedPerspectiveModel;
import net.silentchaos512.gear.client.model.BakedWrapper;
import net.silentchaos512.gear.client.model.LayeredModel;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.gear.gear.material.LazyMaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.ModResourceLocation;
import net.silentchaos512.utils.Color;

import java.util.*;
import java.util.function.Function;

public class CompoundPartModel extends LayeredModel<CompoundPartModel> {
    private static final ModResourceLocation PART_MARKER_TEXTURE = SilentGear.getId("part_marker");

    private final ItemTransforms cameraTransforms;
    final GearType gearType;
    final PartType partType;
    final String texturePath;
    private final List<ResourceLocation> extraLayers;
    private CompoundPartModelOverrideList overrideList;

    CompoundPartModel(ItemTransforms cameraTransforms, GearType gearType, PartType partType, String texturePath, List<ResourceLocation> extraLayers) {
        this.cameraTransforms = cameraTransforms;
        this.gearType = gearType;
        this.partType = partType;
        this.texturePath = texturePath;
        this.extraLayers = Collections.unmodifiableList(extraLayers);
    }

    public void clearCache() {
        if (overrideList != null) {
            overrideList.clearCache();
        }
    }

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        overrideList = new CompoundPartModelOverrideList(this, owner, bakery, spriteGetter, modelTransform, modelLocation);
        return new BakedWrapper(this, owner, bakery, spriteGetter, modelTransform, modelLocation, overrideList);
    }

    @SuppressWarnings("MethodWithTooManyParameters")
    public BakedModel bake(List<MaterialLayer> layers,
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

        for (int i = 0; i < layers.size(); i++) {
            MaterialLayer layer = layers.get(i);
            TextureAtlasSprite texture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, layer.getTexture(this.texturePath, 0)));
            builder.addAll(getQuadsForSprite(i, texture, rotation, layer.getColor()));
        }

        // No layers?
        if (layers.isEmpty()) {
            if (Const.Materials.EXAMPLE.isPresent()) {
                buildFakeModel(spriteGetter, builder, rotation, Const.Materials.EXAMPLE.get());
            } else {
                // Shouldn't happen, but...
                SilentGear.LOGGER.error("Example material is missing?");
                TextureAtlasSprite texture = spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, SilentGear.getId("item/error")));
                builder.addAll(getQuadsForSprite(0, texture, rotation, Color.VALUE_WHITE));
            }
        }

        // Extras
        for (int i = 0; i < this.extraLayers.size(); i++) {
            ResourceLocation texture = this.extraLayers.get(i);
            builder.addAll(getQuadsForSprite(layers.size() + i,
                    spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, new StaticLayer(texture).getTexture())),
                    rotation,
                    Color.VALUE_WHITE));
        }

        builder.addAll(getQuadsForSprite(layers.size(),
                spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, new StaticLayer(PART_MARKER_TEXTURE).getTexture())),
                rotation,
                Color.LIGHTSKYBLUE.getColor()));

        TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

        return new BakedPerspectiveModel(builder.build(), particle, transforms, overrideList, rotation.isIdentity(), owner.isSideLit(), getCameraTransforms(transformVariant));
    }

    private void buildFakeModel(Function<Material, TextureAtlasSprite> spriteGetter, ImmutableList.Builder<BakedQuad> builder, Transformation rotation, IMaterial material) {
        // This method will display an example item for items with no data (ie, for advancements)
        MaterialInstance mat = MaterialInstance.of(material);
        IMaterialDisplay model = mat.getDisplayProperties();
        MaterialLayer exampleMain = model.getLayerList(this.gearType, this.partType, mat).getFirstLayer();
        if (exampleMain != null) {
            builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, exampleMain.getTexture(this.texturePath, 0))), rotation, exampleMain.getColor()));
        }
        builder.addAll(getQuadsForSprite(0, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, new StaticLayer(PART_MARKER_TEXTURE).getTexture(this.gearType, 0))), rotation, Color.VALUE_WHITE));
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        Set<Material> ret = new HashSet<>();
        if (this.gearType == GearType.SHIELD) {
            // Unobtainable part items, no need for textures
            return ret;
        }

        // Generic built-in textures
        for (PartTextures tex : PartTextures.getTextures(this.gearType)) {
            ret.add(getTexture(tex.getTexture()));
        }

        // Custom textures
        for (IMaterialDisplay materialDisplay : GearDisplayManager.getMaterials()) {
            for (MaterialLayer layer : materialDisplay.getLayerList(this.gearType, this.partType, LazyMaterialInstance.of(materialDisplay.getMaterialId()))) {
                ret.add(getTexture(layer));
            }
        }

        for (ResourceLocation texture : this.extraLayers) {
            ret.add(getTexture(new StaticLayer(texture)));
        }

        ret.add(getTexture(new StaticLayer(PART_MARKER_TEXTURE)));
        ret.add(new Material(InventoryMenu.BLOCK_ATLAS, SilentGear.getId("item/error")));

        if (CompoundPartModelOverrideList.isDebugLoggingEnabled()) {
            SilentGear.LOGGER.info("Textures for compound part model '{}'", PartGearKey.of(this.gearType, this.partType));
            for (Material mat : ret) {
                SilentGear.LOGGER.info("- {}", mat.texture());
            }
        }

        return ret;
    }

    private Material getTexture(MaterialLayer layer) {
        return getMaterial(layer.getTexture(this.texturePath, 0));
    }

    private Material getTexture(ResourceLocation tex) {
        String path = "item/" + this.texturePath + "/" + tex.getPath();
        ResourceLocation location = new ResourceLocation(tex.getNamespace(), path);
        return getMaterial(location);
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
