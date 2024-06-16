package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgIngredientTypes;
import net.silentchaos512.gear.setup.SgItems;

import javax.annotation.Nullable;

public final class GearTypeIngredient extends Ingredient {
    public static final Codec<GearTypeIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            GearType.CODEC.fieldOf("gear_type").forGetter(GearTypeIngredient::getGearType)
    ).apply(instance, GearTypeIngredient::new));

    private final GearType type;

    public GearTypeIngredient(GearType type) {
        super(SgItems.ITEMS.getEntries().stream()
                .filter(holder -> holder.get() instanceof ICoreItem)
                .map(holder -> (ICoreItem) holder.get())
                .filter(item -> item.getGearType().matches(type))
                .map(item -> new ItemValue(new ItemStack(item))));
        this.type = type;
    }

    public static GearTypeIngredient of(GearType type) {
        return new GearTypeIngredient(type);
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.GEAR_TYPE.get();
    }

    private GearType getGearType() {
        return type;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        return stack.getItem() instanceof ICoreItem && ((ICoreItem) stack.getItem()).getGearType().matches(this.type);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
