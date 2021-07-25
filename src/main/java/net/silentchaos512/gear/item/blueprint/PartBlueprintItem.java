package net.silentchaos512.gear.item.blueprint;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

import net.minecraft.world.item.Item.Properties;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final PartType partType;
    private final Tag.Named<Item> itemTag;

    public PartBlueprintItem(PartType partType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.partType = partType;
        this.itemTag = ItemTags.bind(new ResourceLocation(partType.getName().getNamespace(), "blueprints/" + partType.getName().getPath()).toString());
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
    public Tag.Named<Item> getItemTag() {
        return itemTag;
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        return this.partType.getDisplayName(0);
    }
}
