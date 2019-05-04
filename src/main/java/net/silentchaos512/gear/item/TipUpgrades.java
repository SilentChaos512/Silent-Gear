package net.silentchaos512.gear.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.parts.ItemPartData;
import net.silentchaos512.gear.api.parts.PartOrigins;
import net.silentchaos512.gear.api.parts.PartTip;
import net.silentchaos512.lib.item.IEnumItems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public enum TipUpgrades implements IEnumItems<TipUpgrades, TipUpgrades.Item> {
    IRON,
    GOLD,
    DIAMOND,
    EMERALD,
    REDSTONE,
    GLOWSTONE,
    LAPIS,
    QUARTZ;

    private final TipUpgrades.Item item;
    private final PartTip part;

    TipUpgrades() {
        this.item = new TipUpgrades.Item();
        ResourceLocation name = new ResourceLocation(SilentGear.MOD_ID, "tip_" + name().toLowerCase(Locale.ROOT));
        this.part = new PartTip(name, PartOrigins.BUILTIN_CORE);
    }

    @Nonnull
    @Override
    public TipUpgrades getEnum() {
        return this;
    }

    @Nonnull
    @Override
    public Item getItem() {
        return this.item;
    }

    @Nonnull
    public PartTip getPart() {
        return this.part;
    }

    @Nonnull
    @Override
    public String getNamePrefix() {
        return "tip_upgrade";
    }

    public class Item extends net.minecraft.item.Item {
        @Override
        public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
            ItemPartData data = ItemPartData.instance(getPart());
            list.add(getPart().getNameColor() + data.getDisplayName(ItemStack.EMPTY).getFormattedText());
        }
    }
}
