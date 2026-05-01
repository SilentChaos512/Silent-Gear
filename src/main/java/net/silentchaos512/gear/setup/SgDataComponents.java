package net.silentchaos512.gear.setup;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.core.component.RepairKitCodecs;
import net.silentchaos512.gear.core.component.TraitAddedEnchantments;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SgDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SilentGear.MOD_ID);

    public static final Supplier<DataComponentType<ItemContainerContents>> CONTAINED_ITEMS = REGISTRAR.registerComponentType(
            "contained_items",
            builder -> builder
                    .persistent(ItemContainerContents.CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<Integer>> SELECTED_SLOT = REGISTRAR.registerComponentType(
            "selected_slot",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final Supplier<DataComponentType<GearConstructionData>> GEAR_CONSTRUCTION = REGISTRAR.registerComponentType(
            "construction",
            builder -> builder
                    .persistent(GearConstructionData.CODEC)
                    .networkSynchronized(GearConstructionData.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<GearPropertiesData>> GEAR_PROPERTIES = REGISTRAR.registerComponentType(
            "properties",
            builder -> builder
                    .persistent(GearPropertiesData.CODEC)
                    .networkSynchronized(GearPropertiesData.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<Integer>> PAINT_COLOR = REGISTRAR.registerComponentType(
            "paint_color",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );
    public static final Supplier<DataComponentType<TraitAddedEnchantments>> TRAIT_ENCHANTMENTS = REGISTRAR.registerComponentType(
            "trait_enchantments",
            builder -> builder
                    .persistent(TraitAddedEnchantments.CODEC)
                    .networkSynchronized(TraitAddedEnchantments.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<Boolean>> GEAR_IS_EXAMPLE = REGISTRAR.registerComponentType(
            "is_example",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final Supplier<DataComponentType<Unit>> CRUDE = REGISTRAR.registerComponentType(
            "crude",
            builder -> builder
                    .persistent(Unit.CODEC)
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );
    public static final Supplier<DataComponentType<MaterialGrade>> MATERIAL_GRADE = REGISTRAR.registerComponentType(
            "grade",
            builder -> builder
                    .persistent(MaterialGrade.CODEC)
                    .networkSynchronized(MaterialGrade.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<MaterialInstance>> MATERIAL_SINGLE = REGISTRAR.registerComponentType(
            "material",
            builder -> builder
                    .persistent(MaterialInstance.CODEC)
                    .networkSynchronized(MaterialInstance.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<List<MaterialInstance>>> MATERIAL_LIST = REGISTRAR.registerComponentType(
            "material_list",
            builder -> builder
                    .persistent(Codec.list(MaterialInstance.CODEC))
                    .networkSynchronized(MaterialInstance.STREAM_CODEC.apply(ByteBufCodecs.list()))
    );
    public static final Supplier<DataComponentType<PartType>> PART_TYPE = REGISTRAR.registerComponentType(
            "part_type",
            builder -> builder
                    .persistent(PartType.CODEC)
                    .networkSynchronized(PartType.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<Integer>> STARCHARGED_LEVEL = REGISTRAR.registerComponentType(
            "starcharged_level",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final Supplier<DataComponentType<Byte>> OXIDATION_STAGE = REGISTRAR.registerComponentType(
            "oxidation_stage",
            builder -> builder
                    .persistent(Codec.BYTE)
                    .networkSynchronized(ByteBufCodecs.BYTE)
    );
    public static final Supplier<DataComponentType<Integer>> OXIDATION_COUNTER = REGISTRAR.registerComponentType(
            "oxidation_counter",
            builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );
    public static final Supplier<DataComponentType<Unit>> RECALCULATE_FLAG = REGISTRAR.registerComponentType(
            "recalculate_flag",
            builder -> builder
                    .persistent(Unit.CODEC)
                    .networkSynchronized(Unit.STREAM_CODEC)
    );
    // Use by repair kits
    public static final Supplier<DataComponentType<Map<MaterialInstance, Float>>> MATERIAL_STORAGE = REGISTRAR.registerComponentType(
            "material_storage",
            builder -> builder
                    .persistent(RepairKitCodecs.MATERIAL_STORAGE_CODEC)
                    .networkSynchronized(RepairKitCodecs.MATERIAL_STORAGE_STREAM_CODEC)
    );

    @EventBusSubscriber
    public static class TooltipHandler {
        @SubscribeEvent
        public static void onTooltip(ItemTooltipEvent event) {
            var item = event.getItemStack();
            // Paint and painted gear parts
            if (item.has(PAINT_COLOR)) {
                int color = item.getOrDefault(PAINT_COLOR, 0) & 0xFFFFFF;
                var text = Component.translatable("item.silentgear.paint.color", Color.format(color));
                event.getToolTip().add(TextUtil.withColor(text, color | 0xFF000000));
            }
        }
    }
}
