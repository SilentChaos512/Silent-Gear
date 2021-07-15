package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.item.ISlingshotAmmo;
import net.silentchaos512.gear.entity.projectile.SlingshotProjectile;
import net.silentchaos512.gear.util.TextUtil;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item.Properties;

public class SlingshotAmmoItem extends ArrowItem implements ISlingshotAmmo {
    public SlingshotAmmoItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrowEntity createArrow(World worldIn, ItemStack stack, LivingEntity shooter) {
        return new SlingshotProjectile(shooter, worldIn);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(TextUtil.translate("item", "slingshot_ammo.desc").withStyle(TextFormatting.ITALIC));
    }
}
