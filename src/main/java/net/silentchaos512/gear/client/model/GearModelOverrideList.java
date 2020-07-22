package net.silentchaos512.gear.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModelConfiguration;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.gear.CoreCrossbow;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.type.CompoundPart;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        CacheKey key = getKey(model, stack, worldIn, entityIn, animationFrame);
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
            if (part.getPart() instanceof CompoundPart) {
                MaterialInstance mat = CompoundPart.getPrimaryMaterial(part);
                if (mat != null) {
                    addWithBlendedColor(layers, part, mat, stack);
                }
            } else {
                // Legacy parts (remove later?)
                layers.addAll(part.getPart().getLiteTexture(part, stack).getLayers(part.getType()).stream()
                        .map(loc -> {
                            int c = loc.equals(SilentGear.getId("_highlight")) ? Color.VALUE_WHITE : part.getColor(stack, animationFrame);
                            PartTextures tex = PartTextures.byTextureId(loc);
                            return tex != null ? tex.getLayer(c) : new MaterialLayer(loc, c);
                        })
                        .collect(Collectors.toList()));
            }
        }

        if (stack.getItem() instanceof CoreCrossbow) {
            getCrossbowCharge(stack, worldIn, entityIn).ifPresent(layers::add);
        }

        return model.bake(layers, animationFrame, "test", owner, bakery, spriteGetter, modelTransform, this, modelLocation);
    }

    private void addWithBlendedColor(List<MaterialLayer> list, PartData part, MaterialInstance material, ItemStack stack) {
        for (MaterialLayer layer : material.getMaterial().getMaterialDisplay(stack, part.getType()).getLayers()) {
            if (layer.getColor() < 0xFFFFFF) {
                list.add(new MaterialLayer(layer.getTextureId(), GearData.getColor(stack, part.getType())));
            } else {
                list.add(layer);
            }
        }
    }

    private static Optional<MaterialLayer> getCrossbowCharge(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
        // TODO: Maybe should add an ICoreItem method to get additional layers?
        IItemPropertyGetter chargedProperty = stack.getItem().getPropertyGetter(new ResourceLocation("charged"));
        IItemPropertyGetter fireworkProperty = stack.getItem().getPropertyGetter(new ResourceLocation("firework"));

        if (chargedProperty != null && fireworkProperty != null) {
            boolean charged = chargedProperty.call(stack, world, entity) > 0;
            boolean firework = fireworkProperty.call(stack, world, entity) > 0;
            if (charged) {
                if (firework) {
                    return Optional.of(new MaterialLayer(PartTextures.CHARGED_FIREWORK, Color.VALUE_WHITE));
                }
                return Optional.of(new MaterialLayer(PartTextures.CHARGED_ARROW, Color.VALUE_WHITE));
            }
        }

        return Optional.empty();
    }

    private static CacheKey getKey(IBakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int animationFrame) {
        String chargeSuffix = getCrossbowCharge(stack, world, entity)
                .map(l -> l.getTextureId().getPath())
                .orElse("");
        return new CacheKey(model, GearData.getModelKey(stack, animationFrame) + chargeSuffix);
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
