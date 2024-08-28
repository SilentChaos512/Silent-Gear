package net.silentchaos512.gear.gear.part;

import com.google.common.collect.ImmutableList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.NeoForge;
import net.silentchaos512.gear.api.event.GetPropertyModifiersEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.property.GearPropertyMap;
import net.silentchaos512.gear.api.part.PartCraftingData;
import net.silentchaos512.gear.api.part.PartDisplayData;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractGearPart implements GearPart {
    String packName = "UNKNOWN PACK";
    protected final PartCraftingData crafting;
    protected final PartDisplayData display;
    protected final GearPropertyMap properties;

    protected AbstractGearPart(PartCraftingData crafting, PartDisplayData display, GearPropertyMap properties) {
        this.crafting = crafting;
        this.display = display;
        this.properties = properties;
    }

    @Override
    public Ingredient getIngredient() {
        return crafting.craftingItem();
    }

    @Override
    public String getPackName() {
        return packName;
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> getPropertyModifiers(PartInstance instance, PartType partType, PropertyKey<T, V> key) {
        var mods = new ArrayList<>(this.properties.getValues(key));
        var event = new GetPropertyModifiersEvent<>(instance, key, mods);
        NeoForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public boolean isCraftingAllowed(PartInstance part, PartType partType, GearType gearType, @Nullable CraftingInput craftingInput) {
        if (!gearType.matches(GearTypes.ALL.get())) return true;
        for (GearType blacklistedGearType : crafting.gearTypeBlacklist()) {
            if (gearType.matches(blacklistedGearType)) {
                return false;
            }
        }
        return GearPart.super.isCraftingAllowed(part, partType, gearType, craftingInput);
    }

    @Override
    public void onAddToGear(ItemStack gear, PartInstance part) {
        GearPart.super.onAddToGear(gear, part);
        // Transfer durability from main parts
        if (part.getType() == PartTypes.MAIN.get()) {
             gear.setDamageValue(part.getItem().getDamageValue());
        }
    }

    @Override
    public Component getDisplayName(@Nullable PartInstance part) {
        return display.name().copy();
    }

    @Override
    public Component getDisplayName(@Nullable PartInstance part, PartType type) {
        return display.name().copy();
    }

    @Override
    public Component getDisplayNamePrefix(@Nullable PartInstance part, ItemStack gear) {
        return display.namePrefix().copy();
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    @Override
    public void addInformation(PartInstance part, ItemStack gear, List<Component> tooltip, TooltipFlag flag) {}

    /**
     * List of blacklisted {@link GearType}s, mostly used for part tooltips. To know whether of not
     * a part may be used in crafting, use {@link net.silentchaos512.gear.api.util.GearComponent#isCraftingAllowed(Object, PartType, GearType, CraftingInput)} instead.
     *
     * @return The List of GearTypes the part may not be used to craft (could be empty)
     */
    public List<GearType> getBlacklistedGearTypes() {
        return ImmutableList.copyOf(crafting.gearTypeBlacklist());
    }

    @Override
    public String toString() {
        return "AbstractGearPart{" +
                "id=" + SgRegistries.PART.getKey(this) +
                ", partType=" + this.getType() +
                "}";
    }
}
