package net.silentchaos512.gear.setup;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.core.component.GearConstructionData;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.core.component.MaterialGradeData;

import java.util.function.Supplier;

public class SgDataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(SilentGear.MOD_ID);

    public static final Supplier<DataComponentType<GearConstructionData>> GEAR_CONSTRUCTION = REGISTRAR.registerComponentType(
            "construction",
            builder -> builder
                    .persistent(GearConstructionData.CODEC)
                    .networkSynchronized(GearConstructionData.STREAM_CODEC)
    );

    public static final Supplier<DataComponentType<GearPropertiesData>> GEAR_PROPERTIES = REGISTRAR.registerComponentType(
            "properties",
            builder -> builder
                    .networkSynchronized(GearConstructionData.STREAM_CODEC)
    );
    public static final Supplier<DataComponentType<MaterialGrade>> MATERIAL_GRADE = REGISTRAR.registerComponentType(
            "grade",
            builder -> builder
                    .persistent(MaterialGrade.CODEC)
                    .networkSynchronized(MaterialGrade.STREAM_CODEC)
    );
}
