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
import java.util.Map;
import java.util.function.Supplier;

public class ChargerTileEntity extends LockableSidedInventoryTileEntity implements ITickableTileEntity, INamedContainerExtraData {
    static final int INVENTORY_SIZE = 3;
    private static final int CHARGE_RATE = 30 * (SilentGear.isDevBuild() ? 10 : 1);
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
                    return structureLevel;
                case 3:
                    // Charge lower bytes
                    return charge & 0xFFFF;
                case 4:
                    // Charge upper bytes
                    return (charge >> 16) & 0xFFFF;
                case 5:
                    // Max charge lower bytes
                    return getMaxCharge() & 0xFFFF;
                case 6:
                    // Max charge upper bytes
                    return (getMaxCharge() >> 16) & 0xFFFF;
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
        buffer.writeByte(this.fields.getCount());
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
            if (catalyst.getItem().is(ModTags.Items.STARLIGHT_CHARGER_TIERS.get(i))) {
                return i + 1;
            }
        }

        return 0;
    }

    protected int getMaterialChargeLevel(ItemStack stack) {
        return EnchantmentHelper.getItemEnchantmentLevel(this.enchantment.get(), stack);
    }

    protected void chargeMaterial(ItemStack output, int level) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(output);
        enchantments.put(this.enchantment.get(), level);
        EnchantmentHelper.setEnchantments(enchantments, output);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        gatherEnergy();

        if (++updateTimer > UPDATE_FREQUENCY) {
            if (checkStructureLevel()) {
                SilentGear.LOGGER.info("{}} at {}: structure level updated to {}",
                        this.getBlockState().getBlock().getRegistryName(), this.worldPosition, this.structureLevel);
            }
            updateTimer = 0;
            //sendUpdate();
        }

        ItemStack input = getItem(0);
        ItemStack catalyst = getItem(1);
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
        assert level != null;
        if (charge < getMaxCharge() && level.isNight() && level.canSeeSkyFromBelowWater(worldPosition.above())) {
            charge += CHARGE_RATE;
        }
    }

    private void handleCharging(ItemStack input, ItemStack catalyst) {
        assert level != null;
        int chargeLevel = getChargingAgentTier(catalyst);
        int drainRate = getDrainRate(input, chargeLevel);

        if (chargeLevel > getMaterialChargeLevel(input) && chargeLevel <= this.structureLevel && this.charge >= drainRate) {
            if (wouldFitInOutputSlot(input, chargeLevel)) {
                ++this.progress;
                this.charge -= drainRate;
                this.workTime = getWorkTime(input);

                if (this.progress >= this.workTime) {
                    if (getItem(2).isEmpty()) {
                        ItemStack output = input.copy();
                        output.setCount(1);
                        chargeMaterial(output, chargeLevel);
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
                && input.sameItem(output)
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
                this.getPillarLevel(this.worldPosition.relative(Direction.NORTH, 3).relative(Direction.WEST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.NORTH, 3).relative(Direction.EAST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.SOUTH, 3).relative(Direction.WEST, 3)),
                this.getPillarLevel(this.worldPosition.relative(Direction.SOUTH, 3).relative(Direction.EAST, 3)));
        return this.structureLevel != oldValue;
    }

    protected int getPillarLevel(BlockPos pos) {
        assert level != null;
        BlockState state = this.level.getBlockState(pos.above(2));
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
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        return canPlaceItem(index, itemStackIn);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return index > 1; // Not the material or catalyst slots
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 0) return GearApi.isMaterial(stack);
        if (index == 1) return stack.getItem().is(ModTags.Items.STARLIGHT_CHARGER_CATALYSTS);
        return false;
    }

    @Override
    public void load(BlockState stateIn, CompoundNBT tags) {
        super.load(stateIn, tags);
        SyncVariable.Helper.readSyncVars(this, tags);
    }

    @Override
    public CompoundNBT save(CompoundNBT tags) {
        CompoundNBT compoundTag = super.save(tags);
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
