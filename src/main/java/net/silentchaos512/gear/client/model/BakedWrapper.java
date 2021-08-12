package net.silentchaos512.gear.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IModelConfiguration;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BakedWrapper implements BakedModel {
    private final TextureAtlasSprite particleTexture;
    private final ItemOverrides overrideList;

    @SuppressWarnings("ConstructorWithTooManyParameters")
    public BakedWrapper(LayeredModel<?> model,
                        IModelConfiguration owner,
                        ModelBakery bakery,
                        Function<Material, TextureAtlasSprite> spriteGetter,
                        ModelState modelTransform,
                        ResourceLocation modelLocation,
                        ItemOverrides overrideList) {
        this.particleTexture = spriteGetter.apply(owner.resolveTexture("particle"));
        this.overrideList = overrideList;
    }

    @SuppressWarnings("deprecated")
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @SuppressWarnings("deprecated")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return particleTexture;
    }

    @Override
    public ItemOverrides getOverrides() {
        return overrideList;
    }
}
