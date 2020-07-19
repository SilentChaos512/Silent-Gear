package net.silentchaos512.gear.client.util;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public final class ModelPropertiesHelper {
    private ModelPropertiesHelper() {}

    @Nullable
    public static IItemPropertyGetter get(ItemStack stack, ResourceLocation id) {
        return ItemModelsProperties.func_239417_a_(stack.getItem(), id);
    }

    public static float getValue(ItemStack stack, ResourceLocation id, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        IItemPropertyGetter prop = get(stack, id);
        if (prop != null) {
            return prop.call(stack, world, entity);
        }
        return 0;
    }
}
