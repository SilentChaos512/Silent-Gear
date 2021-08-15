package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.silentchaos512.gear.item.gear.GearElytraItem;
import net.silentchaos512.gear.util.GearHelper;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Consumer;

public class CurioGearItemCapability {
    public static void register() {
        CapabilityManager.INSTANCE.register(CurioGearItemWrapper.class);
    }

    public static ICapabilityProvider createProvider(ItemStack stack, Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes) {
        return new CurioGearItemCapability.Provider(new CurioGearItemWrapper(stack, extraAttributes));
    }

    public static ICapabilityProvider createElytraProvider(ItemStack stack, GearElytraItem item) {
        //noinspection OverlyComplexAnonymousInnerClass
        return new CurioGearItemCapability.Provider(new CurioGearItemWrapper(stack, multimap -> {
            // Add armor, flight, and trait-related attributes
            item.addAttributes("back", stack, multimap, false);
        }) {
            @Override
            public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                if (livingEntity.level.isClientSide || !ElytraItem.isFlyEnabled(stack)) {
                    return;
                }
                Integer ticksFlying = ObfuscationReflectionHelper.getPrivateValue(LivingEntity.class, livingEntity, "fallFlyTicks");

                if (ticksFlying != null && (ticksFlying + 1) % 20 == 0) {
                    stack.hurtAndBreak(1, livingEntity, entity -> entity.broadcastBreakEvent(EquipmentSlot.CHEST));
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
            this.capability = LazyOptional.of(() -> curio);
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
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public void curioTick(SlotContext slotContext) {
            GearHelper.inventoryTick(stack, slotContext.entity().level, slotContext.entity(), -1, true);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
            Multimap<Attribute, AttributeModifier> multimap = GearHelper.getAttributeModifiers(slotContext.identifier(), stack, HashMultimap.create(), false);
            extraAttributes.accept(multimap);
            return multimap;
        }

        @Override
        public int getFortuneLevel(SlotContext slotContext, @Nullable LootContext lootContext) {
            // TODO: Add trait
            return 0;
        }

        @Override
        public int getLootingLevel(SlotContext slotContext, DamageSource source, LivingEntity target, int baseLooting) {
            // TODO: Add trait
            return 0;
        }

        @Nonnull
        @Override
        public DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit) {
            return DropRule.ALWAYS_KEEP;
        }

        @Override
        public boolean canEquipFromUse(SlotContext slotContext) {
            return true;
        }

        @Nonnull
        @Override
        public SoundInfo getEquipSound(SlotContext slotContext) {
            return new SoundInfo(SoundEvents.ARMOR_EQUIP_GOLD, 1.0f, 1.0f);
        }
    }
}
