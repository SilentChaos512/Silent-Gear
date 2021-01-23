package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.silentchaos512.gear.item.gear.CoreElytra;
import net.silentchaos512.gear.util.GearHelper;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

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
        }, CurioGearItemWrapper::new);
    }

    public static ICapabilityProvider createProvider(ItemStack stack, Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes) {
        return new CurioGearItemCapability.Provider(new CurioGearItemWrapper(stack, extraAttributes));
    }

    public static ICapabilityProvider createElytraProvider(ItemStack stack, CoreElytra item) {
        //noinspection OverlyComplexAnonymousInnerClass
        return new CurioGearItemCapability.Provider(new CurioGearItemWrapper(stack, multimap -> {
            // Add armor, flight, and trait-related attributes
            item.addAttributes("back", stack, multimap, false);
        }) {
            @Override
            public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                if (livingEntity.world.isRemote || !ElytraItem.isUsable(stack)) {
                    return;
                }
                Integer ticksFlying = ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, livingEntity, "field_184629_bo");

                if (ticksFlying != null && (ticksFlying + 1) % 20 == 0) {
                    stack.damageItem(1, livingEntity, entity -> entity.sendBreakAnimation(EquipmentSlotType.CHEST));
                }
            }
        });
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

    public static class CurioGearItemWrapper implements ICurio {
        private final ItemStack stack;
        private final Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes;

        private CurioGearItemWrapper() {
            this(ItemStack.EMPTY, multimap -> {});
        }

        private CurioGearItemWrapper(ItemStack stack, Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes) {
            this.stack = stack;
            this.extraAttributes = extraAttributes;
        }

        @Override
        public void curioTick(String identifier, int index, LivingEntity livingEntity) {
            GearHelper.inventoryTick(stack, livingEntity.world, livingEntity, -1, true);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(String identifier) {
            Multimap<Attribute, AttributeModifier> multimap = GearHelper.getAttributeModifiers(identifier, stack, HashMultimap.create(), false);
            extraAttributes.accept(multimap);
            return multimap;
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
