package net.silentchaos512.gear.block.stoneanvil;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.silentchaos512.gear.crafting.recipe.ToolActionRecipe;
import net.silentchaos512.gear.setup.SgBlockEntities;
import net.silentchaos512.gear.setup.SgRecipes;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class StoneAnvilBlockEntity extends BlockEntity implements Clearable {
    private static final Logger LOGGER = LogUtils.getLogger();

    private ItemStack item = ItemStack.EMPTY;
    private final RecipeManager.CachedCheck<ToolActionRecipe.Input, ToolActionRecipe> quickCheck =
            RecipeManager.createCheck(SgRecipes.TOOL_ACTION_TYPE.get());

    public StoneAnvilBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(SgBlockEntities.STONE_ANVIL.get(), pPos, pBlockState);
    }

    public ItemStack getItem() {
        return item;
    }

    public Optional<RecipeHolder<ToolActionRecipe>> getRecipe(ItemStack tool, ItemStack item) {
        if (item.isEmpty() || this.level == null || !(this.level instanceof ServerLevel serverLevel)) return Optional.empty();

        return quickCheck.getRecipeFor(new ToolActionRecipe.Input(tool, item), serverLevel);
    }

    public boolean interact(LivingEntity entity, ItemStack stack, InteractionHand hand) {
        if (this.item.isEmpty()) {
            placeItem(entity, stack);
            return true;
        }
        if (workOnItem(entity, stack, hand)) {
            return true;
        }
        return takeItem(entity);
    }

    public void placeItem(LivingEntity entity, ItemStack stack) {
        this.item = stack.split(stack.getCount());
        if (this.level != null) {
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
        }
        this.markUpdated();
    }

    public boolean takeItem(LivingEntity entity) {
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

    public boolean workOnItem(LivingEntity entity, ItemStack tool, InteractionHand hand) {
        if (GearHelper.isGear(tool) && GearHelper.isBroken(tool)) return false;

        var optionalHolder = getRecipe(tool, this.item);
        if (optionalHolder.isPresent() && this.level != null) {
            var recipe = optionalHolder.get().value();
            ItemStack result = recipe.assemble(new ToolActionRecipe.Input(tool, this.item));
            int damage = recipe.getDamageToTool();

            this.dropItem(result.copy());
            this.item.shrink(1);
            var serverPlayer = entity instanceof ServerPlayer ? (ServerPlayer) entity : null;
            if (serverPlayer == null || !serverPlayer.getAbilities().instabuild) {
                tool.hurtAndBreak(damage, entity, hand);
            }
            recipe.getSound().playAt(level, getBlockPos(), SoundSource.PLAYERS);
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
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.item.isEmpty()) {
            output.store("Item", ItemStack.CODEC, this.item);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag;
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(this.problemPath(), LOGGER)) {
            TagValueOutput output = TagValueOutput.createWithContext(scopedCollector, provider);
            if (!this.item.isEmpty()) {
                output.store("Item", ItemStack.CODEC, this.item);
            }
            tag = output.buildResult();
        }
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ValueInput input) {
        super.onDataPacket(net, input);
        this.item = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
