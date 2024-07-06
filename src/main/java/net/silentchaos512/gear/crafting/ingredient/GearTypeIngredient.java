package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.setup.SgIngredientTypes;

import javax.annotation.Nullable;
import java.util.Arrays;
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
    private ItemStack[] itemStacks;

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

        return stack.getItem() instanceof ICoreItem && ((ICoreItem) stack.getItem()).getGearType().matches(this.type);
    }

    private void dissolve() {
        if (this.itemStacks == null) {
            // FIXME
            this.itemStacks = new ItemStack[0];
        }
    }

    @Override
    public Stream<ItemStack> getItems() {
        return Arrays.stream(itemStacks);
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
