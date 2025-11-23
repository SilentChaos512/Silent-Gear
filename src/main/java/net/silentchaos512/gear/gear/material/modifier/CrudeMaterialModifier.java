package net.silentchaos512.gear.gear.material.modifier;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.Config;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifier;
import net.silentchaos512.gear.api.material.modifier.IMaterialModifierType;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.property.GearPropertyValue;
import net.silentchaos512.gear.api.util.PropertyKey;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.SgDataComponents;
import net.silentchaos512.gear.setup.gear.MaterialModifiers;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.TextUtil;
import net.silentchaos512.lib.util.Color;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class CrudeMaterialModifier extends UnitMaterialModifier {
    public static final CrudeMaterialModifier INSTANCE = new CrudeMaterialModifier();
    public static final Supplier<DataComponentType<Unit>> DATA_COMPONENT_TYPE = SgDataComponents.CRUDE;
    public static final MapCodec<CrudeMaterialModifier> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, CrudeMaterialModifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    public static void setOn(ItemStack materialItemStack) {
        materialItemStack.set(DATA_COMPONENT_TYPE, Unit.INSTANCE);
    }

    @Override
    public IMaterialModifierType<?> getType() {
        return MaterialModifiers.CRUDE.get();
    }

    @Override
    public <T, V extends GearPropertyValue<T>> Collection<V> modifyProperties(MaterialInstance material, PartType partType, PropertyKey<T, V> key, Collection<V> mods) {
        if (key.property().isAffectedBySynergy()) {
            final float multiplier = Config.Common.crudeMixerPropertyMultiplier.get().floatValue() - 1f;
            return IMaterialModifier.Helper.modifyNumberValuesWithBonusOrPenalty(key, mods, multiplier);
        }
        return mods;
    }

    @Override
    public void appendTooltip(List<Component> tooltip) {
        var crude = Component.translatable("misc.silentgear.crude");
        tooltip.add(TextUtil.withColor(crude, Color.FIREBRICK));
    }

    public static class Type extends UnitMaterialModifier.Type<CrudeMaterialModifier> {
        public Type() {
            super(Const.CRUDE, INSTANCE, DATA_COMPONENT_TYPE, CODEC, STREAM_CODEC);
        }
    }
}
