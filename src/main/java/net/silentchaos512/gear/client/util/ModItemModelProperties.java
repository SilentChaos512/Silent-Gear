package net.silentchaos512.gear.client.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Items;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class ModItemModelProperties {

    private ModItemModelProperties() {}

    @SuppressWarnings("OverlyComplexMethod")
    public static void register(FMLClientSetupEvent event) {
        ItemProperties.register(SgItems.BOW.get(), new ResourceLocation("pull"), (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        ItemProperties.register(SgItems.BOW.get(), new ResourceLocation("pulling"), (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        ItemProperties.register(SgItems.SLINGSHOT.get(), new ResourceLocation("pull"), (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        ItemProperties.register(SgItems.SLINGSHOT.get(), new ResourceLocation("pulling"), (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        ItemProperties.register(SgItems.CROSSBOW.get(), new ResourceLocation("pull"), (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return CrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(stack);
            }
        });
        ItemProperties.register(SgItems.CROSSBOW.get(), new ResourceLocation("pulling"), (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        ItemProperties.register(SgItems.CROSSBOW.get(), new ResourceLocation("charged"), (stack, level, entity, par4) -> {
            return entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        ItemProperties.register(SgItems.CROSSBOW.get(), new ResourceLocation("firework"), (stack, level, entity, par4) -> {
            return entity != null && CrossbowItem.isCharged(stack) && CrossbowItem.containsChargedProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });

        ItemProperties.register(SgItems.SHIELD.get(), new ResourceLocation("blocking"), (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        SgItems.getItems(ICoreItem.class).forEach(item -> {
            ItemProperties.register(item.asItem(), Const.MODEL, (stack, level, entity, par4) -> {
                return GearData.getModelIndex(stack);
            });
            ItemProperties.register(item.asItem(), Const.BROKEN_PROPERTY, (stack, level, entity, par4) -> {
                return GearHelper.isBroken(stack) ? 0 : 1;
            });
        });
    }
}
