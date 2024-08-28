package net.silentchaos512.gear.util;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.ICoreTool;
import net.silentchaos512.gear.api.part.PartList;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.PartTypes;

import java.util.Objects;
import java.util.Optional;

public final class GearGenerator {
    private GearGenerator() {
        throw new IllegalAccessError("Utility class");
    }

    public static Optional<PartInstance> getRandomPart(GearType gearType, PartType partType) {
        return partType.getCompoundPartItem(gearType)
                .map(GearGenerator::createRandom);
    }

    private static PartInstance createRandom(CompoundPartItem item) {
        var material = SgRegistries.MATERIAL.getRandomObtainable(SilentGear.RANDOM_SOURCE)
                        .orElse(SgRegistries.MATERIAL.get(Const.Materials.EXAMPLE.getId()));
        var stack = item.create(MaterialInstance.of(Objects.requireNonNull(material)));
        return PartInstance.from(stack);
    }

    public static ItemStack create(ICoreItem item) {
        return randomizeParts(new ItemStack(item));
    }

    public static ItemStack randomizeParts(ItemStack stack) {
        if (!(stack.getItem() instanceof ICoreItem item)) {
            throw new RuntimeException("Called GearGenerator.randomizeParts on non-gear");
        }
        GearType gearType = item.getGearType();
        PartList parts = PartList.of();

        for (PartType partType : item.getRequiredParts()) {
            getRandomPart(gearType, partType).ifPresent(parts::add);
        }

        if (parts.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack result = stack.copy();
        GearData.writeConstructionParts(result, parts);

        // Apply some random upgrades?
        if (item instanceof ICoreTool && SilentGear.RANDOM.nextFloat() < 0.3f) {
            getRandomPart(gearType, PartTypes.TIP.get()).ifPresent(part ->
                    GearData.addUpgradePart(result, part));
        }

        GearData.recalculateGearData(result, null);
        parts.forEach(p -> p.onAddToGear(result));

        return result;
    }
}
