package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.PartMaterialIngredient;

public class SgSlotDisplayTypes {
    public static final DeferredRegister<SlotDisplay.Type<?>> SLOT_DISPLAY_TYPES = DeferredRegister.create(Registries.SLOT_DISPLAY, SilentGear.MOD_ID);

    static {
        SLOT_DISPLAY_TYPES.register("part_material", () -> PartMaterialIngredient.PartMaterialSlotDisplay.TYPE);
    }
}
