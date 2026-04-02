package net.silentchaos512.gear.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.TextureType;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public class GearItemRenderer  extends BlockEntityWithoutLevelRenderer {
    private static final Cache<TextureAtlasSprite, List<BakedQuad>> QUADS_FOR_SPRITES_CACHE = CacheBuilder.newBuilder()
            .maximumSize(128)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public GearItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    private List<BakedQuad> bakeQuadsForSprite(TextureAtlasSprite sprite) {
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite, null);
        return UnbakedGeometryHelper.bakeElements(unbaked, material -> sprite, new SimpleModelState(Transformation.identity()));
    }

    private List<BakedQuad> getQuadsForSprite(TextureAtlasSprite sprite) {
        // Attempt to get quads from cache, if not present bake them
        // An error should never be thrown inside the baking logic but if it is, propagate error
        try {
            return QUADS_FOR_SPRITES_CACHE.get(sprite, () -> bakeQuadsForSprite(sprite));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public ResourceLocation getPartTextureLocation(GearType gearType, PartInstance partInst) {
        var part = partInst.get();
        var partType = part.getType();
        var gearTypeName = GearHelper.gearTypeName(gearType);

        var material = partInst.getPrimaryMaterial();
        if (material == null) throw new IllegalStateException("Part has no material: " + partInst);

        if (partType == PartTypes.MAIN.get()) {
            return ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/main_generic_%s".formatted(gearTypeName, material.getMainTextureType().alias)
            );
        }
        else if (partType == PartTypes.ROD.get()) {
            return ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/rod_generic_%s".formatted(gearTypeName, material.getMainTextureType().alias)
            );
        }
        else if (partType == PartTypes.TIP.get()) {
            return ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/tip_sharp".formatted(gearTypeName)
            );
        }
        else if (partType == PartTypes.GRIP.get()) {
            return ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/grip_wool".formatted(gearTypeName)
            );
        }
        else if (partType == PartTypes.BINDING.get()) {
            return ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/binding_generic".formatted(gearTypeName)
            );
        }
        return ResourceLocation.fromNamespaceAndPath("neoforge", "empty");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof GearItem item))
            throw new IllegalStateException("Attempting to render a non-gear item as gear: " + stack);

        var blockAtlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

        var construction = GearData.getConstruction(stack);

        GearType type = item.getGearType();
        String name = GearHelper.gearTypeName(type);

        poseStack.pushPose();
        var vc = buffer.getBuffer(RenderType.CUTOUT);

        for (var partInst : construction.parts()) {
            var spriteLoc = getPartTextureLocation(type, partInst);
            if (spriteLoc.getPath().equals("empty")) continue;

            SilentGear.LOGGER.info(spriteLoc);
            var sprite = blockAtlas.apply(spriteLoc);

            var packedColor = ColorUtils.getBlendedColorForPartInGear(stack, partInst.getType());
            var red = FastColor.ARGB32.red(packedColor);
            var green = FastColor.ARGB32.green(packedColor);
            var blue = FastColor.ARGB32.blue(packedColor);
            var alpha = FastColor.ARGB32.alpha(packedColor);

            var quads = getQuadsForSprite(sprite);

            for (BakedQuad quad : quads) {
                vc.putBulkData(poseStack.last(), quad, red / 255.0f, green / 255.0f, blue / 255.0f, alpha / 255.0f, packedLight, packedOverlay);
            }
        }
        poseStack.popPose();
    }
}
