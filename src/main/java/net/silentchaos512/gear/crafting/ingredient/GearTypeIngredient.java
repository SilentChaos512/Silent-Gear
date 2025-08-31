package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.setup.SgIngredientTypes;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public final class GearTypeIngredient implements ICustomIngredient {
    public static final MapCodec<GearTypeIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    GearType.CODEC.fieldOf("gear_type").forGetter(GearTypeIngredient::getGearType)
            ).apply(instance, GearTypeIngredient::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, GearTypeIngredient> STREAM_CODEC = StreamCodec.composite(
            GearType.STREAM_CODEC, ingredient -> ingredient.type,
            GearTypeIngredient::new
    );

    private final GearType type;

    public GearTypeIngredient(GearType type) {
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

        return stack.getItem() instanceof GearItem && ((GearItem) stack.getItem()).getGearType().matches(this.type);
    }

    @Override
    public Stream<Holder<Item>> items() {
        return BuiltInRegistries.ITEM.stream()
                .filter(item -> item instanceof GearItem gearItem && gearItem.getGearType().equals(this.type))
                .map(Holder::direct);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
