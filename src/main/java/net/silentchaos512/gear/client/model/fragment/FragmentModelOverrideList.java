package net.silentchaos512.gear.client.model.fragment;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelConfiguration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.material.MaterialDisplayManager;
import net.silentchaos512.gear.item.FragmentItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class FragmentModelOverrideList extends ItemOverrideList {

    private final Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final FragmentModel model;
    private final IModelConfiguration owner;
    private final ModelBakery bakery;
    private final Function<RenderMaterial, TextureAtlasSprite> spriteGetter;
    private final IModelTransform modelTransform;
    private final ResourceLocation modelLocation;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public FragmentModelOverrideList(FragmentModel model,
                                     IModelConfiguration owner,
                                     ModelBakery bakery,
                                     Function<RenderMaterial, TextureAtlasSprite> spriteGetter,
                                     IModelTransform modelTransform,
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
    public IBakedModel getOverrideModel(IBakedModel model, ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        CacheKey key = getKey(model, stack, worldIn, entityIn);
        try {
            return bakedModelCache.get(key, () -> getOverrideModel(stack, worldIn, entityIn));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return model;
    }

    private IBakedModel getOverrideModel(ItemStack stack, @Nullable ClientWorld worldIn, @Nullable LivingEntity entityIn) {
        List<MaterialLayer> layers = new ArrayList<>();

        IMaterial material = FragmentItem.getMaterial(stack);
        if (material != null) {
            IMaterialDisplay model = MaterialDisplayManager.get(material);
            if (model != null) {
                for (MaterialLayer layer : model.getLayers(GearType.FRAGMENT, PartType.MAIN)) {
                    layers.add(layer);
                }
            }
        }

        return model.bake(layers, owner, bakery, spriteGetter, modelTransform, this, modelLocation);
    }

    private static CacheKey getKey(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
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
        final IBakedModel parent;
        final String data;

        CacheKey(IBakedModel parent, String hash) {
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
