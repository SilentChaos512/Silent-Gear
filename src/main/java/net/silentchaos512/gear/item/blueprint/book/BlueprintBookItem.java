package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.init.Registration;
import net.silentchaos512.gear.item.IContainerItem;
import net.silentchaos512.gear.item.ICycleItem;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.network.KeyPressOnItemPacket;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.utils.Color;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlueprintBookItem extends Item implements IBlueprint, IContainerItem, ICycleItem {
    private static final String NBT_SELECTED = "Selected";
    public static final int INVENTORY_SIZE = 6 * 9;

    public BlueprintBookItem(Properties properties) {
        super(properties);
    }

    private static int clampSelectedSlot(int slot) {
        return Mth.clamp(slot, 0, INVENTORY_SIZE - 1);
    }

    public static int getSelectedSlot(ItemStack book) {
        return clampSelectedSlot(book.getOrCreateTag().getInt(NBT_SELECTED));
    }

    public static void setSelectedSlot(ItemStack book, int slot) {
        book.getOrCreateTag().putInt(NBT_SELECTED, clampSelectedSlot(slot));
    }

    private ItemStack getSelectedItem(ItemStack book) {
        return getInventory(book).getStackInSlot(getSelectedSlot(book));
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        ItemStack selected = getSelectedItem(stack);
        if (!selected.isEmpty() && canStore(selected)) {
            return ((AbstractBlueprintItem) selected.getItem()).getPartType(selected);
        }
        return PartType.MISC_UPGRADE;
    }

    @Override
    public GearType getGearType(ItemStack stack) {
        ItemStack selected = getSelectedItem(stack);
        if (!selected.isEmpty() && canStore(selected)) {
            return ((AbstractBlueprintItem) selected.getItem()).getGearType(selected);
        }
        return GearType.NONE;
    }

    @Override
    public int getInventorySize(ItemStack stack) {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean canStore(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof AbstractBlueprintItem && !((AbstractBlueprintItem) item).isSingleUse();
    }

    public static void openContainer(ServerPlayer playerIn, ItemStack stack) {
        NetworkHooks.openGui(playerIn,
                new SimpleMenuProvider((id, inv, z) -> new BlueprintBookContainer(id, inv, stack),
                        new TranslatableComponent("container.silentgear.blueprint_book")),
                buf -> buf.writeItem(stack));
    }

    @Override
    public void onCycleKeyPress(ItemStack stack, KeyPressOnItemPacket.Type direction) {
        int current = getSelectedSlot(stack);
        IItemHandler inventory = getInventory(stack);
        for (int i = 1; i <= inventory.getSlots(); ++i) {
            int index = current + (i * direction.direction);
            if (index < 0) index += inventory.getSlots();
            if (index >= inventory.getSlots()) index -= inventory.getSlots();

            ItemStack inSlot = inventory.getStackInSlot(index);
            if (!inSlot.isEmpty()) {
                setSelectedSlot(stack, index);
                return;
            }
        }

        // No blueprints stored?
        if (getSelectedItem(stack).isEmpty()) {
            setSelectedSlot(stack, 0);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if (!worldIn.isClientSide) {
            openContainer((ServerPlayer) playerIn, stack);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (allowdedIn(group)) {
            super.fillItemCategory(group, items);

            // Create a book with all blueprints
            ItemStack filled = new ItemStack(this);
            IItemHandler inventory = this.getInventory(filled);
            List<Item> blueprints = new ArrayList<>(Registration.getItems(item -> this.canStore(new ItemStack(item))));
            for (int i = 0; i < blueprints.size() && i < getInventorySize(filled); i++) {
                inventory.insertItem(i, new ItemStack(blueprints.get(i)), false);
            }
            this.saveInventory(filled, inventory);
            filled.setHoverName(new TextComponent("Fully-Loaded Blueprint Book"));
            items.add(filled);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        ItemStack selected = getSelectedItem(stack);
        if (!selected.isEmpty()) {
            tooltip.add(TextUtil.withColor(TextUtil.translate("item", "blueprint_book.selected"), Color.SKYBLUE)
                    .append(selected.getHoverName().copy().withStyle(ChatFormatting.GRAY)));
        }

        tooltip.add(TextUtil.translate("item", "blueprint_book.keyHint",
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_BACK), Color.AQUAMARINE),
                TextUtil.withColor(TextUtil.keyBinding(KeyTracker.CYCLE_NEXT), Color.AQUAMARINE)));
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }
}
