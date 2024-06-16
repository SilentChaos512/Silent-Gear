package net.silentchaos512.gear.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.util.DataResource;
import net.silentchaos512.gear.util.TraitHelper;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public final class CuriosCompat {
    private CuriosCompat() {}

    public static int getHighestTraitLevel(LivingEntity entity, DataResource<ITrait> trait) {
        AtomicInteger max = new AtomicInteger();
        CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
            inventory.getCurios().forEach((identifier, slotInventory) -> {
                var stacks = slotInventory.getStacks();
                for (int i = 0; i < stacks.getSlots(); ++i) {
                    var stack = stacks.getStackInSlot(i);
                    if (stack.getItem() instanceof ICoreItem) {
                        max.set(Math.max(max.get(), TraitHelper.getTraitLevel(stack, trait)));
                    }
                }
            });
        });
        return max.get();
    }

    public static Collection<ItemStack> getEquippedCurios(LivingEntity entity) {
        Collection<ItemStack> ret = new ArrayList<>();

        CuriosApi.getCuriosInventory(entity).ifPresent(inventory -> {
            inventory.getCurios().forEach((identifier, slotInventory) -> {
                var stacks = slotInventory.getStacks();
                for (int i = 0; i < stacks.getSlots(); ++i) {
                    var stack = stacks.getStackInSlot(i);
                    if (stack.getItem() instanceof ICoreItem) {
                        ret.add(stack);
                    }
                }
            });
        });

        return ret;
    }

    public static void registerEventHandlers(IEventBus modEventBus) {
        modEventBus.addListener(CurioGearItemCapability::registerCapabilities);
    }
}
