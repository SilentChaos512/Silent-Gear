package net.silentchaos512.gear.item.blueprint;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final PartType partType;
    private final TagKey<Item> itemTag;

    public PartBlueprintItem(PartType partType, boolean singleUse, Properties properties) {
        super(properties, singleUse);
        this.partType = partType;
        this.itemTag = ItemTags.create(new ResourceLocation(partType.getName().getNamespace(), "blueprints/" + partType.getName().getPath()));
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
    public TagKey<Item> getItemTag() {
        return itemTag;
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        return this.partType.getDisplayName(0);
    }
}
