package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.silentchaos512.gear.util.GearHelper;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurioGearItemCapability {
    public static void register() {
        CapabilityManager.INSTANCE.register(CurioGearItemWrapper.class, new Capability.IStorage<CurioGearItemWrapper>() {
            @Override
            public INBT writeNBT(Capability<CurioGearItemWrapper> capability, CurioGearItemWrapper instance, Direction side) {
                return new CompoundNBT();
            }

            @Override
            public void readNBT(Capability<CurioGearItemWrapper> capability, CurioGearItemWrapper instance, Direction side, INBT nbt) {
            }
        }, () -> {
            return new CurioGearItemCapability.CurioGearItemWrapper();
        });
    }

    public static ICapabilityProvider createProvider(ItemStack stack) {
        return new CurioGearItemCapability.Provider(new CurioGearItemWrapper(stack));
    }

    public static ICapabilityProvider createProvider(CurioGearItemWrapper curio) {
        return new CurioGearItemCapability.Provider(curio);
    }

    public static class Provider implements ICapabilityProvider {
        final LazyOptional<ICurio> capability;

        Provider(CurioGearItemWrapper curio) {
            this.capability = LazyOptional.of(() -> {
                return curio;
            });
        }

        @Override
        @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return CuriosCapability.ITEM.orEmpty(cap, this.capability);
        }
    }

    private static final class CurioGearItemWrapper implements ICurio {
        private final ItemStack stack;

        private CurioGearItemWrapper() {
            this(ItemStack.EMPTY);
        }

        private CurioGearItemWrapper(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            GearHelper.inventoryTick(stack, livingEntity.world, livingEntity, -1, false);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(String identifier) {
            // FIXME
            return ICurio.super.getAttributeModifiers(identifier);
        }

        @Nonnull
        @Override
        public DropRule getDropRule(LivingEntity livingEntity) {
            return DropRule.ALWAYS_KEEP;
        }
    }
}
