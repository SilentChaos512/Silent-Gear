package net.silentchaos512.gear.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.setup.SgRegistries;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GearItemSet<I extends Item> {
    private final DeferredHolder<GearType, GearType> type;
    private final String partName;

    private DeferredItem<I> gearItem;
    private DeferredItem<MainPartItem> mainPart;
    private DeferredItem<GearBlueprintItem> blueprint;
    private DeferredItem<GearBlueprintItem> template;

    private Supplier<I> gearItemSupplier;
    private Supplier<MainPartItem> mainPartSupplier;
    private Supplier<GearBlueprintItem> blueprintSupplier;
    private Supplier<GearBlueprintItem> templateSupplier;

    public GearItemSet(DeferredHolder<GearType, GearType> type, String partName, Function<Supplier<GearType>, I> gearItem) {
        this(type, partName, () -> gearItem.apply(type::value));
    }

    public GearItemSet(DeferredHolder<GearType, GearType> type, String partName, Supplier<I> gearItem) {
        this(
                type,
                partName,
                gearItem,
                () -> new MainPartItem(type::value, new Item.Properties().stacksTo(1)),
                () -> new GearBlueprintItem(type::value, BlueprintType.BLUEPRINT, new Item.Properties()),
                () -> new GearBlueprintItem(type::value, BlueprintType.TEMPLATE, new Item.Properties())
        );
    }

    public GearItemSet(
            DeferredHolder<GearType, GearType> type,
            String partName,
            Supplier<I> gearItem,
            Supplier<MainPartItem> mainPart,
            Supplier<GearBlueprintItem> blueprint,
            Supplier<GearBlueprintItem> template
    ) {
        this.type = type;
        this.partName = partName;
        this.gearItemSupplier = gearItem;
        this.mainPartSupplier = mainPart;
        this.blueprintSupplier = blueprint;
        this.templateSupplier = template;
    }

    public GearType type() {
        return this.type.value();
    }

    public String name() {
        return this.type.getId().getPath();
    }

    public I gearItem() {
        return this.gearItem.get();
    }

    public MainPartItem mainPart() {
        return this.mainPart.get();
    }

    public GearBlueprintItem blueprint() {
        return this.blueprint.get();
    }

    public GearBlueprintItem template() {
        return this.template.get();
    }

    public void registerGearItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.gearItemSupplier, "gear item");
        this.gearItem = registrar.register(name(), this.gearItemSupplier);
        this.gearItemSupplier = null;
    }

    public void registerMainPartItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.mainPartSupplier, "main part");
        this.mainPart = registrar.register(this.partName, mainPartSupplier);
        this.mainPartSupplier = null;
    }

    public void registerBlueprintItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.blueprintSupplier, "blueprint");
        this.blueprint = registrar.register(name() + "_blueprint", blueprintSupplier);
        this.blueprintSupplier = null;
    }

    public void registerTemplateItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.templateSupplier, "template");
        this.template = registrar.register(name() + "_template", templateSupplier);
        this.templateSupplier = null;
    }

    private void checkNotRegistered(@Nullable Supplier<?> supplier, String itemTypeName) {
        if (supplier == null) {
            var gearTypeName = SgRegistries.GEAR_TYPE.getKey(this.type.get());
            throw new IllegalStateException(itemTypeName + " for " + gearTypeName + " has already been registered!");
        }
    }
}
