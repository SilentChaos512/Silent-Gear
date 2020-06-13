package net.silentchaos512.gear.parts.type;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartTraitInstance;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;
import net.silentchaos512.gear.parts.PartTextureType;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.utils.EnumUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundPart extends AbstractGearPart {
    public static final Serializer SERIALIZER = new Serializer(SilentGear.getId("compound_part"), CompoundPart::new);

    private PartType partType;
    private IPartPosition partPosition;

    public CompoundPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return this.partType;
    }

    @Override
    public IPartPosition getPartPosition() {
        return this.partPosition;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public PartTextureType getLiteTexture(PartData part, ItemStack gear) {
        MaterialInstance material = CompoundPartItem.getPrimaryMaterial(part.getCraftingItem());
        if (material != null) {
            return material.getMaterial().getTexture(partType, gear);
        }
        return super.getLiteTexture(part, gear);
    }

    @Override
    public int getColor(PartData part, ItemStack gear, int animationFrame) {
        return CompoundPartItem.getColor(part.getCraftingItem());
    }

    @Override
    public ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear) {
        //noinspection ConstantConditions
        return part != null ? part.getCraftingItem().getDisplayName() : super.getDisplayName(part, gear);
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartData part) {
        // Get any base modifiers for this part (should be none, normally?)
        Collection<StatInstance> baseStats = new ArrayList<>(this.stats.get(stat));
        ItemStack stack = part.getCraftingItem();
        if (!(stack.getItem() instanceof CompoundPartItem)) {
            return baseStats;
        }

        // Get the rod materials and all the stat modifiers they provide for this stat
        Collection<MaterialInstance> materials = CompoundPartItem.getMaterials(stack);
        Collection<StatInstance> statMods = materials.stream()
                .flatMap(m -> m.getStatModifiers(stat, this.partType, gear).stream())
                .collect(Collectors.toList());

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        List<StatInstance> ret = new ArrayList<>(baseStats);
        for (StatInstance.Operation op : StatInstance.Operation.values()) {
            Collection<StatInstance> modsForOp = statMods.stream().filter(s -> s.getOp() == op).collect(Collectors.toList());
            if (!modsForOp.isEmpty()) {
                StatInstance mod = StatInstance.getWeightedAverageMod(modsForOp, op);
                ret.add(mod);
            }
        }

        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, ret);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    @Override
    public List<PartTraitInstance> getTraits(ItemStack gear, PartData part) {
        List<PartTraitInstance> ret = new ArrayList<>(super.getTraits(gear, part));
        TraitHelper.getTraits(CompoundPartItem.getMaterials(part.getCraftingItem()), PartType.ROD, gear).forEach((trait, level) ->
                ret.add(new PartTraitInstance(trait, level, Collections.emptyList())));
        return ret;
    }

    private static class Serializer extends AbstractGearPart.Serializer<CompoundPart> {
        Serializer(ResourceLocation serializerId, Function<ResourceLocation, CompoundPart> function) {
            super(serializerId, function);
        }

        @Override
        public CompoundPart read(ResourceLocation id, JsonObject json) {
            CompoundPart part = super.read(id, json);
            part.partType = PartType.get(new ResourceLocation(JSONUtils.getString(json, "part_type")));
            // FIXME
            part.partPosition = EnumUtils.byName(JSONUtils.getString(json, "part_position"), PartPositions.ANY);
            return part;
        }

        @Override
        public CompoundPart read(ResourceLocation id, PacketBuffer buffer) {
            CompoundPart part = super.read(id, buffer);
            part.partType = PartType.get(buffer.readResourceLocation());
            // FIXME
            part.partPosition = EnumUtils.byName(buffer.readString(), PartPositions.ANY);
            return part;
        }

        @Override
        public void write(PacketBuffer buffer, CompoundPart part) {
            super.write(buffer, part);
            buffer.writeResourceLocation(part.partType.getName());
            // FIXME
            for (PartPositions pos : PartPositions.values()) {
                if (pos == part.partPosition) {
                    buffer.writeString(pos.name());
                    break;
                }
            }
        }
    }
}
