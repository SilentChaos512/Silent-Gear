package net.silentchaos512.gear.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.core.BuiltinMaterials;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.blueprint.BlueprintType;
import net.silentchaos512.gear.item.blueprint.GearBlueprintItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GearItemSet<I extends Item & GearItem> {
    private final DeferredHolder<GearType, GearType> type;
    private final String partName;

    private DeferredItem<I> gearItem;
    private DeferredItem<MainPartItem> mainPart;
    private DeferredItem<GearBlueprintItem> blueprint;
    private DeferredItem<GearBlueprintItem> template;

    private Function<Item.Properties, I> gearItemFactory;
    private final Item.Properties gearItemProperties;
    private Function<Item.Properties, MainPartItem> mainPartFactory;
    private Function<Item.Properties, GearBlueprintItem> blueprintFactory;
    private Function<Item.Properties, GearBlueprintItem> templateFactory;

    public GearItemSet(
            DeferredHolder<GearType, GearType> type,
            String partName,
            BiFunction<Supplier<GearType>, Item.Properties, I> gearItem,
            Item.Properties gearItemProperties
    ) {
        this(type, partName, properties -> gearItem.apply(type::value, properties), gearItemProperties);
    }

    public GearItemSet(
            DeferredHolder<GearType, GearType> type,
            String partName,
            Function<Item.Properties, I> gearItem,
            Item.Properties gearItemProperties
    ) {
        this(
                type,
                partName,
                gearItem,
                gearItemProperties,
                properties -> new MainPartItem(type::value, properties),
                properties -> new GearBlueprintItem(type::value, BlueprintType.BLUEPRINT, properties),
                properties -> new GearBlueprintItem(type::value, BlueprintType.TEMPLATE, properties)
        );
    }

    public GearItemSet(
            DeferredHolder<GearType, GearType> type,
            String partName,
            Function<Item.Properties, I> gearItem,
            Item.Properties gearItemProperties,
            Function<Item.Properties, MainPartItem> mainPart,
            Function<Item.Properties, GearBlueprintItem> blueprint,
            Function<Item.Properties, GearBlueprintItem> template
    ) {
        this.type = type;
        this.partName = partName;
        this.gearItemFactory = gearItem;
        this.gearItemProperties = gearItemProperties;
        this.mainPartFactory = mainPart;
        this.blueprintFactory = blueprint;
        this.templateFactory = template;
    }

    public GearType type() {
        return this.type.value();
    }

    public String name() {
        return this.type.getId().getPath();
    }

    public String partName() {
        return this.partName;
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
        checkNotRegistered(this.gearItemFactory, "gear item");
        this.gearItem = registrar.registerItem(name(), this.gearItemFactory, this.gearItemProperties);
        this.gearItemFactory = null;
    }

    public void registerMainPartItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.mainPartFactory, "main part");
        this.mainPart = registrar.registerItem(this.partName, mainPartFactory, new Item.Properties().stacksTo(1).setNoCombineRepair());
        this.mainPartFactory = null;
    }

    public void registerBlueprintItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.blueprintFactory, "blueprint");
        this.blueprint = registrar.registerItem(name() + "_blueprint", blueprintFactory);
        this.blueprintFactory = null;
    }

    public void registerTemplateItem(DeferredRegister.Items registrar) {
        checkNotRegistered(this.templateFactory, "template");
        this.template = registrar.registerItem(name() + "_template", templateFactory);
        this.templateFactory = null;
    }

    private void checkNotRegistered(@Nullable Function<Item.Properties, ?> constructor, String itemTypeName) {
        if (constructor == null) {
            var gearTypeName = SgRegistries.GEAR_TYPE.getKey(this.type.get());
            throw new IllegalStateException(itemTypeName + " for " + gearTypeName + " has already been registered!");
        }
    }

    public ItemStack constructBasicItem(BuiltinMaterials builtinMaterial) {
        GearItem item = gearItem();
        List<PartInstance> parts = new ArrayList<>();
        parts.add(PartInstance.from(mainPart().create(MaterialInstance.of(builtinMaterial.getMaterial()))));
        if (item.requiresPartOfType(PartTypes.ROD.get())) {
            parts.add(PartInstance.from(SgItems.ROD.get().create(MaterialInstance.of(Const.Materials.WOOD))));
        }
        if (item.requiresPartOfType(PartTypes.CORD.get())) {
            parts.add(PartInstance.from(SgItems.CORD.get().create(MaterialInstance.of(Const.Materials.STRING))));
        }
        if (item.requiresPartOfType(PartTypes.BINDING.get())) {
            parts.add(PartInstance.from(SgItems.BINDING.get().create(MaterialInstance.of(Const.Materials.STRING))));
        }
        if (item.requiresPartOfType(PartTypes.SETTING.get())) {
            parts.add(PartInstance.from(SgItems.SETTING.get().create(MaterialInstance.of(Const.Materials.DIAMOND))));
        }
        var result = item.construct(parts);
        result.setCount(1); // Creative tabs don't like stack counts >1
        return result;
    }
}
