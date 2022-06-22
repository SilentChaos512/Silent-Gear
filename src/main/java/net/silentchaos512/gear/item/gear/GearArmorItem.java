package net.silentchaos512.gear.item.gear;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.material.IMaterialDisplay;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStats;
import net.silentchaos512.gear.client.model.ModelErrorLogging;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.part.PartData;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.utils.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GearArmorItem extends DyeableArmorItem implements ICoreArmor {
    // Just copied from ArmorItem, access transformers are too flaky
    private static final UUID[] ARMOR_MODIFIERS = {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    // sum = 1, starts with boots
    private static final float[] ABSORPTION_RATIO_BY_SLOT = {3f / 20f, 6f / 20f, 8f / 20f, 3f / 20f};
    // Same values as in ArmorItem.
    private static final int[] MAX_DAMAGE_ARRAY = {13, 15, 16, 11};

    // Caches armor colors by model key to speed up armor rendering
    private static final Cache<String, Integer> ARMOR_COLORS = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public GearArmorItem(EquipmentSlot slot) {
        super(GearHelper.DUMMY_ARMOR_MATERIAL, slot, GearHelper.getBaseItemProperties());
    }

    @Deprecated
    public GearArmorItem(EquipmentSlot slot, String name) {
        this(slot);
    }

    @Override
    public GearType getGearType() {
        switch (this.getSlot()) {
            case HEAD:
                return GearType.HELMET;
            case CHEST:
                return GearType.CHESTPLATE;
            case LEGS:
                return GearType.LEGGINGS;
            case FEET:
                return GearType.BOOTS;
            default:
                throw new IllegalStateException("Don't know the gear type for " + this.getRegistryName());
        }
    }

    @Override
    public boolean isValidSlot(String slot) {
        return this.getSlot().getName().equalsIgnoreCase(slot);
    }

    //region Stats and attributes

    @Override
    public float getRepairModifier(ItemStack stack) {
        return MAX_DAMAGE_ARRAY[this.getSlot().getIndex()];
    }

    public double getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getStat(stack, ItemStats.ARMOR);
    }

    public double getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getStat(stack, ItemStats.ARMOR_TOUGHNESS);
    }

    public double getArmorMagicProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getStat(stack, ItemStats.MAGIC_ARMOR);
    }

    private static double getGenericArmorProtection(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof GearArmorItem)
            return ((GearArmorItem) item).getArmorProtection(stack);
        else if (item instanceof ArmorItem)
            return ((ArmorItem) item).getDefense();
        return 0;
    }

    private static int getPlayerTotalArmorValue(LivingEntity player) {
        float total = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            total += getGenericArmorProtection(armor);
        }
        return Math.round(total);
    }

    @Nonnull
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = LinkedHashMultimap.create();
        if (slot == this.getSlot()) {
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            multimap.put(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", getArmorProtection(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", getArmorToughness(stack), AttributeModifier.Operation.ADDITION));
            float knockbackResistance = GearData.getStat(stack, ItemStats.KNOCKBACK_RESISTANCE) / 10f;
            if (knockbackResistance > 0) {
                multimap.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", knockbackResistance, AttributeModifier.Operation.ADDITION));
            }
            return GearHelper.getAttributeModifiers(slot, stack, multimap);
        }
        return multimap;
    }

    //endregion

    //region Item overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        int maxDamageFactor = GearData.getStatInt(stack, getDurabilityStat());
        return MAX_DAMAGE_ARRAY[this.getSlot().getIndex()] * maxDamageFactor;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack))
            return;
        if (!Config.Common.gearBreaksPermanently.get())
            damage = Mth.clamp(damage, 0, getMaxDamage(stack));
        super.setDamage(stack, damage);
    }

    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return GearHelper.damageItem(stack, amount, entity, t -> {
            GearHelper.onBroken(stack, t instanceof Player ? (Player) t : null, this.getSlot());
            onBroken.accept(t);
        });
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearHelper.getEnchantability(stack);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        GearHelper.inventoryTick(stack, world, player, 0, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        GearHelper.fillItemGroup(this, group, items);
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return TraitHelper.hasTrait(stack, Const.Traits.BRILLIANT);
    }

    //endregion

    //region Client-side methods and rendering horrors

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        // In order for colors to work, it seems the following must be true:
        // 1. Armor texture must be named using the vanilla convention
        // 2. Return value of this may NOT be cached... wat? Not a big deal I guess.
        // 3. You got lucky. The tiniest change can break everything for no apparent reason.

        // Empty texture if broken
        if (GearHelper.isBroken(stack))
            return SilentGear.MOD_ID + ":textures/models/armor/empty.png";

        int layer = slot == EquipmentSlot.LEGS ? 2 : 1;
        // Overlay - default to a blank texture
        if ("overlay".equals(type))
            return SilentGear.MOD_ID + ":textures/models/armor/all_layer_" + layer + "_overlay.png";

        // New material-based armor
        MaterialInstance material = GearData.getPrimaryArmorMaterial(stack);
        if (material != null) {
            IMaterialDisplay materialModel = material.getDisplayProperties();
            PartType partType = GearData.hasPartOfType(stack, PartType.COATING) ? PartType.COATING : PartType.MAIN;
            MaterialLayer materialLayer = materialModel.getLayerList(this.getGearType(), partType, material).getFirstLayer();
            if (materialLayer != null) {
                ResourceLocation tex = materialLayer.getTextureId();
                return tex.getNamespace() + ":textures/models/armor/"
                        + tex.getPath()
                        + "_layer_" + layer
                        + (type != null ? "_" + type : "")
                        + ".png";
            }
        }

        return "silentgear:textures/models/armor/main_generic_hc_layer_" + layer + (type != null ? "_" + type : "") + ".png";
    }

    @Override
    public boolean hasCustomColor(ItemStack stack) {
        return true;
    }

    @Override
    public int getColor(ItemStack stack) {
        try {
            return ARMOR_COLORS.get(GearData.getModelKey(stack, 0), () -> getArmorColor(stack));
        } catch (ExecutionException e) {
            ModelErrorLogging.notifyOfException(e, "armor model");
        }

        return Color.VALUE_WHITE;
    }

    private static int getArmorColor(ItemStack stack) {
        // Gets the outer-most (coating or main) part and compute its color
        PartData part = GearData.getCoatingOrMainPart(stack);
        if (part != null) {
            return part.getColor(stack);
        }
        return Color.VALUE_WHITE;
    }

    @Override
    public void clearColor(ItemStack stack) {}

    @Override
    public void setColor(ItemStack stack, int color) {}

    @Override
    public boolean isFoil(ItemStack stack) {
        return GearClientHelper.hasEffect(stack);
    }

    @Override
    public Component getName(ItemStack stack) {
        return GearHelper.getDisplayName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        GearClientHelper.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return GearHelper.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return GearHelper.getBarColor(stack);
    }

    //endregion
}
