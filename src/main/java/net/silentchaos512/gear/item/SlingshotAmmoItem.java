package net.silentchaos512.gear.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.util.TextUtil;

import java.util.List;

public class SlingshotAmmoItem extends ArrowItem implements ISlingshotAmmo {
    public SlingshotAmmoItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter) {
        return new SlingshotProjectile(SgEntities.SLINGSHOT_PROJECTILE.get(), shooter, worldIn, stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "slingshot_ammo.desc").withStyle(ChatFormatting.ITALIC));
    }
}
