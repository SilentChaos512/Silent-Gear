package net.silentchaos512.gear.gear.trait;

import net.minecraft.world.InteractionHand;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.ApiConst;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.utils.MathUtils;

import java.util.Collection;

public class SelfRepairTrait extends SimpleTrait {
    public static final ITraitSerializer<SelfRepairTrait> SERIALIZER = new Serializer<>(
            ApiConst.SELF_REPAIR_TRAIT_ID,
            SelfRepairTrait::new,
            (trait, json) -> {
                trait.activationChance = GsonHelper.getAsFloat(json, "activation_chance");
                trait.repairAmount = GsonHelper.getAsInt(json, "repair_amount", 1);
            },
            (trait, buffer) -> {
                trait.activationChance = buffer.readFloat();
                trait.repairAmount = buffer.readVarInt();
            },
            (trait, buffer) -> {
                buffer.writeFloat(trait.activationChance);
                buffer.writeVarInt(trait.repairAmount);
            }
    );

    private float activationChance;
    private int repairAmount;

    public SelfRepairTrait(ResourceLocation id) {
        super(id, SERIALIZER);
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        if (shouldActivate(context)) {
            int amount = -repairAmount * context.getTraitLevel();
            GearHelper.attemptDamage(context.getGear(), amount, context.getPlayer(), InteractionHand.MAIN_HAND);
        }
    }

    private boolean shouldActivate(TraitActionContext context) {
        if (context.getPlayer() != null && context.getPlayer().tickCount % 20 == 0) {
            return MathUtils.tryPercentage(activationChance * context.getTraitLevel());
        }
        return false;
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        Collection<String> ret = super.getExtraWikiLines();
        float chancePercent = 100 * activationChance;
        ret.add(String.format("  - %.1f%% chance per level of %s %d durability each second",
                chancePercent,
                repairAmount > 0 ? "restoring" : "losing",
                Math.abs(repairAmount)));
        ret.add("  - Only works if equipped or in a player's inventory");
        return ret;
    }
}
