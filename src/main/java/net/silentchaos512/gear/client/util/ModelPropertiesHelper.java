package net.silentchaos512.gear.client.util;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public final class ModelPropertiesHelper {
    private ModelPropertiesHelper() {}

    @Nullable
    public static ItemPropertyFunction get(ItemStack stack, ResourceLocation id) {
        return ItemProperties.getProperty(stack.getItem(), id);
    }

    public static float getValue(ItemStack stack, ResourceLocation id, @Nullable ClientLevel world, @Nullable LivingEntity entity) {
        ItemPropertyFunction prop = get(stack, id);
        if (prop != null) {
            return prop.call(stack, world, entity);
        }
        return 0;
    }
}
