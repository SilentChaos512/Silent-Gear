package net.silentchaos512.gear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.ModList;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GearCurioItem extends BasicGearItem {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.SETTING.get()
    ));

    private final Supplier<GearType> gearType;
    private final String slot;

    public GearCurioItem(Supplier<GearType> gearType, String slot, Properties properties) {
        super(properties);
        this.gearType = gearType;
        this.slot = slot;
    }

    public String getSlot() {
        return slot;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        if (!ModList.get().isLoaded(Const.CURIOS)) {
            tooltipAdder.accept(TextUtil.misc("curiosNotInstalled").withStyle(ChatFormatting.RED));
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        // Not the normal behavior for gear items; add attributes only in the curio capability
        return super.getDefaultAttributeModifiers(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }
}
