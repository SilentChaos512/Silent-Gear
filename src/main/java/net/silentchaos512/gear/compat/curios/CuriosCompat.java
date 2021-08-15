package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.item.gear.GearElytraItem;
import net.silentchaos512.gear.util.DataResource;
import net.silentchaos512.gear.util.TraitHelper;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public final class CuriosCompat {
    private CuriosCompat() {}

    public static void imcEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", "register_type", () -> SlotTypePreset.BRACELET.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", "register_type", () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
    }

    public static ICapabilityProvider createProvider(ItemStack stack) {
        return createProvider(stack, multimap -> {});
    }

    public static ICapabilityProvider createProvider(ItemStack stack, Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes) {
        return CurioGearItemCapability.createProvider(stack, extraAttributes);
    }

    public static ICapabilityProvider createElytraProvider(ItemStack stack, GearElytraItem item) {
        return CurioGearItemCapability.createElytraProvider(stack, item);
    }

    public static int getHighestTraitLevel(LivingEntity entity, DataResource<ITrait> trait) {
        LazyOptional<IItemHandlerModifiable> lazy = CuriosApi.getCuriosHelper().getEquippedCurios(entity);
        int max = 0;

        if (lazy.isPresent()) {
            IItemHandlerModifiable handler = lazy.orElseThrow(IllegalStateException::new);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() instanceof ICoreItem) {
                    max = Math.max(max, TraitHelper.getTraitLevel(stack, trait));
                }
            }
        }

        return max;
    }

    public static Collection<ItemStack> getEquippedCurios(LivingEntity entity) {
        LazyOptional<IItemHandlerModifiable> lazy = CuriosApi.getCuriosHelper().getEquippedCurios(entity);
        Collection<ItemStack> ret = new ArrayList<>();

        if (lazy.isPresent()) {
            IItemHandlerModifiable handler = lazy.orElseThrow(IllegalStateException::new);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.getItem() instanceof ICoreItem) {
                    ret.add(stack);
                }
            }
        }

        return ret;
    }
}
