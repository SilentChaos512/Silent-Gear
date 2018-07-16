package net.silentchaos512.gear.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.lib.MaterialGrade;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.gear.init.ModItems;
import net.silentchaos512.lib.item.ItemSL;
import net.silentchaos512.lib.registry.RecipeMaker;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TipUpgrade extends ItemSL {

    public TipUpgrade() {
        super(Type.values().length, SilentGear.MOD_ID, "tip_upgrade");
    }

    @Override
    public void addRecipes(RecipeMaker recipes) {
        for (Type type : Type.values())
            recipes.addShapelessOre(nameForType(type), new ItemStack(this, 1, type.ordinal()),
                    CraftingItems.UPGRADE_BASE.getStack(), type.oreName);
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        ItemPart part = PartRegistry.get(stack);
        if (part != null) {
            ItemPartData data = new ItemPartData(part, MaterialGrade.NONE, stack);
            list.add(part.getNameColor() + part.getLocalizedName(data, ItemStack.EMPTY));
        }
    }

    @Override
    public void getModels(Map<Integer, ModelResourceLocation> models) {
        for (Type type : Type.values())
            models.put(type.ordinal(), new ModelResourceLocation(SilentGear.RESOURCE_PREFIX + nameForType(type), "inventory"));
    }

    @Override
    public String getNameForStack(ItemStack stack) {
        int meta = stack.getItemDamage();
        if (meta >= 0 && meta < Type.values().length)
            return nameForType(Type.values()[meta]);
        return super.getNameForStack(stack);
    }

    private String nameForType(Type type) {
        return getName() + "_" + type.name().toLowerCase(Locale.ROOT);
    }

    public enum Type {
        IRON("ingotIron"), GOLD("ingotGold"), DIAMOND("gemDiamond"), EMERALD("gemEmerald"), REDSTONE("dustRedstone"), GLOWSTONE("dustGlowstone"), LAPIS("gemLapis");

        public final String oreName;

        Type(String oreName) {

            this.oreName = oreName;
        }
    }
}
