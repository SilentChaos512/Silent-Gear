package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.*;

interface IGearRecipe {
    GearItem getOutputItem();

    default Collection<PartInstance> getParts(CraftingInput inv) {
        List<MaterialInstance> materials = new ArrayList<>();
        Material first = null;
        List<PartInstance> parts = new ArrayList<>();

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (!stack.isEmpty()) {
                MaterialInstance mat = MaterialInstance.from(stack);

                if (mat != null) {
                    // If classic mixing is disabled, all materials must be the same
                    if (first == null) {
                        first = mat.get();
                    } else if (first != mat.get()) {
                        return Collections.emptyList();
                    }

                    materials.add(mat);
                } else {
                    PartInstance part = PartInstance.from(stack);
                    if (part != null) {
                        parts.add(part);
                    }
                }
            }
        }

        if (!materials.isEmpty()) {
            // Construct a tool head
            createToolHead(this.getOutputItem().getGearType(), materials).ifPresent(parts::addFirst);
        }

        return parts;
    }

    static Optional<PartInstance> createToolHead(GearType gearType, List<MaterialInstance> materials) {
        return createCompoundPart(gearType, PartTypes.MAIN.get(), materials);
    }

    static Optional<PartInstance> createCompoundPart(GearType gearType, PartType partType, List<MaterialInstance> materials) {
        return partType.getCompoundPartItem(gearType).map(item -> {
            ItemStack stack = item.create(materials);
            return PartInstance.from(stack);
        });
    }
}
