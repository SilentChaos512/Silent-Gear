package net.silentchaos512.gear.block.charger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.api.util.ChargedProperties;
import net.silentchaos512.gear.block.INamedContainerExtraData;
import net.silentchaos512.gear.block.SgContainerBlockEntity;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.modifier.ChargedMaterialModifier;
import net.silentchaos512.gear.gear.material.modifier.StarchargedMaterialModifier;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgBlocks;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.SgTags;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.MaterialModifiers;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.MathUtils;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.lib.util.TimeUtils;

import java.util.ArrayList;

public class ChargerBlockEntity<T extends ChargedMaterialModifier> extends SgContainerBlockEntity implements INamedContainerExtraData {
    static final int INVENTORY_SIZE = 3;
    private static final int UPDATE_FREQUENCY = TimeUtils.ticksFromSeconds(15);

    private final ChargedMaterialModifier.Type<T> modifierType;

    private int progress = 0;
    private int workTime = 100;
    private int charge = 0;
    private int structureLevel;
    private int updateTimer = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass")
    private final ContainerData fields = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> workTime;
                case 2 -> structureLevel;
                case 3 -> charge & 0xFFFF; // Charge lower bytes
                case 4 -> (charge >> 16) & 0xFFFF; // Charge upper bytes
                case 5 -> getMaxCharge() & 0xFFFF; // Max charge lower bytes
                case 6 -> (getMaxCharge() >> 16) & 0xFFFF; // Max charge upper bytes
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    progress = value;
                    break;
                case 1:
                    workTime = value;
                    break;
                case 2:
                    structureLevel = value;
                    break;
            }
        }

        @Override
        public int getCount() {
            return 7;
        }
    };

    public ChargerBlockEntity(BlockEntityType<?> type, ChargedMaterialModifier.Type<T> modifierType, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.modifierType = modifierType;
    }

    public static ChargerBlockEntity<StarchargedMaterialModifier> createStarlightCharger(BlockPos pos, BlockState state) {
        return new ChargerBlockEntity<>(SgBlockEntities.STARLIGHT_CHARGER.get(), MaterialModifiers.STARCHARGED.get(), pos, state);
    }

    protected int getMaxCharge() {
        return Config.Common.starlightChargerMaxCharge.get();
    }

    @Override
    public void encodeExtraData(FriendlyByteBuf buffer) {
        buffer.writeByte(this.fields.getCount());
    }

    protected int getWorkTime(ItemStack input) {
        MaterialInstance material = MaterialInstance.from(input);
        if (material != null) {
            float rarity = material.getProperty(PartTypes.MAIN.get(), GearProperties.RARITY.get());
            float clampedRarity = Mth.clamp(rarity, 5, 500);
            return (int) (5 * clampedRarity);
        }
        var data = input.get(SgDataComponents.MATERIAL_LIST);
        if (data != null) {
            for (MaterialInstance materialInstance : data) {
                var materialChargeMod = materialInstance.getModifier(MaterialModifiers.STARCHARGED.get());
                if (materialChargeMod == null) {
                    float rarity = materialInstance.getProperty(PartTypes.MAIN.get(), GearProperties.RARITY.get());
                    float clampedRarity = Mth.clamp(rarity, 5, 500);
                    return (int) (5 * clampedRarity);
                }
            }
        }
        return -1;
    }

    protected int getDrainRate(ItemStack input, int level) {
        return 150 + 50 * level * level;
    }

    protected int getChargingAgentTier(ItemStack catalyst) {
        return getStarlightChargerCatalystTier(catalyst);
    }

    public static int getStarlightChargerCatalystTier(ItemStack catalyst) {
        for (int i = SgTags.Items.STARLIGHT_CHARGER_TIERS.size() - 1; i >= 0; --i) {
            if (catalyst.is(SgTags.Items.STARLIGHT_CHARGER_TIERS.get(i))) {
                return i + 1;
            }
        }

        return 0;
    }

    protected int getMaterialChargeLevel(ItemStack stack) {
        return modifierType.checkLevel(stack);
    }

    public static boolean canCharge(ItemStack stack) {
        if (canChargePartItem(stack)) return true;

        var material = MaterialInstance.from(stack);
        if (material == null) return false;

        for (IMaterialModifier modifier : material.getModifiers()) {
            if (modifier instanceof ChargedMaterialModifier) {
                return false;
            }
        }

        return true;
    }

    private static boolean canChargePartItem(ItemStack stack) {
        if (!(Config.Common.starlightChargerCanChargeParts.get() || ModList.get().isLoaded("sgearmetalworks"))) {
            return false;
        }

        var data = stack.get(SgDataComponents.MATERIAL_LIST);
        if (data != null) {
            for (MaterialInstance materialInstance : data) {
                var materialChargeMod = materialInstance.getModifier(MaterialModifiers.STARCHARGED.get());
                if (materialChargeMod == null) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void chargeMaterial(ItemStack output, int level) {
        if (chargePartItem(output, level)) return;

        T mod = modifierType.create(level);
        modifierType.addModifier(mod, output);
    }

    protected boolean chargePartItem(ItemStack output, int level) {
        T mod = modifierType.create(level);

        var data = output.get(SgDataComponents.MATERIAL_LIST);
        if (data != null) {
            data = new ArrayList<>(data); // turn mutable
            for (int i = 0; i < data.size(); i++) {
                MaterialInstance materialInstance = data.get(i);
                var materialChargeMod = materialInstance.getModifier(MaterialModifiers.STARCHARGED.get());
                if (materialChargeMod == null) {
                    // charge material
                    data.remove(i);
                    var materialStack = materialInstance.getItem();
                    modifierType.addModifier(mod, materialStack);
                    data.add(MaterialInstance.of(materialInstance.get(), materialStack));
                    break;
                }
            }
            output.set(SgDataComponents.MATERIAL_LIST, data);
            return true;
        }
        return false;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChargerBlockEntity<?> blockEntity) {
        blockEntity.gatherEnergy();

        if (++blockEntity.updateTimer > UPDATE_FREQUENCY) {
            if (blockEntity.checkStructureLevel()) {
                SilentGear.LOGGER.info("{}} at {}: structure level updated to {}",
                        NameUtils.fromBlock(blockEntity.getBlockState()), blockEntity.worldPosition, blockEntity.structureLevel);
            }
            blockEntity.updateTimer = 0;
            //sendUpdate();
        }

        ItemStack input = blockEntity.getItem(0);
        ItemStack catalyst = blockEntity.getItem(1);
        if (input.isEmpty() || catalyst.isEmpty()) {
            return;
        }

        int currentLevel = blockEntity.getMaterialChargeLevel(input);

        if (currentLevel < blockEntity.structureLevel) {
            blockEntity.handleCharging(input, catalyst);
        } else if (blockEntity.progress > 0) {
            blockEntity.progress = 0;
            blockEntity.workTime = 100;
        }
    }

    protected void gatherEnergy() {
        assert level != null;
        if (charge < getMaxCharge() && level.isNight() && level.canSeeSkyFromBelowWater(worldPosition.above())) {
            // Charge up, but watch for overflows since the config allows any value for charge rate and max charge.
            final int newCharge = charge + Config.Common.starlightChargerChargeRate.get();
            if (newCharge < 0 || newCharge > getMaxCharge()) {
                charge = getMaxCharge();
            } else {
                charge = newCharge;
            }
        }
    }

    private void handleCharging(ItemStack input, ItemStack catalyst) {
        assert level != null;
        int chargeLevel = getChargingAgentTier(catalyst);
        int drainRate = getDrainRate(input, chargeLevel);

        if (canCharge(input) && chargeLevel > getMaterialChargeLevel(input) && chargeLevel <= this.structureLevel && this.charge >= drainRate) {
            ItemStack output = input.copy();
            output.setCount(1);
            chargeMaterial(output, chargeLevel);

            if (wouldFitInOutputSlot(output, chargeLevel)) {
                ++this.progress;
                this.charge -= drainRate;
                this.workTime = getWorkTime(input);

                if (this.progress >= this.workTime) {
                    if (getItem(2).isEmpty()) {
                        setItem(2, output);
                    } else {
                        getItem(2).grow(1);
                    }

                    this.progress = 0;
                    removeItem(0, 1);
                    removeItem(1, 1);
                }
            }

            // sendUpdate();
        }
    }

    private boolean wouldFitInOutputSlot(ItemStack input, int chargeTier) {
        ItemStack output = getItem(2);
        if (output.isEmpty()) {
            return true;
        }

        return output.getCount() < output.getMaxStackSize()
                && ItemStack.isSameItemSameComponents(input, output);
    }

    protected boolean checkStructureLevel() {
        int oldValue = this.structureLevel;
        this.structureLevel = MathUtils.min(
                this.getPillarLevel(this.worldPosition.relative(Direction.NORTH, 3).relative(Direction.WEST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.NORTH, 3).relative(Direction.EAST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.SOUTH, 3).relative(Direction.WEST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.SOUTH, 3).relative(Direction.EAST, 3)));
        return this.structureLevel != oldValue;
    }

    protected int getPillarLevel(BlockPos pos) {
        assert level != null;
        BlockState state = this.level.getBlockState(pos.above(2));
        if (state.getBlock() == SgBlocks.CRIMSON_STEEL_BLOCK.get()) return 1;
        if (state.getBlock() == SgBlocks.AZURE_ELECTRUM_BLOCK.get()) return 2;
        if (state.getBlock() == SgBlocks.TYRIAN_STEEL_BLOCK.get()) return 3;

        return 0;
    }

    @Override
    protected Component getDefaultName() {
        return TextUtil.translate("container", "material_charger");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory player) {
        return ChargerContainerMenu.createStarlightCharger(id, player, this, fields);
    }

    @Override
    public ItemStackHandler createItemHandler() {
        return new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return switch (slot) {
                    case 0 -> canCharge(stack);
                    case 1 -> stack.is(SgTags.Items.STARLIGHT_CHARGER_CATALYSTS);
                    default -> false;
                };
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot != 2) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };
    }

    @Override
    public void setChanged() {
        this.progress = 0;
        super.setChanged();
    }

    @Override
    protected void loadAdditional(CompoundTag tags, HolderLookup.Provider provider) {
        super.loadAdditional(tags, provider);
        this.progress = tags.getInt("Progress");
        this.workTime = tags.getInt("WorkTime");
        this.charge = tags.getInt("Charge");
        this.structureLevel = tags.getInt("StructureLevel");
    }

    @Override
    public void saveAdditional(CompoundTag tags, HolderLookup.Provider provider) {
        super.saveAdditional(tags, provider);
        tags.putInt("Progress", this.progress);
        tags.putInt("WorkTime", this.workTime);
        tags.putInt("Charge", this.charge);
        tags.putInt("StructureLevel", this.structureLevel);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tags = super.getUpdateTag(provider);
        tags.putInt("Progress", this.progress);
        tags.putInt("WorkTime", this.workTime);
        tags.putInt("Charge", this.charge);
        tags.putInt("StructureLevel", this.structureLevel);
        return tags;
    }
}
