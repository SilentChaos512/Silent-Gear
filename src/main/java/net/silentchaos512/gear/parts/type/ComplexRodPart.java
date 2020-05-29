package net.silentchaos512.gear.parts.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.material.IMaterialInstance;
import net.silentchaos512.gear.api.material.IPartMaterial;
import net.silentchaos512.gear.api.parts.IPartPosition;
import net.silentchaos512.gear.api.parts.IPartSerializer;
import net.silentchaos512.gear.api.parts.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.item.PartItem;
import net.silentchaos512.gear.parts.AbstractGearPart;
import net.silentchaos512.gear.parts.PartData;
import net.silentchaos512.gear.parts.PartPositions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ComplexRodPart extends AbstractGearPart {
    public static final IPartSerializer<ComplexRodPart> SERIALIZER = new Serializer(SilentGear.getId("complex_rod"), ComplexRodPart::new);

    public ComplexRodPart(ResourceLocation name) {
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
        Collection<IMaterialInstance> materials = ((PartItem) stack.getItem()).getMaterials(stack);
        Collection<StatInstance> statMods = materials.stream()
                .flatMap(m -> {
                    IPartMaterial material = m.getMaterial();
                    return material != null ? material.getStatModifiers(gear, stat, this.getType()).stream() : Stream.of();
                })
                .collect(Collectors.toList());

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        List<StatInstance> ret = new ArrayList<>(baseStats);
        for (StatInstance.Operation op : StatInstance.Operation.values()) {
            Collection<StatInstance> modsForOp = statMods.stream().filter(s -> s.getOp() == op).collect(Collectors.toList());
            if (!modsForOp.isEmpty()) {
                ret.add(new StatInstance(ItemStat.getWeightedAverage(modsForOp, op), op));
            }
        }

        GetStatModifierEvent event = new GetStatModifierEvent(part, stat, ret);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getModifiers();
    }

    private static class Serializer extends AbstractGearPart.Serializer<ComplexRodPart> {
        public Serializer(ResourceLocation serializerId, Function<ResourceLocation, ComplexRodPart> function) {
            super(serializerId, function);
        }
    }
}
