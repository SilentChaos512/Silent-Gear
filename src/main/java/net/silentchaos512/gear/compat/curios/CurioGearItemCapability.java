package net.silentchaos512.gear.compat.curios;

public class CurioGearItemCapability {
    public static void register() {
        /*CapabilityManager.INSTANCE.register(CurioGearItemWrapper.class, new Capability.IStorage<CurioGearItemWrapper>() {
            @Override
            public Tag writeNBT(Capability<CurioGearItemWrapper> capability, CurioGearItemWrapper instance, Direction side) {
                return new CompoundTag();
            }

            @Override
            public void readNBT(Capability<CurioGearItemWrapper> capability, CurioGearItemWrapper instance, Direction side, Tag nbt) {
            }
        }, CurioGearItemWrapper::new);*/
    }

    /*public static ICapabilityProvider createProvider(ItemStack stack, Consumer<Multimap<Attribute, AttributeModifier>> extraAttributes) {
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
            GearHelper.inventoryTick(stack, livingEntity.level, livingEntity, -1, true);
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
            livingEntity.level.playSound(null, new BlockPos(livingEntity.position()), SoundEvents.ARMOR_EQUIP_GOLD, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }*/
}
