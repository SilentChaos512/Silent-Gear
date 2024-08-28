package net.silentchaos512.gear.compat.curios;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class CurioGearItemCapability {
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                CuriosCapability.ITEM,
                (stack, context) -> new GearCurio(stack, multimap -> {}),
                GearItemSets.BRACELET.gearItem(),
                GearItemSets.RING.gearItem(),
                GearItemSets.NECKLACE.gearItem()
        );
        event.registerItem(
                CuriosCapability.ITEM,
                (stack, context) -> new GearCurio(stack, builder -> {
                    // Add armor, flight, and trait-related attributes
                    GearItemSets.ELYTRA.gearItem().addAttributes(stack, builder, false);
                }) {
                    @Override
                    public void curioTick(SlotContext context) {
                        if (context.entity().level().isClientSide || !ElytraItem.isFlyEnabled(stack)) {
                            return;
                        }
                        int ticksFlying = context.entity().getFallFlyingTicks();

                        if ((ticksFlying + 1) % 20 == 0) {
                            stack.hurtAndBreak(1, context.entity(), EquipmentSlot.BODY);
                        }
                    }
                },
                GearItemSets.ELYTRA.gearItem()
        );
    }

    public static class GearCurio implements ICurio {
        private final ItemStack stack;
        private final Consumer<ItemAttributeModifiers.Builder> extraAttributes;

        private GearCurio() {
            this(ItemStack.EMPTY, multimap -> {});
        }

        private GearCurio(ItemStack stack, Consumer<ItemAttributeModifiers.Builder> extraAttributes) {
            this.stack = stack;
            this.extraAttributes = extraAttributes;
        }

        @Override
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public void curioTick(SlotContext slotContext) {
            GearHelper.inventoryTick(stack, slotContext.entity().level(), slotContext.entity(), -1, true);
        }

        @Override
        public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(SlotContext slotContext, ResourceLocation id) {
            var builder = ItemAttributeModifiers.builder();
            GearHelper.addAttributeModifiers(stack, builder);
            ItemAttributeModifiers itemAttributeModifiers = builder.build();

            Multimap<Holder<Attribute>, AttributeModifier> result = ArrayListMultimap.create();
            itemAttributeModifiers.modifiers().forEach(entry -> result.put(entry.attribute(), entry.modifier()));
            return result;
        }

        @Override
        public boolean makesPiglinsNeutral(SlotContext slotContext) {
            return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
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
            return new SoundInfo(SoundEvents.ARMOR_EQUIP_GOLD.value(), 1.0f, 1.0f);
        }
    }
}
