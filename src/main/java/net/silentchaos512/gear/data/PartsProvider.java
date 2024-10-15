package net.silentchaos512.gear.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.data.part.PartBuilder;
import net.silentchaos512.gear.api.data.part.PartsProviderBase;
import net.silentchaos512.gear.api.data.part.UpgradePartBuilder;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.part.PartCraftingData;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.NumberProperty;
import net.silentchaos512.gear.api.property.TraitListPropertyValue;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.setup.SgItems;
import net.silentchaos512.gear.setup.gear.GearProperties;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PartsProvider extends PartsProviderBase {
    public PartsProvider(DataGenerator generator) {
        super(generator, SilentGear.MOD_ID);
    }

    @Override
    public Collection<PartBuilder> getParts() {
        Collection<PartBuilder> ret = new ArrayList<>();

        ret.add(part("binding", GearTypes.TOOL, PartTypes.BINDING)
                .crafting(SgItems.BINDING)
        );
        ret.add(part("coating", GearTypes.ALL, PartTypes.COATING)
                .crafting(new PartCraftingData(
                        Ingredient.of(SgItems.COATING),
                        ImmutableList.of(GearTypes.ELYTRA.get()),
                        true
                ))
        );
        ret.add(part("cord", GearTypes.RANGED_WEAPON, PartTypes.CORD)
                .crafting(SgItems.CORD)
        );
        ret.add(part("fletching", GearTypes.PROJECTILE, PartTypes.FLETCHING)
                .crafting(SgItems.FLETCHING)
        );
        ret.add(part("grip", GearTypes.TOOL, PartTypes.GRIP)
                .crafting(SgItems.GRIP)
        );
        ret.add(part("lining", GearTypes.ARMOR, PartTypes.LINING)
                .crafting(SgItems.LINING)
        );
        ret.add(part("rod", GearTypes.TOOL, PartTypes.ROD)
                .crafting(SgItems.ROD)
        );
        ret.add(part("setting", GearTypes.CURIO, PartTypes.SETTING)
                .crafting(SgItems.SETTING)
        );
        ret.add(part("tip", GearTypes.TOOL, PartTypes.TIP)
                .crafting(SgItems.TIP)
        );

        SgItems.getItems(MainPartItem.class).forEach(item -> {
            PartBuilder builder = part(NameUtils.fromItem(item).getPath(), item::getGearType, item::getPartType);
            builder.crafting(item);
            ret.add(addMainPartStats(builder));
        });

        ret.add(upgradePart("misc/spoon", CraftingItems.SPOON_UPGRADE)
                .upgradeGearTypes(GearTypes.PICKAXE.get().getMatcher(false))
                .numberProperty(GearProperties.DURABILITY, 0.2f, NumberProperty.Operation.MULTIPLY_BASE)
                .numberProperty(GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
                .property(GearProperties.TRAITS, TraitListPropertyValue.single(Const.Traits.SPOON, 1))
        );
        ret.add(upgradePart("misc/road_maker", CraftingItems.ROAD_MAKER_UPGRADE)
                .upgradeGearTypes(GearTypes.EXCAVATOR.get().getMatcher(false))
                .numberProperty(GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .numberProperty(GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
                .property(GearProperties.TRAITS, TraitListPropertyValue.single(Const.Traits.ROAD_MAKER, 1))
        );
        ret.add(upgradePart("misc/wide_plate", CraftingItems.WIDE_PLATE_UPGRADE)
                .upgradeGearTypes(new GearTypeMatcher(false, GearTypes.HAMMER.get(), GearTypes.EXCAVATOR.get()))
                .numberProperty(GearProperties.DURABILITY, 0.1f, NumberProperty.Operation.MULTIPLY_BASE)
                .numberProperty(GearProperties.RARITY, 10, NumberProperty.Operation.ADD)
                .property(GearProperties.TRAITS, TraitListPropertyValue.single(Const.Traits.WIDEN, 1))
        );
        ret.add(upgradePart("misc/red_card", CraftingItems.RED_CARD_UPGRADE)
                .upgradeGearTypes(GearTypeMatcher.ALL)
                .numberProperty(GearProperties.RARITY, -5, NumberProperty.Operation.ADD)
                .property(GearProperties.TRAITS, TraitListPropertyValue.single(Const.Traits.RED_CARD, 1))
        );

        return ret;
    }

    private static PartBuilder part(String name, Supplier<GearType> gearType, Supplier<PartType> partType) {
        return new PartBuilder(SilentGear.getId(name), gearType, partType)
                .display(Component.translatable("part.silentgear." + name.replace('/', '.')));
    }

    private static UpgradePartBuilder upgradePart(String name, ItemLike item) {
        return (UpgradePartBuilder) new UpgradePartBuilder(SilentGear.getId(name), GearTypes.ALL, PartTypes.MISC_UPGRADE)
                .crafting(item)
                .display(Component.translatable("part.silentgear." + name.replace('/', '.')));
    }

    public static Map<GearType, Consumer<PartBuilder>> MAIN_PART_PROPERTIES = ImmutableMap.<GearType, Consumer<PartBuilder>>builder()
            .put(GearTypes.ARROW.get(), b -> {})
            .put(GearTypes.AXE.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 5, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.BOOTS.get(), b -> b
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.BOW.get(), b -> b
                    .numberProperty(GearProperties.RANGED_DAMAGE, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.DRAW_SPEED, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.45f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.BRACELET.get(), b -> {})
            .put(GearTypes.CHESTPLATE.get(), b -> b
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.CROSSBOW.get(), b -> b
                    .numberProperty(GearProperties.RANGED_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.DRAW_SPEED, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.45f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.DAGGER.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 2.8f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 2.0f)
                    .numberProperty(GearProperties.ATTACK_DAMAGE, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.ELYTRA.get(), b -> b
                    .numberProperty(GearProperties.ARMOR, -0.65f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ARMOR, -3.5f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.EXCAVATOR.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 2.0f)
                    .numberProperty(GearProperties.DURABILITY, 1.0f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.FISHING_ROD.get(), b -> b
                    .numberProperty(GearProperties.DURABILITY, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.25f)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.75f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ATTACK_SPEED, 4f, NumberProperty.Operation.ADD)
            )
            .put(GearTypes.HAMMER.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 4, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 0.8f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.5f)
                    .numberProperty(GearProperties.DURABILITY, 1.0f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.HELMET.get(), b -> b
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.HOE.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, -1f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ATTACK_SPEED, 3f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.KATANA.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 4, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.4f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
                    .numberProperty(GearProperties.DURABILITY, 0.125f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.1f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.KNIFE.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 2.4f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 2.0f)
                    .numberProperty(GearProperties.ATTACK_DAMAGE, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.LEGGINGS.get(), b -> b
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.MACE.get(), b -> b
                    .numberProperty(GearProperties.DURABILITY, 1.0f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 3.0f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 0.6f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .put(GearTypes.MACHETE.get(), b -> b
                    .numberProperty(GearProperties.HARVEST_SPEED, 0.4f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.8f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .put(GearTypes.MATTOCK.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.4f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.25f)
                    .numberProperty(GearProperties.DURABILITY, 0.25f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.25f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.25f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.NECKLACE.get(), b -> {})
            .put(GearTypes.PAXEL.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 3, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.0f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.2f)
                    .numberProperty(GearProperties.DURABILITY, 0.35f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.3f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.2f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.PICKAXE.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.2f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .put(GearTypes.PROSPECTOR_HAMMER.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.4f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.5f)
                    .numberProperty(GearProperties.DURABILITY, -0.25f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.25f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.25f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.RING.get(), b -> {})
            .put(GearTypes.SAW.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 2, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.6f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.5f)
                    .numberProperty(GearProperties.DURABILITY, 1.0f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.5f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.HARVEST_SPEED, -0.75f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.SHEARS.get(), b -> b
                    .numberProperty(GearProperties.DURABILITY, -0.048f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.25f)
            )
            .put(GearTypes.SHIELD.get(), b -> b
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1)
            )
            .put(GearTypes.SHOVEL.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 1.5f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.0f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 2f)
            )
            .put(GearTypes.SICKLE.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 1, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 2.2f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .put(GearTypes.SLINGSHOT.get(), b -> b
                    .numberProperty(GearProperties.RANGED_DAMAGE, 0.5f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.DRAW_SPEED, 1.5f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 2)
                    .numberProperty(GearProperties.ENCHANTMENT_VALUE, -0.65f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.RANGED_DAMAGE, -0.75f, NumberProperty.Operation.MULTIPLY_BASE)
            )
            .put(GearTypes.SPEAR.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 3, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.3f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1.25f)
                    .numberProperty(GearProperties.DURABILITY, -0.2f, NumberProperty.Operation.MULTIPLY_BASE)
                    .numberProperty(GearProperties.ATTACK_REACH, 1, NumberProperty.Operation.ADD)
            )
            .put(GearTypes.SWORD.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 3, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.6f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .put(GearTypes.TRIDENT.get(), b -> b
                    .numberProperty(GearProperties.ATTACK_DAMAGE, 4, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.ATTACK_SPEED, 1.1f, NumberProperty.Operation.ADD)
                    .numberProperty(GearProperties.REPAIR_EFFICIENCY, 1f)
            )
            .build();

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private static PartBuilder addMainPartStats(PartBuilder builder) {
        var consumer = MAIN_PART_PROPERTIES.get(builder.getGearType());
        if (consumer == null) {
            throw new IllegalArgumentException("Properties for " + builder.getId() + " are missing!");
        }
        consumer.accept(builder);
        return builder;
    }
}
