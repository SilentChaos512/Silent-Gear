package net.silentchaos512.gear.gear.trait;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.MathUtils;

public class StellarTrait extends WielderEffectTrait {
    public static final ITraitSerializer<StellarTrait> SERIALIZER = new Serializer<>(
            SilentGear.getId("stellar"),
            StellarTrait::new,
            WielderEffectTrait::deserializeJson,
            WielderEffectTrait::readFromNetwork,
            WielderEffectTrait::writeToNetwork
    );

    public StellarTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        Player player = context.player();
        if (player != null && player.tickCount % 20 == 0) {
            float chance = Const.Traits.STELLAR_REPAIR_CHANCE * context.traitLevel();
            if (MathUtils.tryPercentage(chance)) {
                GearHelper.attemptDamage(context.gear(), -1, player, InteractionHand.MAIN_HAND);
            }
        }

        super.onUpdate(context, isEquipped);
    }
}
