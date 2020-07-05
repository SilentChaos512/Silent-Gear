package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.ToolHeadItem;
import net.silentchaos512.gear.parts.PartData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

interface IGearRecipe {
    ICoreItem getOutputItem();

    default Collection<PartData> getParts(IInventory inv) {
        List<MaterialInstance> materials = new ArrayList<>();
        List<PartData> parts = new ArrayList<>();

        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                MaterialInstance mat = MaterialInstance.from(stack);
                if (mat != null) {
                    materials.add(mat);
                } else {
                    PartData part = PartData.from(stack);
                    if (part != null) {
                        parts.add(part);
                    }
                }
            }
        }

        if (!materials.isEmpty()) {
            // Construct a tool head
            ToolHeadItem toolHeadItem = Registration.getItems(ToolHeadItem.class).stream()
                    .filter(item -> this.getOutputItem().getGearType().matches(item.getGearType()))
                    .findFirst()
                    .orElse(null);

            if (toolHeadItem != null) {
                ItemStack toolHead = toolHeadItem.create(materials);
                PartData toolHeadPart = PartData.from(toolHead);
                if (toolHeadPart != null) {
                    parts.add(0, toolHeadPart);
                }
            }
        }

        return parts;
    }
}
