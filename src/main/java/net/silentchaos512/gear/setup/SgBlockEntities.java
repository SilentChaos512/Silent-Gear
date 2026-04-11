package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.alloymaker.entity.*;
import net.silentchaos512.gear.block.charger.ChargerBlockEntity;
import net.silentchaos512.gear.block.grader.GraderBlockEntity;
import net.silentchaos512.gear.block.paintmixer.PaintMixerBlockEntity;
import net.silentchaos512.gear.block.press.MetalPressBlockEntity;
import net.silentchaos512.gear.block.salvager.SalvagerBlockEntity;
import net.silentchaos512.gear.block.stoneanvil.StoneAnvilBlockEntity;
import net.silentchaos512.gear.client.renderer.blockentity.StoneAnvilRenderer;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;

import java.util.Arrays;

public final class SgBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraderBlockEntity>> MATERIAL_GRADER = register(
            "material_grader",
            GraderBlockEntity::new,
            SgBlocks.MATERIAL_GRADER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AlloyForgeBlockEntity>> ALLOY_FORGE = register(
            "alloy_forge",
            AlloyForgeBlockEntity::new,
            SgBlocks.ALLOY_FORGE
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalPressBlockEntity>> METAL_PRESS = register(
            "metal_press",
            MetalPressBlockEntity::new,
            SgBlocks.METAL_PRESS
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RecrystallizerBlockEntity>> RECRYSTALLIZER = register(
            "recrystallizer",
            RecrystallizerBlockEntity::new,
            SgBlocks.RECRYSTALLIZER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RefabricatorBlockEntity>> REFABRICATOR = register(
            "refabricator",
            RefabricatorBlockEntity::new,
            SgBlocks.REFABRICATOR
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrudeMixerBlockEntity>> CRUDE_MIXER = register(
            "crude_mixer",
            CrudeMixerBlockEntity::new,
            SgBlocks.CRUDE_MIXER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SuperMixerBlockEntity>> SUPER_MIXER = register(
            "super_mixer",
            SuperMixerBlockEntity::new,
            SgBlocks.SUPER_MIXER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SalvagerBlockEntity>> SALVAGER = register(
            "salvager",
            SalvagerBlockEntity::new,
            SgBlocks.SALVAGER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChargerBlockEntity<StarchargedMaterialModifier>>> STARLIGHT_CHARGER = register(
            "starlight_charger",
            ChargerBlockEntity::createStarlightCharger,
            SgBlocks.STARLIGHT_CHARGER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<StoneAnvilBlockEntity>> STONE_ANVIL = register(
            "stone_anvil",
            StoneAnvilBlockEntity::new,
            SgBlocks.STONE_ANVIL
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PaintMixerBlockEntity>> PAINT_MIXER = register(
            "paint_mixer",
            PaintMixerBlockEntity::new,
            SgBlocks.PAINT_MIXER
    );

    private SgBlockEntities() {
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, DeferredBlock<?>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(DeferredBlock::get).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }

    @OnlyIn(Dist.CLIENT)
    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(STONE_ANVIL.get(), StoneAnvilRenderer::new);
        }
    }
}
