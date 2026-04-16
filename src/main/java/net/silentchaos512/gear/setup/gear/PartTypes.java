package net.silentchaos512.gear.setup.gear;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Optional;
import java.util.function.Consumer;

public class PartTypes {
    public static final DeferredRegister<PartType> REGISTRAR = DeferredRegister.create(SgRegistries.PART_TYPE, SilentGear.MOD_ID);

    public static final DeferredHolder<PartType, PartType> NONE = register("none", builder -> {
    });
    public static final DeferredHolder<PartType, PartType> MAIN = register("main", builder -> builder
            .compoundPartItem(gearType -> {
                // PartType will cache the result on its own
                for (Item item : BuiltInRegistries.ITEM) {
                    if (item instanceof MainPartItem mainPartItem) {
                        var itemGearType = mainPartItem.getGearType();
                        if (itemGearType.matches(gearType)) {
                            SilentGear.LOGGER.debug("Main part item for gear type {} is {}", gearType, mainPartItem);
                            return Optional.of(mainPartItem);
                        }
                    }
                }

                return Optional.empty();
            })
    );
    public static final DeferredHolder<PartType, PartType> ROD = register("rod", builder -> builder
            .compoundPartItem(() -> SgItems.ROD.get())
    );
    public static final DeferredHolder<PartType, PartType> TIP = register("tip", builder -> builder
            .compoundPartItem(() -> SgItems.TIP.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> CORD = register("cord", builder -> builder
            .compoundPartItem(() -> SgItems.CORD.get())
    );
    public static final DeferredHolder<PartType, PartType> FLETCHING = register("fletching", builder -> builder
            .compoundPartItem(() -> SgItems.FLETCHING.get())
    );
    public static final DeferredHolder<PartType, PartType> BINDING = register("binding", builder -> builder
            .compoundPartItem(() -> SgItems.BINDING.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> COATING = register("coating", builder -> builder
            .compoundPartItem(() -> SgItems.COATING.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> GRIP = register("grip", builder -> builder
            .compoundPartItem(() -> SgItems.GRIP.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> LINING = register("lining", builder -> builder
            .compoundPartItem(() -> SgItems.LINING.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> SETTING = register("setting", builder -> builder
            .compoundPartItem(() -> SgItems.SETTING.get())
            .isRemovable(true)
    );
    public static final DeferredHolder<PartType, PartType> MISC_UPGRADE = register("misc_upgrade", builder -> builder
            .isRemovable(true)
            .isUpgrade(true)
            .canPaint(false)
            .maxPerItem(256)
    );

    private static DeferredHolder<PartType, PartType> register(String name, Consumer<PartType.Builder> consumer) {
        PartType.Builder builder = PartType.Builder.builder();
        consumer.accept(builder);
        return REGISTRAR.register(name, () -> new PartType(builder));
    }
}
