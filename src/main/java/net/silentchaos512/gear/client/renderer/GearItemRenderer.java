package net.silentchaos512.gear.client.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemProperties;
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
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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

    public List<ResourceLocation> getPartTextureLocations (
            GearType gearType,
            PartInstance partInst,
            GearConstructionData construction
    ) {
        var part = partInst.get();
        var partType = part.getType();
        var gearTypeName = GearHelper.gearTypeName(gearType);

        var material = partInst.getPrimaryMaterial();
        if (material == null) {
            if (partInst.getId().equals(ResourceLocation.fromNamespaceAndPath(SilentGear.MOD_ID, "spoon_upgrade"))) {
                return List.of(
                        ResourceLocation.fromNamespaceAndPath(
                                SilentGear.MOD_ID,
                                "item/%s/spoon".formatted(gearTypeName)
                        )
                );
            }
        }

        if (partType == PartTypes.MAIN.get()) {
            var mainPart = construction.getCoatingOrMainPart();
            if (mainPart == null)
                throw new IllegalStateException("Gear appears to have no main part: " + construction);
            if (mainPart.getPrimaryMaterial() == null)
                throw new IllegalStateException("Main part appears to have no material: " + mainPart);
            if (mainPart.getPrimaryMaterial().getMainTextureType() == TextureType.HIGH_CONTRAST) {
                return List.of(
                        ResourceLocation.fromNamespaceAndPath(
                                SilentGear.MOD_ID,
                                "item/%s/main_generic_hc".formatted(gearTypeName)
                        ),
                        ResourceLocation.fromNamespaceAndPath(
                                SilentGear.MOD_ID,
                                "item/%s/_highlight".formatted(gearTypeName)
                        )
                );
            }
            else {
                return List.of(
                        ResourceLocation.fromNamespaceAndPath(
                                SilentGear.MOD_ID,
                                "item/%s/main_generic_%s".formatted(
                                        gearTypeName,
                                        mainPart.getPrimaryMaterial().getMainTextureType().alias
                                )
                        )
                );
            }
        }
        else if (partType == PartTypes.ROD.get()) {
            var textureType = material != null ? material.getMainTextureType() : TextureType.LOW_CONTRAST;
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/rod_generic_%s".formatted(gearTypeName, textureType.alias)
            ));
        }
        else if (partType == PartTypes.TIP.get()) {
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/tip_sharp".formatted(gearTypeName)
            ));
        }
        else if (partType == PartTypes.GRIP.get()) {
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/grip_wool".formatted(gearTypeName)
            ));
        }
        else if (partType == PartTypes.BINDING.get()) {
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/binding_generic".formatted(gearTypeName)
            ));
        }
        else if (partType == PartTypes.FLETCHING.get()) {
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/fletching_generic".formatted(gearTypeName)
            ));
        }
        else if (partType == PartTypes.SETTING.get()) {
            return List.of(
                    ResourceLocation.fromNamespaceAndPath(
                        SilentGear.MOD_ID,
                        "item/%s/adornment_generic".formatted(gearTypeName)
                    ),
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/%s/adornment_highlight".formatted(gearTypeName)
                    )
            );
        }
        else if (partType == PartTypes.CORD.get()) {
            return List.of(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/%s/bowstring_string".formatted(gearTypeName)
            ));
        }
        return List.of();
    }

    public void renderColoredSprite(
            TextureAtlasSprite sprite,
            VertexConsumer vc,
            PoseStack poseStack,
            int packedLight,
            int packedOverlay,
            ItemDisplayContext displayContext,
            int packedColor
    ) {
        var red = FastColor.ARGB32.red(packedColor);
        var green = FastColor.ARGB32.green(packedColor);
        var blue = FastColor.ARGB32.blue(packedColor);
        // Alpha is discarded and forced to full for cutout rendering
        // Additionally prevents parts without materials from not rendering

        var quads = getQuadsForSprite(sprite);

        for (BakedQuad quad : quads) {
            vc.putBulkData(poseStack.last(), quad, red / 255.0f, green / 255.0f, blue / 255.0f, 1.0f, packedLight, packedOverlay);
        }
        expandIfNotGui(displayContext, poseStack);
    }

    public void renderUncoloredSprite(
            TextureAtlasSprite sprite,
            VertexConsumer vc,
            PoseStack poseStack,
            int packedLight,
            int packedOverlay,
            ItemDisplayContext displayContext
    ) {
        var quads = getQuadsForSprite(sprite);

        for (BakedQuad quad : quads) {
            vc.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
        }
        expandIfNotGui(displayContext, poseStack);
    }

    public void rodLineRendering(
            GearConstructionData construction,
            VertexConsumer vc,
            ItemStack stack,
            Function<ResourceLocation, TextureAtlasSprite> blockAtlas,
            PoseStack poseStack,
            ItemDisplayContext displayContext,
            int packedLight,
            int packedOverlay
    ) {
        var mc = Minecraft.getInstance();

        // Return w/o rendering if cast
        var cast = ItemProperties.getProperty(stack, ResourceLocation.withDefaultNamespace("cast"));
        if (cast != null && cast.call(stack, mc.level, mc.player, 0) == 1.0) return;

        int bobberColor =
                (GearData.hasPartOfType(stack, PartTypes.COATING.get()))
                        ? GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.COATING.get())
                        : GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());

        int lineColor = GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.CORD.get());

        var bobberSprite = construction.getPrimaryPart() != null && construction.getPrimaryPart().getPrimaryMaterial() != null
                ? blockAtlas.apply(ResourceLocation.fromNamespaceAndPath(
                        SilentGear.MOD_ID,
                        "item/fishing_rod/bobber_%s".formatted(construction.getPrimaryPart().getPrimaryMaterial().getMainTextureType().alias)
                ))
                : blockAtlas.apply(ResourceLocation.fromNamespaceAndPath(
                    SilentGear.MOD_ID,
                    "item/fishing_rod/bobber_lc"
                ));

        var lineSprite = blockAtlas.apply(ResourceLocation.fromNamespaceAndPath(
                SilentGear.MOD_ID,
                "item/fishing_rod/line"
        ));

        renderColoredSprite(lineSprite, vc, poseStack, packedLight, packedOverlay, displayContext, lineColor);
        renderColoredSprite(bobberSprite, vc, poseStack, packedLight, packedOverlay, displayContext, bobberColor);
    }

    public void expandIfNotGui(ItemDisplayContext displayContext, PoseStack poseStack) {
        if (displayContext != ItemDisplayContext.GUI) {
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.scale(1.001f, 1.001f, 1.001f);
            poseStack.translate(-0.5f, -0.5f, -0.5f);
        }
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof GearItem item))
            throw new IllegalStateException("Attempting to render a non-gear item as gear: " + stack);

        var blockAtlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

        var construction = GearData.getConstruction(stack);

        GearType type = item.getGearType();

        poseStack.pushPose();
        var vc = buffer.getBuffer(RenderType.CUTOUT);

        //TODO: Remove need for forcing main rendering - example items don't contain main by default in JEI/Creative
        if (!GearData.hasPartOfType(stack, PartTypes.MAIN.get())) {
            var mainSprite = blockAtlas.apply(
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/%s/main_generic_lc".formatted(GearHelper.gearTypeName(type))
                    )
            );

            renderUncoloredSprite(mainSprite, vc, poseStack, packedLight, packedOverlay, displayContext);
        }

        //TODO: Remove need for forcing rod rendering - example items don't contain rods by default in JEI/Creative
        if (!type.isArmor() && !type.matches(GearTypes.CURIO.get()) && !GearData.hasPartOfType(stack, PartTypes.ROD.get())) {
            var mainSprite = blockAtlas.apply(
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/%s/rod_generic_lc".formatted(GearHelper.gearTypeName(type))
                    )
            );

            renderUncoloredSprite(mainSprite, vc, poseStack, packedLight, packedOverlay, displayContext);
        }

        //TODO: Remove need for forcing fletching rendering - arrows in creative inventory don't have fletching
        if (type == GearTypes.ARROW.get() && !GearData.hasPartOfType(stack, PartTypes.FLETCHING.get())) {
            var mainSprite = blockAtlas.apply(
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/arrow/fletching_generic"
                    )
            );

            renderUncoloredSprite(mainSprite, vc, poseStack, packedLight, packedOverlay, displayContext);
        }

        //TODO: Remove need for forcing fletching rendering - example items don't contain adornments by default
        if (type.matches(GearTypes.CURIO.get()) && !GearData.hasPartOfType(stack, PartTypes.SETTING.get())) {
            var mainSprite = blockAtlas.apply(
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/%s/adornment_generic".formatted(GearHelper.gearTypeName(type))
                    )
            );
            var highlightSprite = blockAtlas.apply(
                    ResourceLocation.fromNamespaceAndPath(
                            SilentGear.MOD_ID,
                            "item/%s/adornment_highlight".formatted(GearHelper.gearTypeName(type))
                    )
            );

            renderUncoloredSprite(mainSprite, vc, poseStack, packedLight, packedOverlay, displayContext);
            renderUncoloredSprite(highlightSprite, vc, poseStack, packedLight, packedOverlay, displayContext);
        }

        for (var partInst : construction.parts()) {
            var spriteLocations = getPartTextureLocations(type, partInst, construction);

            for (var spriteLocation : spriteLocations) {
                var sprite = blockAtlas.apply(spriteLocation);

                var packedColor =
                        (partInst.getType() == PartTypes.MAIN.get() && GearData.hasPartOfType(stack, PartTypes.COATING.get()))
                            ? GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.COATING.get())
                            : GearColorUtils.getBlendedColorForPartInGear(stack, partInst.getType());

                renderColoredSprite(sprite, vc, poseStack, packedLight, packedOverlay, displayContext, packedColor);
            }
        }

        if (type.matches(GearTypes.FISHING_ROD.get()))
            rodLineRendering(construction, vc, stack, blockAtlas, poseStack, displayContext, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
