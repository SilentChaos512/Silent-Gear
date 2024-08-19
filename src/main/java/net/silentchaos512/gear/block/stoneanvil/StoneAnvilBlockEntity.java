package net.silentchaos512.gear.block.stoneanvil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
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
        if (item.isEmpty() || this.level == null) return Optional.empty();

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
        if (this.level != null) {
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
        }
        this.markUpdated();
    }

    public boolean takeItem(@Nullable Entity entity) {
        if (!this.item.isEmpty()) {
            dropItem(this.item);
            this.clearContent();
            if (this.level != null) {
                level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
            }
            this.markUpdated();
            return true;
        }
        return false;
    }

    public boolean workOnItem(@Nullable Entity entity, ItemStack tool) {
        var optionalHolder = getRecipe(tool, this.item);
        if (optionalHolder.isPresent() && this.level != null) {
            var recipe = optionalHolder.get().value();
            ItemStack result = recipe.getResultItem(this.level.registryAccess());
            int damage = recipe.getDamageToTool();

            this.dropItem(result.copy());
            this.item.shrink(1);
            var serverPlayer = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
            if (serverPlayer == null || !serverPlayer.getAbilities().instabuild) {
                tool.hurtAndBreak(damage, SilentGear.RANDOM_SOURCE, serverPlayer, () -> {});
            }
            level.playSound(null, getBlockPos(), SoundEvents.STONE_HIT, SoundSource.PLAYERS, 1f, 1f);
            level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
            this.markUpdated();

            return true;
        }

        return false;
    }

    public void dropItem(ItemStack stack) {
        if (this.level != null) {
            Containers.dropItemStack(this.level, getBlockPos().getX(), getBlockPos().getY() + 1.0, getBlockPos().getZ(), stack);
        }
    }

    private void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void clearContent() {
        this.item = ItemStack.EMPTY;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.item = ItemStack.EMPTY;
        if (tag.contains("Item")) {
            this.item = ItemStack.parse(provider, tag.getCompound("Item")).orElse(null);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("Item", this.item.save(provider));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.put("Item", this.item.save(provider));
        return tag;
    }
}
