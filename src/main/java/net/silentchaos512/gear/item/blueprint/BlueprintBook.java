package net.silentchaos512.gear.item.blueprint;

import com.google.common.collect.Sets;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.util.CollectionUtils;
import net.silentchaos512.lib.util.StackHelper;

import java.util.List;
import java.util.Set;

public class BlueprintBook extends ItemSL {

  private static final String NBT_SELECTED = "SelectedBlueprint";
  private static final String NBT_STORED = "StoredBlueprints";

  public BlueprintBook() {

    super(1, SilentGear.MOD_ID, "blueprint_book");
    setContainerItem(this);
  }

  @Override
  public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
    list.add("Selected: " + getSelected(stack));
    list.add("Stored: " + StackHelper.getTagCompound(stack, true).getString(NBT_STORED));
    list.add("StoredCount: " + getStored(stack).size());
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      ItemStack stack1 = new ItemStack(this);
      ItemStack stack2 = new ItemStack(this);
      for (String toolClass : ModItems.toolClasses.keySet())
        addStored(stack2, toolClass);
      list.add(stack1);
      list.add(stack2);
    }
  }

  public String getSelected(ItemStack stack) {
    return StackHelper.getTagCompound(stack, true).getString(NBT_SELECTED);
  }

  public void setSelected(ItemStack stack, String toolClass) {
    if (getStored(stack).contains(toolClass))
      StackHelper.getTagCompound(stack, true).setString(NBT_SELECTED, toolClass);
  }

  public Set<String> getStored(ItemStack stack) {
    Set<String> set = Sets.newHashSet(StackHelper.getTagCompound(stack, true).getString(NBT_STORED).split(";"));
    set.removeIf(String::isEmpty);
    return set;
  }

  public boolean addStored(ItemStack stack, String toolClass) {
    NBTTagCompound tags = StackHelper.getTagCompound(stack, true);
    // Load up current stored values, make it a list (if there is any reason I hate Java...)
    String[] array = tags.getString(NBT_STORED).split(";");
    List<String> list = CollectionUtils.asMutableList(array);
    //list.removeIf(str -> str.isEmpty());

    // Don't add if already stored.
    if (list.contains(toolClass))
      return false;

    // Add it! And sort the list because OCD.
    list.add(toolClass);
    list.sort(String::compareTo);
    StringBuilder newValue = new StringBuilder();
    for (int i = 0; i < list.size(); ++i)
      newValue.append(i > 0 ? ";" : "").append(list.get(i));
    tags.setString(NBT_STORED, newValue.toString());
    return true;
  }
}
