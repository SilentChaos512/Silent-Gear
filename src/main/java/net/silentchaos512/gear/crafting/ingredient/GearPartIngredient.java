package net.silentchaos512.gear.crafting.ingredient;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartInstance;
import net.silentchaos512.gear.gear.part.PartManager;
import net.silentchaos512.gear.setup.SgIngredientTypes;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public final class GearPartIngredient implements ICustomIngredient, IGearIngredient {
    public static final MapCodec<GearPartIngredient> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    PartType.CODEC.fieldOf("part_type").forGetter(GearPartIngredient::getPartType)
            ).apply(instance, GearPartIngredient::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, GearPartIngredient> STREAM_CODEC = StreamCodec.composite(
            PartType.STREAM_CODEC, ingredient -> ingredient.type,
            GearPartIngredient::new
    );

    private final PartType type;

    private GearPartIngredient(PartType type) {
        this.type = type;
    }

    public static GearPartIngredient of(PartType type) {
        return new GearPartIngredient(type);
    }

    @Override
    public IngredientType<?> getType() {
        return SgIngredientTypes.PART.get();
    }

    @Override
    public PartType getPartType() {
        return type;
    }

    @Override
    public Optional<Component> getJeiHint() {
        MutableComponent typeText = this.type.getDisplayName();
        MutableComponent text = TextUtil.withColor(typeText, Color.GOLD);
        return Optional.of(TextUtil.translate("jei", "partType", text));
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        PartInstance part = PartInstance.from(stack);
        return part != null && part.getType().equals(type);
    }

    @Override
    public Stream<ItemStack> getItems() {
        // Although gear parts are not available when the ingredient is constructed,
        // they are available later on
        Collection<GearPart> parts = PartManager.getPartsOfType(this.type);
        if (!parts.isEmpty()) {
            return parts.stream()
                    .flatMap(part -> Stream.of(part.getIngredient().getItems()))
                    .filter(stack -> !stack.isEmpty());
        }
        return Stream.empty();
    }

    @Override
    public boolean isSimple() {
        return false;
    }
}
