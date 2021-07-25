package net.silentchaos512.gear.client.model.fragment;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.model.IModelConfiguration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.client.model.ModelErrorLogging;
import net.silentchaos512.gear.item.FragmentItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;

public class FragmentModelOverrideList extends ItemOverrides {
    private final Cache<CacheKey, BakedModel> bakedModelCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final FragmentModel model;
    private final IModelConfiguration owner;
    private final ModelBakery bakery;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final ModelState modelTransform;
    private final ResourceLocation modelLocation;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public FragmentModelOverrideList(FragmentModel model,
                                     IModelConfiguration owner,
                                     ModelBakery bakery,
                                     Function<Material, TextureAtlasSprite> spriteGetter,
                                     ModelState modelTransform,
                                     ResourceLocation modelLocation) {
        this.model = model;
        this.owner = owner;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
        this.modelLocation = modelLocation;
    }

    @Nullable
    @Override
    public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn) {
        CacheKey key = getKey(model, stack, worldIn, entityIn);
        try {
            return bakedModelCache.get(key, () -> getOverrideModel(stack, worldIn, entityIn));
        } catch (Exception e) {
            ModelErrorLogging.notifyOfException(e, "fragment");
        }
        return model;
    }

    private BakedModel getOverrideModel(ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn) {
        List<MaterialLayer> layers = new ArrayList<>();

        IMaterialInstance material = FragmentItem.getMaterial(stack);
        if (material != null) {
            IMaterialDisplay model = MaterialDisplayManager.get(material);
            int layerLevel = 0;

            for (MaterialLayer layer : model.getLayerList(GearType.FRAGMENT, PartType.MAIN, material)) {
                int blendedColor = model.getLayerColor(GearType.FRAGMENT, PartType.MAIN, material, layerLevel);
                layers.add(new MaterialLayer(layer.getTextureId(), blendedColor));
                ++layerLevel;
            }
        }

        return model.bake(layers, owner, bakery, spriteGetter, modelTransform, this, modelLocation);
    }

    private static CacheKey getKey(BakedModel model, ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity) {
        return new CacheKey(model, FragmentItem.getModelKey(stack));
    }

    @Override
    public ImmutableList<ItemOverride> getOverrides() {
        return super.getOverrides();
    }

    public void clearCache() {
        SilentGear.LOGGER.debug("Clearing model cache for fragments");
        bakedModelCache.invalidateAll();
    }

    static final class CacheKey {
        final BakedModel parent;
        final String data;

        CacheKey(BakedModel parent, String hash) {
            this.parent = parent;
            this.data = hash;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return parent == cacheKey.parent && Objects.equals(data, cacheKey.data);
        }

        @Override
        public int hashCode() {
            return 31 * parent.hashCode() + data.hashCode();
        }
    }
}
