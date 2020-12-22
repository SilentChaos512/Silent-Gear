package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
            GearHelper.inventoryTick(stack, livingEntity.world, livingEntity, -1, true);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(String identifier) {
            return GearHelper.getAttributeModifiers(identifier, stack, HashMultimap.create(), false);
        }

        @Override
        public int getFortuneBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
            // TODO: Add trait
            return 0;
        }

        @Override
        public int getLootingBonus(String identifier, LivingEntity livingEntity, ItemStack curio, int index) {
            // TODO: Add trait
            return 0;
        }

        @Nonnull
        @Override
        public DropRule getDropRule(LivingEntity livingEntity) {
            return DropRule.ALWAYS_KEEP;
        }

        @Override
        public boolean canRightClickEquip() {
            return true;
        }

        @Override
        public void playRightClickEquipSound(LivingEntity livingEntity) {
            livingEntity.world.playSound(null, new BlockPos(livingEntity.getPositionVec()), SoundEvents.ITEM_ARMOR_EQUIP_GOLD, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
