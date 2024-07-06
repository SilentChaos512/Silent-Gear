package net.silentchaos512.gear.setup.gear;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PartTypes {
    public static final DeferredRegister<PartType> PART_TYPES = DeferredRegister.create(SgRegistries.PART_TYPES, SilentGear.MOD_ID);

    public static final Supplier<PartType> NONE = register("none", builder -> {
    });
    public static final Supplier<PartType> MAIN = register("main", builder -> builder
            .compoundPartItem(PartType::getToolHeadItem)
    );
    public static final Supplier<PartType> ROD = register("rod", builder -> builder
            .compoundPartItem(() -> SgItems.ROD.get())
    );
    public static final Supplier<PartType> TIP = register("tip", builder -> builder
            .compoundPartItem(() -> SgItems.TIP.get())
    );
    public static final Supplier<PartType> CORD = register("cord", builder -> builder
            .compoundPartItem(() -> SgItems.CORD.get())
    );
    public static final Supplier<PartType> FLETCHING = register("fletching", builder -> builder
            .compoundPartItem(() -> SgItems.FLETCHING.get())
    );
    public static final Supplier<PartType> BINDING = register("binding", builder -> builder
            .compoundPartItem(() -> SgItems.BINDING.get())
            .isRemovable(true)
    );
    public static final Supplier<PartType> COATING = register("coating", builder -> builder
            .compoundPartItem(() -> SgItems.COATING.get())
            .isRemovable(true)
    );
    public static final Supplier<PartType> GRIP = register("grip", builder -> builder
            .compoundPartItem(() -> SgItems.GRIP.get())
            .isRemovable(true)
    );
    public static final Supplier<PartType> LINING = register("lining", builder -> builder
            .compoundPartItem(() -> SgItems.LINING.get())
            .isRemovable(true)
    );
    public static final Supplier<PartType> SETTING = register("setting", builder -> builder
            .compoundPartItem(() -> SgItems.SETTING.get())
            .isRemovable(true)
    );
    public static final Supplier<PartType> MISC_UPGRADE = register("misc_upgrade", builder -> builder
            .isRemovable(true)
            .isUpgrade(true)
            .maxPerItem(256)
    );

    private static Supplier<PartType> register(String name, Consumer<PartType.Builder> consumer) {
        PartType.Builder builder = PartType.Builder.builder();
        consumer.accept(builder);
        return PART_TYPES.register(name, () -> new PartType(builder));
    }
}
