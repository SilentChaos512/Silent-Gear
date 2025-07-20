package net.silentchaos512.gear.api.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Weapon;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

public interface GearWeapon extends GearTool {
    @Override
    default void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        GearTool.super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            gear.set(DataComponents.WEAPON, new Weapon(1));
        }
    }
}
