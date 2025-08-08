package net.silentchaos512.gear.setup;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.silentchaos512.gear.SilentGear;

public class SgAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SilentGear.MOD_ID);

    public static final DeferredHolder<Attribute, RangedAttribute> MAGIC_ARMOR = ATTRIBUTES.register(
            "magic_armor",
            () -> new RangedAttribute(
                    "magic_armor",
                    0.0,
                    0.0,
                    Integer.MAX_VALUE
            )
    );
}
