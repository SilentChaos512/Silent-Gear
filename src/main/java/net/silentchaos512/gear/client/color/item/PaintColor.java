package net.silentchaos512.gear.client.color.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.setup.SgDataComponents;
import org.jspecify.annotations.Nullable;

public record PaintColor() implements ItemTintSource {
    public static final PaintColor INSTANCE = new PaintColor();
    public static final MapCodec<PaintColor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity owner) {
        return stack.getOrDefault(SgDataComponents.PAINT_COLOR, -1);
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
