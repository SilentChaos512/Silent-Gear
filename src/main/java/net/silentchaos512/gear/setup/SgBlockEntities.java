package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.block.charger.ChargerBlockEntity;
import net.silentchaos512.gear.block.compounder.CompoundMakerBlockEntity;
import net.silentchaos512.gear.block.grader.GraderBlockEntity;
import net.silentchaos512.gear.block.press.MetalPressBlockEntity;
import net.silentchaos512.gear.block.salvager.SalvagerBlockEntity;
import net.silentchaos512.gear.crafting.recipe.alloy.FabricAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.GemAlloyRecipe;
import net.silentchaos512.gear.crafting.recipe.alloy.MetalAlloyRecipe;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;
import net.silentchaos512.gear.util.Const;

import java.util.Arrays;

public final class SgBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GraderBlockEntity>> MATERIAL_GRADER = register(
            "material_grader",
            GraderBlockEntity::new,
            SgBlocks.MATERIAL_GRADER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompoundMakerBlockEntity<MetalAlloyRecipe>>> ALLOY_FORGE = register(
            "alloy_forge",
            (pos, state) -> new CompoundMakerBlockEntity<>(Const.METAL_COMPOUNDER_INFO, pos, state),
            SgBlocks.ALLOY_FORGE
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MetalPressBlockEntity>> METAL_PRESS = register(
            "metal_press",
            MetalPressBlockEntity::new,
            SgBlocks.METAL_PRESS
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompoundMakerBlockEntity<GemAlloyRecipe>>> RECRYSTALLIZER = register(
            "recrystallizer",
            (pos, state) -> new CompoundMakerBlockEntity<>(Const.GEM_COMPOUNDER_INFO, pos, state),
            SgBlocks.RECRYSTALLIZER
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CompoundMakerBlockEntity<FabricAlloyRecipe>>> REFABRICATOR = register(
            "refabricator",
            (pos, state) -> new CompoundMakerBlockEntity<>(Const.FABRIC_COMPOUNDER_INFO, pos, state),
            SgBlocks.REFABRICATOR
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

    private SgBlockEntities() {
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderers(FMLClientSetupEvent event) {
    }

    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> factory, DeferredBlock<?>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> {
            Block[] validBlocks = Arrays.stream(blocks).map(DeferredBlock::get).toArray(Block[]::new);
            //noinspection ConstantConditions - null in build
            return BlockEntityType.Builder.of(factory, validBlocks).build(null);
        });
    }
}
