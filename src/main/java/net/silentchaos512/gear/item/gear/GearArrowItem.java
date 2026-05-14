package net.silentchaos512.gear.item.gear;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.CommonHooks;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.entity.projectile.GearArrowEntity;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.MathUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class GearArrowItem extends ArrowItem implements GearItem {
    private static final Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.FLETCHING.get()
    ));
    private static final Supplier<Collection<PartType>> SUPPORTED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get(),
            PartTypes.FLETCHING.get(),
            PartTypes.TIP.get(),
            PartTypes.COATING.get()
    ));

    private final Supplier<GearType> gearType;

    public GearArrowItem(Supplier<GearType> gearType) {
        super(new Properties().stacksTo(64));
        this.gearType = gearType;
    }

    @Override
    public GearType getGearType() {
        return this.gearType.get();
    }

    @Override
    public float getRepairModifier(ItemStack stack) {
        // No repairs
        return 0f;
    }

    @Override
    public Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    @Override
    public boolean supportsPart(ItemStack gear, PartInstance part) {
        if (part.getType().equals(PartTypes.ROD.get()) && part.isValid()) {
            // Need a special exception for rods as they only support the TOOL gear type
            return true;
        }
        return GearItem.super.supportsPart(gear, part) && SUPPORTED_PARTS.get().contains(part.getType());
    }

    @Override
    public ItemStack construct(Collection<PartInstance> parts) {
        ItemStack result = GearItem.super.construct(parts);
        float durability = GearData.getProperties(result).getNumber(GearProperties.DURABILITY);
        int stackCount = MathUtils.clamp(Math.round(durability / 32.5f), 1, 64);
        result.setCount(stackCount);
        GearData.recalculateGearData(result, CommonHooks.getCraftingPlayer());
        return result;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        GearArrowEntity arrow = new GearArrowEntity(level, shooter, ammo.copyWithCount(1), weapon);
        arrow.setArrowStack(ammo);
        arrow.setBaseDamage(GearData.getProperties(ammo).getNumber(GearProperties.RANGED_DAMAGE));
        return arrow;
    }
    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
    	GearArrowEntity arrow = new GearArrowEntity(level, pos.x(), pos.y(), pos.z(), stack.copyWithCount(1), null);
        arrow.pickup = AbstractArrow.Pickup.ALLOWED;
        return arrow;
    }
    

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return GearHelper.onItemUse(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, tooltipContext, tooltip, flagIn);
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        var builder = ItemAttributeModifiers.builder();
        GearHelper.addAttributeModifiers(stack, builder, false);
        return builder.build();
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return GearHelper.getEnchantmentValue(stack);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return GearClientHelper.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    @OnlyIn(Dist.CLIENT)
    public ItemColor getItemColors() {
//        return (stack, tintIndex) -> Color.VALUE_WHITE;
        //noinspection OverlyLongLambda
        return (stack, tintIndex) -> {
            return switch (tintIndex) {
                case 0 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.ROD.get());
                case 1 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.MAIN.get());
                case 3 -> GearColorUtils.getBlendedColorForPartInGear(stack, PartTypes.FLETCHING.get());
                default -> 0xFFFFFFFF;
            };
        };
    }
}
