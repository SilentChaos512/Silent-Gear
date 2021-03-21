package net.silentchaos512.gear.block.charger;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.GearApi;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.part.MaterialGrade;
import net.silentchaos512.gear.block.INamedContainerExtraData;
import net.silentchaos512.gear.init.GearEnchantments;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.init.ModTags;
import net.silentchaos512.gear.init.ModTileEntities;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.tile.LockableSidedInventoryTileEntity;
import net.silentchaos512.lib.tile.SyncVariable;
import net.silentchaos512.lib.util.TimeUtils;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ChargerTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity, INamedContainerExtraData {
    static final int INVENTORY_SIZE = 3;
    private static final int CHARGE_RATE = SilentGear.isDevBuild() ? 100 : 10;
    private static final int UPDATE_FREQUENCY = TimeUtils.ticksFromSeconds(15);

    private final Supplier<Enchantment> enchantment;

    @SyncVariable(name = "Progress")
    private int progress = 0;
    @SyncVariable(name = "WorkTime")
    private int workTime = 100;
    @SyncVariable(name = "Charge")
    private int charge = 0;
    @SyncVariable(name = "StructureLevel")
    private int structureLevel;

    private int updateTimer = 0;

    @SuppressWarnings("OverlyComplexAnonymousInnerClass")
    private final IIntArray fields = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return progress;
                case 1:
                    return workTime;
                case 2:
                    return charge;
                case 3:
                    return structureLevel;
                case 4:
                    return getMaxCharge();
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    progress = value;
                    break;
                case 1:
                    workTime = value;
                case 2:
                    charge = value;
                case 3:
                    structureLevel = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public ChargerTileEntity(TileEntityType<?> tileEntityTypeIn, Supplier<Enchantment> enchantment) {
        super(tileEntityTypeIn, INVENTORY_SIZE);
        this.enchantment = enchantment;
    }

    public static ChargerTileEntity createStarlightCharger() {
        return new ChargerTileEntity(ModTileEntities.STARLIGHT_CHARGER.get(), GearEnchantments.STAR_CHARGED);
    }

    protected int getMaxCharge() {
        return 1_000_000;
    }

    @Override
    public void encodeExtraData(PacketBuffer buffer) {
        buffer.writeByte(this.fields.size());
    }

    protected int getWorkTime(ItemStack input) {
        IMaterialInstance material = GearApi.getMaterial(input);
        if (material != null) {
            return 100 * material.getTier();
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
        for (int i = ModTags.Items.STARLIGHT_CHARGER_TIERS.size() - 1; i >= 0; --i) {
            if (catalyst.getItem().isIn(ModTags.Items.STARLIGHT_CHARGER_TIERS.get(i))) {
                return i + 1;
            }
        }

        return 0;
    }

    protected int getMaterialChargeLevel(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentLevel(this.enchantment.get(), stack);
    }

    protected void chargeMaterial(ItemStack output, int level) {
        output.addEnchantment(this.enchantment.get(), level);
    }

    @Override
    public void tick() {
        if (world == null || world.isRemote) {
            return;
        }

        gatherEnergy();

        if (++updateTimer > UPDATE_FREQUENCY) {
            if (checkStructureLevel()) {
                SilentGear.LOGGER.info("{}} at {}: structure level updated to {}",
                        this.getBlockState().getBlock().getRegistryName(), this.pos, this.structureLevel);
            }
            updateTimer = 0;
            //sendUpdate();
        }

        ItemStack input = getStackInSlot(0);
        ItemStack catalyst = getStackInSlot(1);
        if (input.isEmpty() || catalyst.isEmpty() || !(GearApi.isMaterial(input))) {
            return;
        }

        int currentLevel = getMaterialChargeLevel(input);

        if (currentLevel < this.structureLevel) {
            handleCharging(input, catalyst);
        } else if (progress > 0) {
            progress = 0;
            workTime = 100;
            //sendUpdate();
        }
    }

    protected void gatherEnergy() {
        assert world != null;
        if (charge < getMaxCharge() && world.isNightTime() && world.canBlockSeeSky(pos.up())) {
            charge += CHARGE_RATE;
        }
    }

    private void handleCharging(ItemStack input, ItemStack catalyst) {
        assert world != null;
        int chargeLevel = getChargingAgentTier(catalyst);
        int drainRate = getDrainRate(input, chargeLevel);

        if (chargeLevel > 0 && chargeLevel <= this.structureLevel && this.charge >= drainRate) {
            if (wouldFitInOutputSlot(input, chargeLevel)) {
                ++this.progress;
                this.charge -= drainRate;
                this.workTime = getWorkTime(input);

                if (this.progress >= this.workTime) {
                    if (getStackInSlot(2).isEmpty()) {
                        ItemStack output = input.copy();
                        output.setCount(1);
                        chargeMaterial(output, chargeLevel);
                        setInventorySlotContents(2, output);
                    } else {
                        getStackInSlot(2).grow(1);
                    }

                    this.progress = 0;
                    decrStackSize(0, 1);
                    decrStackSize(1, 1);
                }
            }

            // sendUpdate();
        }
    }

    private boolean wouldFitInOutputSlot(ItemStack input, int chargeTier) {
        ItemStack output = getStackInSlot(2);
        if (output.isEmpty()) {
            return true;
        }

        return output.getCount() < output.getMaxStackSize()
                && input.isItemEqual(output)
                && getMaterialChargeLevel(output) == chargeTier
                && getGrade(input) == getGrade(output);
    }

    private static MaterialGrade getGrade(ItemStack stack) {
        IMaterialInstance material = GearApi.getMaterial(stack);
        if (material != null) {
            return material.getGrade();
        }
        return MaterialGrade.NONE;
    }

    protected boolean checkStructureLevel() {
        int oldValue = this.structureLevel;
        this.structureLevel = MathUtils.min(
                this.getPillarLevel(this.pos.offset(Direction.NORTH, 3).offset(Direction.WEST, 3)),
                this.getPillarLevel(this.pos.offset(Direction.NORTH, 3).offset(Direction.EAST, 3)),
                this.getPillarLevel(this.pos.offset(Direction.SOUTH, 3).offset(Direction.WEST, 3)),
                this.getPillarLevel(this.pos.offset(Direction.SOUTH, 3).offset(Direction.EAST, 3)));
        return this.structureLevel != oldValue;
    }

    protected int getPillarLevel(BlockPos pos) {
        assert world != null;
        BlockState state = this.world.getBlockState(pos.up(2));
        if (state.getBlock() == ModBlocks.CRIMSON_STEEL_BLOCK.get()) return 1;
        if (state.getBlock() == ModBlocks.AZURE_ELECTRUM_BLOCK.get()) return 2;
        if (state.getBlock() == ModBlocks.TYRIAN_STEEL_BLOCK.get()) return 3;

        return 0;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return TextUtil.translate("container", "material_charger");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return ChargerContainer.createStarlightCharger(id, player, this, fields);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[]{0, 1, 2};
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return index > 1; // Not the material or catalyst slots
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == 0) return GearApi.isMaterial(stack);
        if (index == 1) return stack.getItem().isIn(ModTags.Items.STARLIGHT_CHARGER_CATALYSTS);
        return false;
    }

    @Override
    public void read(BlockState stateIn, CompoundNBT tags) {
        super.read(stateIn, tags);
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    @Override
    public CompoundNBT write(CompoundNBT tags) {
        CompoundNBT compoundTag = super.write(tags);
        SyncVariable.Helper.writeSyncVars(this, compoundTag, SyncVariable.Type.WRITE);
        return compoundTag;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tags = super.getUpdateTag();
        SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.PACKET);
        return tags;
    }
}
