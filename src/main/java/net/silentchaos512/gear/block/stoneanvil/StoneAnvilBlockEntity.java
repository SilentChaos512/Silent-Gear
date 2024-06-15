package net.silentchaos512.gear.block.stoneanvil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class StoneAnvilBlockEntity extends BlockEntity implements Clearable {
    private ItemStack item = ItemStack.EMPTY;
    private final RecipeManager.CachedCheck<Container, ToolActionRecipe> quickCheck = RecipeManager.createCheck(SgRecipes.TOOL_ACTION_TYPE.get());

    public StoneAnvilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SgBlockEntities.STONE_ANVIL.get(), pPos, pBlockState);
    }

    public ItemStack getItem() {
        return item;
    }

    public Optional<RecipeHolder<ToolActionRecipe>> getRecipe(ItemStack tool, ItemStack item) {
        if (item.isEmpty()) return Optional.empty();

        return quickCheck.getRecipeFor(new SimpleContainer(tool, item), this.level);
    }

    public boolean interact(@Nullable Entity entity, ItemStack stack) {
        if (this.item.isEmpty()) {
            placeItem(entity, stack);
            return true;
        }
        if (workOnItem(entity, stack)) {
            return true;
        }
        return takeItem(entity);
    }

    public void placeItem(@Nullable Entity entity, ItemStack stack) {
        this.item = stack.split(stack.getCount());
        this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
        this.markUpdated();
    }

    public boolean takeItem(@Nullable Entity entity) {
        if (!this.item.isEmpty()) {
            dropItem(this.item);
            this.clearContent();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, getBlockPos(), GameEvent.Context.of(getBlockState()));
            return true;
        }
        return false;
    }

    public boolean workOnItem(@Nullable Entity entity, ItemStack tool) {
        var optionalHolder = getRecipe(tool, this.item);
        if (optionalHolder.isPresent()) {
            var recipe = optionalHolder.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            int damage = recipe.getDamageToTool();

            this.dropItem(result.copy());
            this.item.shrink(1);
            var serverPlayer = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
            if (serverPlayer == null || !serverPlayer.getAbilities().instabuild) {
                tool.hurt(damage, SilentGear.RANDOM_SOURCE, serverPlayer);
            }
            level.playSound(null, getBlockPos(), SoundEvents.STONE_HIT, SoundSource.PLAYERS, 1f, 1f);

            return true;
        }

        return false;
    }

    public void dropItem(ItemStack stack) {
        Containers.dropItemStack(level, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), stack);
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.item = ItemStack.EMPTY;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.item = ItemStack.EMPTY;
        if (pTag.contains("Item")) {
            this.item = ItemStack.of(pTag.getCompound("Item"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!this.item.isEmpty()) {
            pTag.put("Item", this.item.save(new CompoundTag()));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        if (!this.item.isEmpty()) {
            tag.put("Item", this.item.save(new CompoundTag()));
        }
        return tag;
    }
}
