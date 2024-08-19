package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.InteractionHand;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.util.MathUtils;

import java.util.ArrayList;
import java.util.Collection;

public final class SelfRepairTraitEffect extends TraitEffect {
    public static final MapCodec<SelfRepairTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("activation_chance").forGetter(e -> e.activationChance),
                    Codec.INT.fieldOf("repair_amount").forGetter(e -> e.repairAmount)
            ).apply(instance, SelfRepairTraitEffect::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SelfRepairTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.activationChance,
            ByteBufCodecs.VAR_INT, e -> e.repairAmount,
            SelfRepairTraitEffect::new
    );

    private final float activationChance;
    private final int repairAmount;

    public SelfRepairTraitEffect(float activationChance, int repairAmount) {
        this.activationChance = activationChance;
        this.repairAmount = repairAmount;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.SELF_REPAIR.get();
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (shouldActivate(context)) {
            int amount = -repairAmount * context.traitLevel();
            GearHelper.attemptDamage(context.gear(), amount, context.player(), InteractionHand.MAIN_HAND);
        }
    }

    private boolean shouldActivate(TraitActionContext context) {
        if (context.player() != null && context.player().tickCount % 20 == 0) {
            return MathUtils.tryPercentage(activationChance * context.traitLevel());
        }
        return false;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = new ArrayList<>();
        float chancePercent = 100 * activationChance;
        ret.add(String.format("  - %.1f%% chance per level of %s %d durability each second",
                chancePercent,
                repairAmount > 0 ? "restoring" : "losing",
                Math.abs(repairAmount)));
        ret.add("  - Only works if equipped or in a player's inventory");
        return ret;
    }
}
