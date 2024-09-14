package net.silentchaos512.gear.client.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.setup.GearItemSets;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

public final class ModItemModelProperties {
    private ModItemModelProperties() {}

    @SuppressWarnings("OverlyComplexMethod")
    public static void register(FMLClientSetupEvent event) {
        var pull = ResourceLocation.withDefaultNamespace("pull");
        register(event, GearItemSets.BOW.gearItem(), pull, (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        var pulling = ResourceLocation.withDefaultNamespace("pulling");
        register(event, GearItemSets.BOW.gearItem(), pulling, (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        register(event, GearItemSets.SLINGSHOT.gearItem(), pull, (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });
        register(event, GearItemSets.SLINGSHOT.gearItem(), pulling, (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        register(event, GearItemSets.CROSSBOW.gearItem(), pull, (stack, level, entity, par4) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return CrossbowItem.isCharged(stack) ? 0.0F : (float) (stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / (float) CrossbowItem.getChargeDuration(stack, entity);
            }
        });
        register(event, GearItemSets.CROSSBOW.gearItem(), pulling, (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        register(event, GearItemSets.CROSSBOW.gearItem(), ResourceLocation.withDefaultNamespace("charged"), (stack, level, entity, par4) -> {
            return entity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
        });
        register(event, GearItemSets.CROSSBOW.gearItem(), ResourceLocation.withDefaultNamespace("firework"), (stack, level, entity, par4) -> {
            var chargedProjectile = stack.get(DataComponents.CHARGED_PROJECTILES);
            return entity != null && chargedProjectile != null && chargedProjectile.contains(Items.FIREWORK_ROCKET) ? 1.0F : 0.0F;
        });

        register(event, GearItemSets.SHIELD.gearItem(), ResourceLocation.withDefaultNamespace("blocking"), (stack, level, entity, par4) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });

        SgItems.getItems(GearItem.class).forEach(item -> {
            register(event, item.asItem(), Const.MODEL, (stack, level, entity, par4) -> {
                return GearData.getModelIndex(stack);
            });
            register(event, item.asItem(), Const.BROKEN_PROPERTY, (stack, level, entity, par4) -> {
                return GearHelper.isBroken(stack) ? 0 : 1;
            });
        });
    }

    private static void register(FMLClientSetupEvent event, Item item, ResourceLocation propertyId, @SuppressWarnings("deprecation") ItemPropertyFunction function) {
        event.enqueueWork(() -> ItemProperties.register(item, propertyId, function));
    }
}
