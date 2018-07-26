package net.silentchaos512.gear.item.gear;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreArmor;
import net.silentchaos512.gear.api.parts.PartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.CommonItemStats;
import net.silentchaos512.gear.client.util.GearClientHelper;
import net.silentchaos512.gear.config.Config;
import net.silentchaos512.gear.config.ConfigOptionEquipment;
import net.silentchaos512.gear.item.blueprint.Blueprint;
import net.silentchaos512.gear.util.GearData;
import net.silentchaos512.gear.util.GearHelper;

import javax.annotation.Nonnull;
import java.util.*;

public class CoreArmor extends ItemArmor implements ICoreArmor {

    // Just using my own UUIDs, ItemArmor can keep being stingy.
    private static final UUID[] ARMOR_MODIFIERS = {UUID.fromString("cfea1f82-ab07-40ed-8384-045446707a98"), UUID.fromString("9f441293-6f6e-461f-a3e2-3cad0c06f3a5"), UUID.fromString("4c90545f-c314-4db4-8a60-dac8b3a132a2"), UUID.fromString("f96e4ac9-ab1d-423b-8392-d820d12fc454")};
    // sum = 1, starts with boots
    private static final float[] ABSORPTION_RATIO_BY_SLOT = {0.175f, 0.3f, 0.4f, 0.125f};
    // Same values as in ItemArmor.
    private static final int[] MAX_DAMAGE_ARRAY = {13, 15, 16, 11};

    private final String itemName;

    public CoreArmor(EntityEquipmentSlot slot, String name) {
        super(ArmorMaterial.DIAMOND, 0, slot);
        setNoRepair();
        this.itemName = name;
    }

    @Override
    public String getGearClass() {
        return itemName;
    }

    //region Stats and attributes

    public double getArmorProtection(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return ABSORPTION_RATIO_BY_SLOT[armorType.getIndex()] * GearData.getStat(stack, CommonItemStats.ARMOR);
    }

    public double getArmorToughness(ItemStack stack) {
        if (GearHelper.isBroken(stack)) return 0;
        return GearData.getStat(stack, CommonItemStats.ARMOR_TOUGHNESS);
    }

    private static double getGenericArmorProtection(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof CoreArmor)
            return ((CoreArmor) item).getArmorProtection(stack);
        else if (item instanceof ItemArmor)
            return ((ItemArmor) item).damageReduceAmount;
        return 0;
    }

    private static int getPlayerTotalArmorValue(EntityLivingBase player) {
        float total = 0;
        for (ItemStack armor : player.getArmorInventoryList()) {
            total += getGenericArmorProtection(armor);
        }
        return Math.round(total);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (slot == this.armorType) {
            UUID uuid = ARMOR_MODIFIERS[slot.getIndex()];
            multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(
                    uuid, "Armor modifier", getArmorProtection(stack), 0));
            multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(
                    uuid, "Armor toughness", getArmorToughness(stack), 0));
        }
        return multimap;
    }

    //endregion

    @Nonnull
    @Override
    public ConfigOptionEquipment getConfig() {
        switch (this.armorType) {
            case FEET:
                return Config.boots;
            case LEGS:
                return Config.leggings;
            case CHEST:
                return Config.chestplate;
            case HEAD:
                return Config.helmet;
            default:
                throw new IllegalArgumentException("Armor type is set to " + this.armorType);
        }
    }

    @Override
    public boolean matchesRecipe(@Nonnull Collection<ItemStack> parts) {
        // TODO
        return false;
    }

    //region Item overrides

    @Override
    public int getMaxDamage(ItemStack stack) {
        int x = GearData.getStatInt(stack, CommonItemStats.DURABILITY);
        float y = (1.8f * x + 1515) / 131;
        return (int) (MAX_DAMAGE_ARRAY[armorType.getIndex()] * y);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (GearHelper.isUnbreakable(stack)) return;
        int original = damage;
        if (!Config.toolsBreakPermanently)
            damage = MathHelper.clamp(damage, 0, getMaxDamage(stack));
        SilentGear.log.info("{}: {} -> {}", stack, original, damage);
        super.setDamage(stack, damage);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return GearHelper.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability(ItemStack stack) {
        return GearData.getStatInt(stack, CommonItemStats.ENCHANTABILITY);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        GearHelper.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
    }

    public Collection<IRecipe> getExampleRecipes() {
        Collection<IRecipe> list = new ArrayList<>();

        Ingredient blueprint = Blueprint.getBlueprintIngredientForGear(this);
        if (blueprint != null) {
            for (PartMain part : PartRegistry.getVisibleMains()) {
                ItemStack result = construct(this, part.getCraftingStack());
                NonNullList<Ingredient> ingredients = NonNullList.create();
                ingredients.add(blueprint);
                for (int i = 0; i < getConfig().getHeadCount(); ++i) {
                    ingredients.add(Ingredient.fromStacks(part.getCraftingStack()));
                }
                list.add(new ShapelessRecipes(SilentGear.MOD_ID, result, ingredients));
            }
        } else {
            SilentGear.log.warn("Trying to add {} example recipes, but could not find blueprint item!", getGearClass());
        }

        return list;
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
        if (this.isInCreativeTab(tab))
            GearHelper.getSubItems(this, tab, subItems);
    }

    //endregion

    //region Client-side methods

    private Map<ResourceLocation, String> textureCache = new HashMap<>();

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        ResourceLocation key = getPrimaryPart(stack).getKey();
        if (!textureCache.containsKey(key)) {
            String str = key.getNamespace() + ":textures/armor/" + key.getPath().replaceFirst("^main_", "")
                    + (slot == EntityEquipmentSlot.LEGS ? "_2" : "_1") + ".png";
            textureCache.put(key, str);
        }
        return textureCache.get(key);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return GearHelper.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        GearClientHelper.addInformation(stack, world, list, flag);
    }

    //endregion
}
