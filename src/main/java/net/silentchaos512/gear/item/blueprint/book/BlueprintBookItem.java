package net.silentchaos512.gear.item.blueprint.book;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.item.IContainerItem;
import net.silentchaos512.gear.item.ICycleItem;
import net.silentchaos512.gear.item.blueprint.AbstractBlueprintItem;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.network.KeyPressOnItemPacket;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

public class BlueprintBookItem extends Item implements IBlueprint, IContainerItem, ICycleItem {
    private static final String NBT_SELECTED = "Selected";

    public BlueprintBookItem(Properties properties) {
        super(properties);
    }

    public static int getSelectedSlot(ItemStack book) {
        return book.getOrCreateTag().getInt(NBT_SELECTED);
    }

    public static void setSelectedSlot(ItemStack book, int slot) {
        book.getOrCreateTag().putInt(NBT_SELECTED, slot);
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
        return 3 * 9;
    }

    @Override
    public boolean canStore(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof AbstractBlueprintItem && !((AbstractBlueprintItem) item).isSingleUse();
    }

    public static void openContainer(ServerPlayerEntity playerIn, ItemStack stack) {
        NetworkHooks.openGui(playerIn,
                new SimpleNamedContainerProvider((id, inv, z) -> new BlueprintBookContainer(id, inv, stack),
                        new TranslationTextComponent("container.silentgear.blueprint_book")),
                buf -> buf.writeItemStack(stack));
    }

    @Override
    public void onCycleKeyPress(ItemStack stack, KeyPressOnItemPacket.Type direction) {
        int current = getSelectedSlot(stack);
        IItemHandler inventory = getInventory(stack);
        for (int i = 1; i <= inventory.getSlots(); ++i) {
            int index = current + (direction == KeyPressOnItemPacket.Type.CYCLE_BACK ? -i : i);
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote) {
            openContainer((ServerPlayerEntity) playerIn, stack);
        }
        return ActionResult.resultSuccess(stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        ItemStack selected = getSelectedItem(stack);
        if (!selected.isEmpty()) {
            tooltip.add(TextUtil.translate("item", "blueprint_book.selected")
                    .applyTextStyle(TextFormatting.AQUA)
                    .appendSibling(selected.getDisplayName()));
        }

        tooltip.add(TextUtil.translate("item", "blueprint_book.keyHint",
                KeyTracker.CYCLE_BACK.getLocalizedName(),
                KeyTracker.CYCLE_NEXT.getLocalizedName())
                .applyTextStyle(TextFormatting.GRAY));
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
