package net.silentchaos512.gear.data.part;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.data.DataGenerators;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.init.SgItems;
import net.silentchaos512.gear.item.CraftingItems;
import net.silentchaos512.gear.item.MainPartItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;
import net.silentchaos512.utils.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class PartsProvider implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;

    public PartsProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public String getName() {
        return "Silent Gear - Parts";
    }

    protected Collection<PartBuilder> getParts() {
        Collection<PartBuilder> ret = new ArrayList<>();

        ret.add(part("adornment", GearType.CURIO, PartType.ADORNMENT, SgItems.ADORNMENT));
        ret.add(part("binding", GearType.TOOL, PartType.BINDING, SgItems.BINDING));
        ret.add(part("bowstring", GearType.RANGED_WEAPON, PartType.CORD, SgItems.CORD));
        ret.add(part("coating", GearType.ALL, PartType.COATING, SgItems.COATING)
                .blacklistGearType(GearType.ELYTRA)
        );
        ret.add(part("fletching", GearType.PROJECTILE, PartType.FLETCHING, SgItems.FLETCHING));
        ret.add(part("grip", GearType.TOOL, PartType.GRIP, SgItems.GRIP));
        ret.add(part("lining", GearType.ARMOR, PartType.LINING, SgItems.LINING));
        ret.add(part("rod", GearType.TOOL, PartType.ROD, SgItems.ROD));
        ret.add(part("tip", GearType.TOOL, PartType.TIP, SgItems.TIP));

        SgItems.getItems(MainPartItem.class).forEach(item -> {
            PartBuilder builder = part(NameUtils.fromItem(item).getPath(), item.getGearType(), item.getPartType(), item);
            ret.add(addHeadStats(builder));
        });

        ret.add(upgradePart("misc/spoon", CraftingItems.SPOON_UPGRADE)
                .upgradeGearTypes(GearType.PICKAXE.getMatcher(false))
                .stat(ItemStats.DURABILITY, 0.2f, StatInstance.Operation.MUL1)
                .stat(ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(Const.Traits.SPOON, 1)
                .display(GearType.PICKAXE, PartType.MISC_UPGRADE, new MaterialLayer(SilentGear.getId("spoon"), Color.VALUE_WHITE))
        );
        ret.add(upgradePart("misc/road_maker", CraftingItems.ROAD_MAKER_UPGRADE)
                .upgradeGearTypes(GearType.EXCAVATOR.getMatcher(false))
                .stat(ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL1)
                .stat(ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(Const.Traits.ROAD_MAKER, 1)
                .display(GearType.EXCAVATOR, PartType.MISC_UPGRADE, new MaterialLayer(SilentGear.getId("road_maker"), Color.VALUE_WHITE))
        );
        ret.add(upgradePart("misc/wide_plate", CraftingItems.WIDE_PLATE_UPGRADE)
                .upgradeGearTypes(new GearTypeMatcher(false, GearType.HAMMER, GearType.EXCAVATOR))
                .stat(ItemStats.DURABILITY, 0.1f, StatInstance.Operation.MUL1)
                .stat(ItemStats.RARITY, 10, StatInstance.Operation.ADD)
                .trait(Const.Traits.WIDEN, 1)
                .display(GearType.HAMMER, PartType.MISC_UPGRADE, new MaterialLayer(SilentGear.getId("wide_plate"), Color.VALUE_WHITE))
                .display(GearType.EXCAVATOR, PartType.MISC_UPGRADE, new MaterialLayer(SilentGear.getId("wide_plate"), Color.VALUE_WHITE))
        );
        ret.add(upgradePart("misc/red_card", CraftingItems.RED_CARD_UPGRADE)
                .upgradeGearTypes(GearTypeMatcher.ALL)
                .stat(ItemStats.RARITY, -5, StatInstance.Operation.ADD)
                .trait(Const.Traits.RED_CARD, 1)
        );

        return ret;
    }

    private static PartBuilder part(String name, GearType gearType, PartType partType, ItemLike item) {
        return new PartBuilder(SilentGear.getId(name), gearType, partType, item)
                .name(Component.translatable("part.silentgear." + name.replace('/', '.')));
    }

    private static PartBuilder upgradePart(String name, ItemLike item) {
        return part(name, GearType.ALL, PartType.MISC_UPGRADE, item)
                .serializerType(PartSerializers.UPGRADE_PART);
    }

    @SuppressWarnings({"OverlyComplexMethod", "OverlyLongMethod"})
    private static PartBuilder addHeadStats(PartBuilder builder) {
        // Tools
        if (isMainPart(builder, SgItems.AXE_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 5, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -3, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1);
        if (isMainPart(builder, SgItems.EXCAVATOR_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -3f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 2.0f)
                    .stat(ItemStats.DURABILITY, 1.0f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.5f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.5f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.HAMMER_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 4, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -3.2f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.5f)
                    .stat(ItemStats.DURABILITY, 1.0f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.5f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.5f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.MATTOCK_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.6f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.25f)
                    .stat(ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.25f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.25f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.PROSPECTOR_HAMMER_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.6f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.5f)
                    .stat(ItemStats.DURABILITY, -0.25f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.25f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.25f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.PAXEL_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 3, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -3.0f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.2f)
                    .stat(ItemStats.DURABILITY, 0.35f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.3f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.2f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.PICKAXE_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.8f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f);
        if (isMainPart(builder, SgItems.SAW_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.4f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.5f)
                    .stat(ItemStats.DURABILITY, 1.0f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.5f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.HARVEST_SPEED, -0.75f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.SHOVEL_HEAD))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 1.5f, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -3.0f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 2f);
        if (isMainPart(builder, SgItems.SICKLE_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -1.8f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f);

        // Melee weapons
        if (isMainPart(builder, SgItems.DAGGER_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -1.2f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 2.0f)
                    .stat(ItemStats.MELEE_DAMAGE, -0.5f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.KATANA_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 4, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.6f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f)
                    .stat(ItemStats.DURABILITY, 0.125f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.1f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.KNIFE_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -1.6f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 2.0f)
                    .stat(ItemStats.MELEE_DAMAGE, -0.5f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.DURABILITY, 0.25f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.MACHETE_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.2f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f);
        if (isMainPart(builder, SgItems.SPEAR_TIP))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 3, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.7f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.25f)
                    .stat(ItemStats.DURABILITY, -0.2f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ATTACK_REACH, 1, StatInstance.Operation.ADD);
        if (isMainPart(builder, SgItems.SWORD_BLADE))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 3, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.4f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f);
        if (isMainPart(builder, SgItems.TRIDENT_PRONGS))
            return builder
                    .stat(ItemStats.MELEE_DAMAGE, 4, StatInstance.Operation.ADD)
                    .stat(ItemStats.ATTACK_SPEED, -2.9f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1f);

        // Ranged weapons
        if (isMainPart(builder, SgItems.BOW_LIMBS))
            return builder
                    .stat(ItemStats.RANGED_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.RANGED_SPEED, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.45f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.CROSSBOW_LIMBS))
            return builder
                    .stat(ItemStats.RANGED_DAMAGE, 2, StatInstance.Operation.ADD)
                    .stat(ItemStats.RANGED_SPEED, 1, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.45f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.SLINGSHOT_LIMBS))
            return builder
                    .stat(ItemStats.RANGED_DAMAGE, 0, StatInstance.Operation.ADD)
                    .stat(ItemStats.RANGED_SPEED, 1.5f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 2)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.65f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.RANGED_DAMAGE, -0.75f, StatInstance.Operation.MUL1);

        // Armor
        if (isMainPart(builder, SgItems.HELMET_PLATES)
                || isMainPart(builder, SgItems.CHESTPLATE_PLATES)
                || isMainPart(builder, SgItems.LEGGING_PLATES)
                || isMainPart(builder, SgItems.BOOT_PLATES))
            return builder
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1);

        if (isMainPart(builder, SgItems.ELYTRA_WINGS))
            return builder
                    .stat(ItemStats.ARMOR, -0.65f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.ARMOR, -3.5f, StatInstance.Operation.ADD)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1);

        // Oddballs
        if (isMainPart(builder, SgItems.SHEARS_BLADES))
            return builder
                    .stat(ItemStats.DURABILITY, -0.048f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.25f);
        if (isMainPart(builder, SgItems.FISHING_REEL_AND_HOOK))
            return builder
                    .stat(ItemStats.DURABILITY, -0.5f, StatInstance.Operation.MUL1)
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1.25f)
                    .stat(ItemStats.ENCHANTMENT_VALUE, -0.75f, StatInstance.Operation.MUL1);
        if (isMainPart(builder, SgItems.SHIELD_PLATE))
            return builder
                    .stat(ItemStats.REPAIR_EFFICIENCY, 1);
        if (isMainPart(builder, SgItems.ARROW_HEADS))
            return builder
                    .stat(ItemStats.REPAIR_EFFICIENCY, 0.75f);

        // Curios
        if (isMainPart(builder, SgItems.RING_SHANK))
            return builder;
        if (isMainPart(builder, SgItems.BRACELET_BAND))
            return builder;

        throw new IllegalArgumentException("Stats for " + builder.id + " are missing!");
    }

    private static boolean isMainPart(PartBuilder builder, ItemLike item) {
        if (!(item.asItem() instanceof MainPartItem))
            throw new IllegalArgumentException("Item " + NameUtils.fromItem(item) + " is not a main part item!");
        return builder.id.equals(NameUtils.fromItem(item));
    }

    @Override
    public void run(CachedOutput cache) {
        Path outputFolder = this.generator.getOutputFolder();
        Set<ResourceLocation> entries = Sets.newHashSet();

        //noinspection OverlyLongLambda
        getParts().forEach(builder -> {
            if (entries.contains(builder.id)) {
                throw new IllegalStateException("Duplicate part: " + builder.id);
            }

            // Part
            entries.add(builder.id);
            Path path = outputFolder.resolve(String.format("data/%s/silentgear_parts/%s.json", builder.id.getNamespace(), builder.id.getPath()));
            trySaveStable(cache, builder, path);

            // Model
            // TODO
        });
    }

    private static void trySaveStable(CachedOutput cache, PartBuilder builder, Path path) {
        try {
            DataGenerators.saveStable(cache, builder.serialize(), path);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
