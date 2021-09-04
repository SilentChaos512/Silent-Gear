package net.silentchaos512.gear.crafting.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;

import java.util.*;

interface IGearRecipe {
    ICoreItem getOutputItem();

    default Collection<PartData> getParts(Container inv) {
        List<MaterialInstance> materials = new ArrayList<>();
        IMaterial first = null;
        List<PartData> parts = new ArrayList<>();

        for (int i = 0; i < inv.getContainerSize(); ++i) {
            ItemStack stack = inv.getItem(i);

            if (!stack.isEmpty()) {
                MaterialInstance mat = MaterialInstance.from(stack);

                if (mat != null) {
                    // If classic mixing is disabled, all materials must be the same
                    if (first == null) {
                        first = mat.get();
                    } else if (!Config.Common.allowLegacyMaterialMixing.get() && first != mat.get()) {
                        return Collections.emptyList();
                    }

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
            createToolHead(this.getOutputItem().getGearType(), materials).ifPresent(part ->
                    parts.add(0, part));
        }

        return parts;
    }

    static Optional<PartData> createToolHead(GearType gearType, List<? extends IMaterialInstance> materials) {
        return createCompoundPart(gearType, PartType.MAIN, materials);
    }

    static Optional<PartData> createCompoundPart(GearType gearType, PartType partType, List<? extends IMaterialInstance> materials) {
        return partType.getCompoundPartItem(gearType).map(item -> {
            ItemStack stack = item.create(materials);
            return PartData.from(stack);
        });
    }
}
