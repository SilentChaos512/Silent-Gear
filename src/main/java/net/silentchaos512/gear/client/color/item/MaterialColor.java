package net.silentchaos512.gear.client.color.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.client.util.GearColorUtils;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.GearTypes;
import net.silentchaos512.gear.setup.gear.PartTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MaterialColor() implements ItemTintSource {
    public static final MaterialColor INSTANCE = new MaterialColor();
    public static final MapCodec<MaterialColor> CODEC = MapCodec.unit(() -> INSTANCE);

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.has(SgDataComponents.PAINT_COLOR)) {
            return stack.getOrDefault(SgDataComponents.PAINT_COLOR, -1);
        } else if (stack.has(SgDataComponents.MATERIAL_SINGLE)) {
            MaterialInstance material = stack.get(SgDataComponents.MATERIAL_SINGLE);
            assert material != null;
            return material.getColor(GearTypes.ALL.get(), PartTypes.MAIN.get()) | 0xFF000000;
        } else if (stack.has(SgDataComponents.MATERIAL_LIST)) {
            List<MaterialInstance> materials = stack.get(SgDataComponents.MATERIAL_LIST);
            assert materials != null;
            return GearColorUtils.getBlendedColorForCompoundMaterial(materials);
        }
        // No material(s) in item
        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return CODEC;
    }
}
