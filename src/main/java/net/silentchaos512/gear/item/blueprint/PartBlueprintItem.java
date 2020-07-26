package net.silentchaos512.gear.item.blueprint;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.parts.PartType;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final PartType partType;
    private final Tag<Item> itemTag;

    public PartBlueprintItem(PartType partType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.partType = partType;
        this.itemTag = new ItemTags.Wrapper(new ResourceLocation(partType.getName().getNamespace(), "blueprints/" + partType.getName().getPath()));
    }

    public PartType getPartType() {
        return partType;
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        return partType;
    }

    @Override
    public GearType getGearType(ItemStack stack) {
        return GearType.PART;
    }

    @Override
    public Tag<Item> getItemTag() {
        return itemTag;
    }

    @Override
    protected ITextComponent getCraftedName(ItemStack stack) {
        return this.partType.getDisplayName(0);
    }
}
