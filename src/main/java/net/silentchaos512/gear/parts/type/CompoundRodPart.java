package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.PartItem;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CompoundRodPart extends AbstractGearPart {
    public static final IPartSerializer<CompoundRodPart> SERIALIZER = new Serializer(SilentGear.getId("compound_rod"), CompoundRodPart::new);

    public CompoundRodPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public PartType getType() {
        return PartType.ROD;
    }

    @Override
    public IPartPosition getPartPosition() {
        return PartPositions.ROD;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public Collection<StatInstance> getStatModifiers(ItemStack gear, ItemStat stat, PartData part) {
        // Get any base modifiers for this part (should be none, normally?)
        Collection<StatInstance> baseStats = new ArrayList<>(this.stats.get(stat));
        ItemStack stack = part.getCraftingItem();
        if (!(stack.getItem() instanceof PartItem)) {
            return baseStats;
        }

        // Get the rod materials and all the stat modifiers they provide for this stat
        Collection<MaterialInstance> materials = PartItem.getMaterials(stack);
        Collection<StatInstance> statMods = materials.stream()
                .flatMap(m -> m.getStatModifiers(stat, this.getType(), gear).stream())
                .collect(Collectors.toList());

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        List<StatInstance> ret = new ArrayList<>(baseStats);
        for (StatInstance.Operation op : StatInstance.Operation.values()) {
            Collection<StatInstance> modsForOp = statMods.stream().filter(s -> s.getOp() == op).collect(Collectors.toList());
            if (!modsForOp.isEmpty()) {
                ret.add(StatInstance.getWeightedAverageMod(modsForOp, op));
            }
        }

        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, ret);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    private static class Serializer extends AbstractGearPart.Serializer<CompoundRodPart> {
        public Serializer(ResourceLocation serializerId, Function<ResourceLocation, CompoundRodPart> function) {
            super(serializerId, function);
        }
    }
}
