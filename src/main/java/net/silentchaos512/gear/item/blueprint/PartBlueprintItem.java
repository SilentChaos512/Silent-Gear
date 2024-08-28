package net.silentchaos512.gear.item.blueprint;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.Lazy;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.setup.SgRegistries;
import net.silentchaos512.gear.setup.gear.GearTypes;

import java.util.Objects;
import java.util.function.Supplier;

public class PartBlueprintItem extends AbstractBlueprintItem {
    private final Supplier<PartType> partType;
    private final Lazy<TagKey<Item>> itemTag;

    public PartBlueprintItem(Supplier<PartType> partType, BlueprintType blueprintType, Properties properties) {
        super(properties, blueprintType);
        this.partType = partType;
        this.itemTag = Lazy.of(() -> {
            var id = Objects.requireNonNull(SgRegistries.PART_TYPE.getKey(this.partType.get()));
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "blueprints/" + id.getPath()));
        });
    }

    public PartType getPartType() {
        return partType.get();
    }

    @Override
    public PartType getPartType(ItemStack stack) {
        return partType.get();
    }

    @Override
    public GearType getGearType(ItemStack stack) {
        return GearTypes.NONE.get();
    }

    @Override
    public TagKey<Item> getItemTag() {
        return itemTag.get();
    }

    @Override
    protected Component getCraftedName(ItemStack stack) {
        return this.partType.get().getDisplayName();
    }
}
