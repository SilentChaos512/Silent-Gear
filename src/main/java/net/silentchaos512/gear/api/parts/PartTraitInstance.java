package net.silentchaos512.gear.api.parts;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.silentchaos512.gear.api.traits.ITrait;
import net.silentchaos512.gear.api.traits.ITraitCondition;
import net.silentchaos512.gear.api.traits.ITraitConditionSerializer;
import net.silentchaos512.gear.client.KeyTracker;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.traits.TraitManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A trait, level, and optional conditions found on a gear part. Note that conditions are NOT sent
 * to the client when syncing parts, so the conditions loaded from JSON must be retained.
 */
// TODO: Rename to TraitInstance?
public class PartTraitInstance {
    private final ITrait trait;
    private final int level;
    private final List<ITraitCondition> conditions;

    public PartTraitInstance(ITrait trait, int level, Collection<ITraitCondition> conditions) {
        this.trait = trait;
        this.level = level;
        this.conditions = ImmutableList.copyOf(conditions);
    }

    public ITrait getTrait() {
        return trait;
    }

    public int getLevel() {
        return level;
    }

    public boolean conditionsMatch(PartDataList parts, ItemStack gear) {
        return conditions.stream().allMatch(c -> c.matches(gear, parts, this.trait));
    }

    public boolean conditionsMatch(Collection<MaterialInstance> materials, PartType partType, ItemStack gear) {
        return conditions.stream().allMatch(c -> c.matches(gear, partType, materials, this.trait));
    }

    public IFormattableTextComponent getDisplayName() {
        IFormattableTextComponent text = this.trait.getDisplayName(this.level).deepCopy();
        if (!conditions.isEmpty()) {
            text.func_240702_b_("*");
        }
        return text;
    }

    public void addInformation(List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (!this.trait.showInTooltip(flag)) return;

        // Display name
        IFormattableTextComponent displayName = this.getDisplayName().func_240699_a_(TextFormatting.ITALIC);
        // TODO: fix the jank
        if (trait.isHidden()) displayName.func_230530_a_(displayName.getStyle().setColor(Color.func_240743_a_(net.silentchaos512.utils.Color.DARKGRAY.getColor())));
        tooltip.add(displayName);

        // Description (usually not shown)
        if (KeyTracker.isAltDown()) {
            // TODO: fix the jank
            ITextComponent description = this.trait.getDescription(level).func_230530_a_(displayName.getStyle().setColor(Color.func_240743_a_(net.silentchaos512.utils.Color.DARKGRAY.getColor())));
            tooltip.add(new StringTextComponent("  ").func_230529_a_(description));
        }
    }

    public static PartTraitInstance deserialize(JsonObject json) {
        // Trait; this should already exist
        ResourceLocation traitId = new ResourceLocation(JSONUtils.getString(json, "name"));
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            throw new JsonSyntaxException("Unknown trait: " + traitId);
        }

        // Level; clamp to valid values
        int level = MathHelper.clamp(JSONUtils.getInt(json, "level", 1), 1, trait.getMaxLevel());

        // Conditions (if available)
        List<ITraitCondition> conditions = new ArrayList<>();
        if (json.has("conditions")) {
            JsonArray array = json.getAsJsonArray("conditions");
            for (JsonElement j : array) {
                conditions.add(ITraitConditionSerializer.getCondition(j.getAsJsonObject()));
            }
        }

        return new PartTraitInstance(trait, level, conditions);
    }

    public static PartTraitInstance read(PacketBuffer buffer) {
        ResourceLocation traitId = buffer.readResourceLocation();
        ITrait trait = TraitManager.get(traitId);
        if (trait == null) {
            throw new IllegalStateException("Unknown trait: " + traitId);
        }
        int level = buffer.readByte();
        return new PartTraitInstance(trait, level, ImmutableList.of());
    }

    public void write(PacketBuffer buffer) {
        buffer.writeResourceLocation(this.trait.getId());
        buffer.writeByte(this.level);
    }
}
