package net.silentchaos512.gear.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.item.IStatItem;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.lib.MaterialGrade;
import net.silentchaos512.gear.api.lib.PartDataList;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.ItemPartMain;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.client.util.EquipmentClientHelper;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.gear.init.ModMaterials;
import net.silentchaos512.gear.item.blueprint.IBlueprint;
import net.silentchaos512.gear.util.EquipmentData;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.registry.RecipeMaker;
import net.silentchaos512.lib.util.LocalizationHelper;
import net.silentchaos512.lib.util.StackHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class ToolHead extends ItemSL implements IStatItem {

    public static final String NAME = "tool_head";
    private static final String NBT_ROOT = "ToolHeadData";
    private static final String NBT_TOOL_CLASS = "ToolClass";
    private static final String NBT_MATERIALS = "Materials";
    private static final String NBT_STAT_CACHE = "StatCache";
    private static final String NBT_IS_EXAMPLE = "IsExample";

    public ToolHead() {
        super(1, SilentGear.MOD_ID, NAME);
        setUnlocalizedName(getFullName());
        // temp hack for ItemSL bug
        this.itemName = NAME;
    }

    /**
     * Create a stack with the given materials. Best for getting crafting results.
     */
    public ItemStack getStack(String toolClass, Collection<ItemStack> materials) {
        return getStack(toolClass, PartDataList.from(materials));
    }

    public ItemStack getStack(String toolClass, PartDataList parts) {
        ItemStack result = new ItemStack(this);
        NBTTagCompound tags = getData(result);
        tags.setString(NBT_TOOL_CLASS, toolClass);

        NBTTagList tagList = new NBTTagList();
        parts.stream().filter(data -> data.part instanceof ItemPartMain)
                .map(data -> data.writeToNBT(new NBTTagCompound()))
                .forEach(tagList::appendTag);
        tags.setTag(NBT_MATERIALS, tagList);

        writeStatCache(result, parts);
        return result;
    }

    /**
     * Create a stack with a single part. Best for sub-items.
     */
    private ItemStack getStack(String toolClass, ItemPartMain part, boolean isExample) {
        boolean hasGuard = "sword".equals(toolClass);

        ItemStack result = new ItemStack(this);
        NBTTagCompound tags = getData(result);
        tags.setString(NBT_TOOL_CLASS, toolClass);
        tags.setBoolean(NBT_IS_EXAMPLE, isExample);

        NBTTagList tagList = new NBTTagList();
        NBTTagCompound partTags = new NBTTagCompound();
        part.writeToNBT(partTags);
        tagList.appendTag(partTags);
        if (hasGuard)
            tagList.appendTag(partTags);
        tags.setTag(NBT_MATERIALS, tagList);

        ItemPartData data = new ItemPartData(part, MaterialGrade.NONE, part.getCraftingStack());
        writeStatCache(result, PartDataList.of(data));
        return result;
    }

    private void writeStatCache(ItemStack stack, PartDataList parts) {
        ICoreItem item = ModItems.toolClasses.get(getToolClass(stack));
        double synergy = EquipmentData.calculateSynergyValue(parts, parts.getUniqueParts(true));
        Multimap<ItemStat, StatInstance> stats = EquipmentData.getStatModifiers(item, parts, synergy);

        NBTTagCompound tags = new NBTTagCompound();
        for (ItemStat stat : stats.keySet()) {
            float value = stat.compute(0f, stats.get(stat));
            tags.setFloat(stat.getUnlocalizedName(), value);
        }
        tags.setFloat("synergy", (float) synergy);
        getData(stack).setTag(NBT_STAT_CACHE, tags);
    }

    /**
     * Get the tool class this item matches.
     */
    @Nonnull
    public String getToolClass(ItemStack stack) {
        return getData(stack).getString(NBT_TOOL_CLASS);
    }

    /**
     * Get the primary (first) part the tool head was constructed with.
     */
    @Nullable
    public ItemPartMain getPrimaryPart(ItemStack stack) {
        NBTTagCompound tags = getData(stack);
        if (!tags.hasKey(NBT_MATERIALS))
            return ModMaterials.mainWood;
        NBTTagList tagList = tags.getTagList(NBT_MATERIALS, 10);
        if (tagList.tagCount() == 0)
            return ModMaterials.mainWood;
        NBTTagCompound partTags = tagList.getCompoundTagAt(0);
        return (ItemPartMain) ItemPartData.readFromNBT(partTags).part;
    }

    @Nullable
    public ItemPartMain getSecondaryPart(ItemStack stack) {
        NBTTagCompound tags = getData(stack);
        if (!tags.hasKey(NBT_MATERIALS))
            return ModMaterials.mainWood;
        NBTTagList tagList = tags.getTagList(NBT_MATERIALS, 10);
        if (tagList.tagCount() < 2)
            return ModMaterials.mainWood;
        NBTTagCompound partTags = tagList.getCompoundTagAt(1);
        return (ItemPartMain) ItemPartData.readFromNBT(partTags).part;
    }

    /**
     * Get all parts the tool head was constructed with.
     */
    @Nonnull
    public Collection<ItemPartData> getAllParts(ItemStack stack) {
        ImmutableList.Builder<ItemPartData> builder = ImmutableList.builder();
        NBTTagCompound tags = getData(stack);
        if (tags.hasKey(NBT_MATERIALS)) {
            NBTTagList tagList = tags.getTagList(NBT_MATERIALS, 10);
            for (NBTBase nbt : tagList)
                builder.add(ItemPartData.readFromNBT((NBTTagCompound) nbt));
        }
        return builder.build();
    }

    /**
     * Convenience method for getting NBT compound.
     */
    @Nonnull
    private NBTTagCompound getData(ItemStack stack) {
        NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
        if (!tags.hasKey(NBT_ROOT))
            tags.setTag(NBT_ROOT, new NBTTagCompound());
        return tags.getCompoundTag(NBT_ROOT);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String toolClass = getToolClass(stack);
        if (!toolClass.isEmpty()) {
            String toolName = SilentGear.localization.getItemSubText(toolClass, "name");
            // TODO
            return SilentGear.localization.getItemSubText(getName(), toolClass);
        }
        return super.getItemStackDisplayName(stack);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        LocalizationHelper loc = SilentGear.localization;
        String toolClass = getToolClass(stack);

        if (getData(stack).getBoolean(NBT_IS_EXAMPLE)) {
            list.add(TextFormatting.RED + loc.getItemSubText(itemName, "isExample1"));
            list.add(TextFormatting.RED + loc.getItemSubText(itemName, "isExample2"));
        }

        list.add(TextFormatting.AQUA + loc.getItemSubText(toolClass, "name"));

        // Materials used in crafting
        for (ItemPartData data : getAllParts(stack))
            list.add("- " + data.part.getLocalizedName(data, ItemStack.EMPTY));

        ICoreItem toolItem = ModItems.toolClasses.get(toolClass);
        // TODO: We're constructing a rod-less tool each time to get stats. Is that bad?
        ItemStack constructed = toolItem.construct((Item) toolItem, getAllParts(stack));
        EquipmentData.recalculateStats(constructed);
        EquipmentClientHelper.addInformation(constructed, world, list, flag);
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        // TODO Auto-generated method stub
        return super.getRarity(stack);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
        if (this.isInCreativeTab(tab))
            for (String toolClass : ModItems.toolClasses.keySet())
                for (ItemPartMain part : PartRegistry.getVisibleMains())
                    list.add(getStack(toolClass, part, true));
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
        ModItems.toolClasses.forEach((toolClass, item) -> {
            Ingredient blueprint = new Ingredient(ModItems.blueprint.getStack(toolClass)) {
                @Override
                public boolean apply(ItemStack stack) {
                    return stack.getItem() instanceof IBlueprint && ((IBlueprint) stack.getItem()).getOutputInfo(stack).gear == item;
                }
            };
            for (ItemPartMain part : PartRegistry.getVisibleMains()) {
                ItemStack result = getStack(toolClass, part, true);
                Object[] inputs = new Object[item.getConfig().getHeadCount() + 1];
                inputs[0] = blueprint;
                for (int i = 1; i < inputs.length; ++i) {
                    inputs[i] = part.getCraftingStack();
                }
                String recipeKey = "head_example_" + toolClass + "_" + part.getKey().toString().replaceAll(":", "_");
                recipes.addShapelessOre(recipeKey, result, inputs);
            }
        });
    }

    @Override
    public float getStat(@Nonnull ItemStack stack, @Nonnull ItemStat stat) {
        NBTTagCompound root = getData(stack);
        if (!root.hasKey(NBT_STAT_CACHE))
            return stat.getDefaultValue();

        NBTTagCompound tags = root.getCompoundTag(NBT_STAT_CACHE);
        return tags.getFloat(stat.getUnlocalizedName());
    }

    public String getSubtypeKey(ItemStack stack) {
        ItemPart part = getPrimaryPart(stack);
        return getToolClass(stack) + (part != null ? "|" + part.getModelIndex(0) : "|empty");
    }
}
