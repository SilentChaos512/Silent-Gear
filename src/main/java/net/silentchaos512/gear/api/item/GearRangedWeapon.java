package net.silentchaos512.gear.api.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import java.util.Collection;
import java.util.function.Supplier;

public interface GearRangedWeapon extends GearTool {
    Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.CORD.get()
    ));

    @Override
    default Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    default float getBaseDrawDelay(ItemStack stack) {
        return 20;
    }

    default float getDrawDelay(ItemStack stack) {
        float speed = GearData.getProperties(stack).getNumber(GearProperties.DRAW_SPEED);
        if (speed <= 0) speed = 1f;
        return getBaseDrawDelay(stack) / speed;
    }

    default float getArrowDamage(ItemStack ammo) {
        if (GearHelper.isGear(ammo)) {
            return GearData.getProperties(ammo).getNumber(GearProperties.RANGED_DAMAGE);
        }
        return 1f;
    }
}
