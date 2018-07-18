package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.silentchaos512.gear.api.lib.ItemPartData;
import net.silentchaos512.gear.api.lib.MaterialGrade;
import net.silentchaos512.gear.api.parts.ItemPart;
import net.silentchaos512.gear.api.parts.PartRegistry;
import net.silentchaos512.lib.item.IEnumItems;

import javax.annotation.Nonnull;
import java.util.List;

public class TipUpgrade extends Item {

    private TipUpgrade() {
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        ItemPart part = PartRegistry.get(stack);
        if (part != null) {
            ItemPartData data = new ItemPartData(part, MaterialGrade.NONE, stack);
            list.add(part.getNameColor() + part.getTranslatedName(data, ItemStack.EMPTY));
        }
    }

    public enum Type implements IEnumItems<Type, TipUpgrade> {
        IRON("ingotIron"), GOLD("ingotGold"), DIAMOND("gemDiamond"), EMERALD("gemEmerald"), REDSTONE("dustRedstone"), GLOWSTONE("dustGlowstone"), LAPIS("gemLapis");

        public final String oreName;
        private final TipUpgrade item;

        Type(String oreName) {
            this.oreName = oreName;
            this.item = new TipUpgrade();
        }

        @Nonnull
        @Override
        public Type getEnum() {
            return this;
        }

        @Nonnull
        @Override
        public TipUpgrade getItem() {
            return item;
        }

        @Nonnull
        @Override
        public String getNamePrefix() {
            return "tip_upgrade";
        }
    }
}
