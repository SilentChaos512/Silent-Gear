package net.silentchaos512.gear.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelConfiguration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.type.CompoundPart;
import net.silentchaos512.gear.util.GearData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GearModelOverrideList extends ItemOverrideList {
    private final Cache<CacheKey, IBakedModel> bakedModelCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    private final GearModel model;
    private final IModelConfiguration owner;
    private final ModelBakery bakery;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private final IModelTransform modelTransform;
    private final ResourceLocation modelLocation;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public GearModelOverrideList(GearModel model,
                                 IModelConfiguration owner,
                                 ModelBakery bakery,
                                 Function<Material, TextureAtlasSprite> spriteGetter,
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
    public IBakedModel getModelWithOverrides(IBakedModel model, ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn) {
        int animationFrame = getAnimationFrame(stack, worldIn, entityIn);
        CacheKey key = getKey(model, stack, entityIn, animationFrame);
        try {
            return bakedModelCache.get(key, () -> getOverrideModel(stack, worldIn, entityIn, animationFrame));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return model;
    }

    private static int getAnimationFrame(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
        return ((ICoreItem) stack.getItem()).getAnimationFrame(stack, world, entity);
    }

    private IBakedModel getOverrideModel(ItemStack stack, @Nullable World worldIn, @Nullable LivingEntity entityIn, int animationFrame) {
        List<MaterialLayer> layers = new ArrayList<>();

        for (PartData part : GearData.getConstructionParts(stack)) {
            // FIXME: What about legacy parts?
            if (part.getPart() instanceof CompoundPart) {
                MaterialInstance mat = CompoundPart.getPrimaryMaterial(part);
                if (mat != null) {
                    for (MaterialLayer layer : mat.getMaterial().getMaterialDisplay(stack, part.getType()).getLayers()) {
                        SilentGear.LOGGER.debug(layer.getTexture(model.gearType, animationFrame));
                        layers.add(layer);
                    }
                }
            }
        }

        return model.bake(layers, animationFrame, "test", owner, bakery, spriteGetter, modelTransform, this, modelLocation);
    }

    private static CacheKey getKey(IBakedModel model, ItemStack stack, @Nullable LivingEntity entity, int animationFrame) {
        return new CacheKey(model, GearData.getModelKey(stack, animationFrame));
    }

    @Override
    public ImmutableList<ItemOverride> getOverrides() {
        return super.getOverrides();
    }

    public void clearCache() {
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
