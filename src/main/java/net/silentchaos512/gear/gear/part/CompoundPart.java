package net.silentchaos512.gear.gear.part;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.event.GetStatModifierEvent;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.material.IMaterial;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.api.part.IPartSerializer;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.api.stats.ItemStat;
import net.silentchaos512.gear.api.stats.StatInstance;
import net.silentchaos512.gear.api.traits.TraitInstance;
import net.silentchaos512.gear.api.util.StatGearKey;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.gear.material.MaterialManager;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.gear.util.SynergyUtils;
import net.silentchaos512.gear.util.TraitHelper;
import net.silentchaos512.utils.MathUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompoundPart extends AbstractGearPart {
    private GearType gearType = GearType.ALL;
    private PartType partType;

    public CompoundPart(ResourceLocation name) {
        super(name);
    }

    @Override
    public GearType getGearType() {
        return gearType;
    }

    @Nullable
    public static MaterialInstance getPrimaryMaterial(IPartData part) {
        return CompoundPartItem.getPrimaryMaterial(part.getItem());
    }

    @Override
    public int getTier(PartData part) {
        MaterialInstance material = getPrimaryMaterial(part);
        return material != null ? material.getTier(this.partType) : super.getTier(part);
    }

    @Override
    public PartType getType() {
        return this.partType;
    }

    @Override
    public IPartSerializer<?> getSerializer() {
        return PartSerializers.COMPOUND_PART;
    }

    @Override
    public int getColor(PartData part, ItemStack gear, int layer, int animationFrame) {
        List<MaterialInstance> materials = getMaterials(part);
        if (gear.getItem() instanceof ICoreItem) {
            return ColorUtils.getBlendedColor((ICoreItem) gear.getItem(), part, materials, layer);
        } else {
            return ColorUtils.getBlendedColor((CompoundPartItem) part.getItem().getItem(), materials, layer);
        }
    }

    @Override
    public ITextComponent getDisplayName(@Nullable PartData part, ItemStack gear) {
        if (part != null) {
            return part.getItem().getDisplayName();
        }
        return super.getDisplayName(null, gear);
    }

    @Override
    public ITextComponent getDisplayNamePrefix(@Nullable PartData part, ItemStack gear) {
        if (part != null) {
            MaterialInstance material = getPrimaryMaterial(part);
            if (material != null) {
                return material.get().getDisplayNamePrefix(gear, partType);
            }
        }
        return super.getDisplayNamePrefix(part, gear);
    }

    @Override
    public ITextComponent getMaterialName(@Nullable PartData part, ItemStack gear) {
        if (part != null) {
            MaterialInstance material = getPrimaryMaterial(part);
            if (material != null) {
                return material.getDisplayName(partType, gear);
            }
        }
        return super.getMaterialName(null, gear);
    }

    @Override
    public String getModelKey(PartData part) {
        String str = "{" + getMaterials(part).stream()
                .map(m -> SilentGear.shortenId(m.getId()))
                .collect(Collectors.joining(",")) +
                "}";
        return super.getModelKey(part) + str;
    }



    @Override
    public Collection<StatInstance> getStatModifiers(IPartData part, PartType partType, StatGearKey key, ItemStack gear) {
        // Get the materials and all the stat modifiers they provide for this stat
        List<MaterialInstance> materials = getMaterials(part);
        List<StatInstance> statMods = materials.stream()
                .flatMap(m -> m.getStatModifiers(partType, key).stream())
                .collect(Collectors.toList());

        // Get any base modifiers for this part (could be none)
        statMods.addAll(this.stats.get(key));

        if (statMods.isEmpty()) {
            // No modifiers for this stat, so doing anything else is pointless
            return statMods;
        }

        GetStatModifierEvent event = new GetStatModifierEvent((PartData) part, (ItemStat) key.getStat(), statMods);
        MinecraftForge.EVENT_BUS.post(event);

        // Average together all modifiers of the same op. This makes things like rods with varying
        // numbers of materials more "sane".
        List<StatInstance> ret = new ArrayList<>(event.getModifiers());
        for (StatInstance.Operation op : StatInstance.Operation.values()) {
            Collection<StatInstance> modsForOp = ret.stream().filter(s -> s.getOp() == op).collect(Collectors.toList());
            if (modsForOp.size() > 1) {
                StatInstance mod = compressModifiers(modsForOp, op);
                ret.removeIf(inst -> inst.getOp() == op);
                ret.add(mod);
            }
        }

        // Synergy
        if (key.getStat().doesSynergyApply()) {
            final float synergy = SynergyUtils.getSynergy(this.partType, materials, getTraits(part, partType, gearType, gear));
            if (!MathUtils.floatsEqual(synergy, 1.0f)) {
                final float multi = synergy - 1f;
                for (int i = 0; i < ret.size(); ++i) {
                    StatInstance oldMod = ret.get(i);
                    float value = oldMod.getValue();
                    // Taking the abs of value times multi makes negative mods become less negative
                    StatInstance newMod = oldMod.copySetValue(value + Math.abs(value) * multi);
                    ret.remove(i);
                    ret.add(i, newMod);
                }
            }
        }

        return ret;
    }

    private static StatInstance compressModifiers(Collection<StatInstance> mods, StatInstance.Operation operation) {
        // We do NOT want to average together max modifiers...
        if (operation == StatInstance.Operation.MAX) {
            return mods.stream()
                    .max((o1, o2) -> Float.compare(o1.getValue(), o2.getValue()))
                    .orElse(StatInstance.of(0, operation))
                    .copy();
        }

        return StatInstance.getWeightedAverageMod(mods, operation);
    }

    @Override
    public Collection<TraitInstance> getTraits(IPartData part, PartType partType, GearType gearType, ItemStack gear) {
        List<TraitInstance> ret = new ArrayList<>(super.getTraits(part, partType, gearType, gear));
        List<MaterialInstance> materials = getMaterials(part);

        TraitHelper.getTraits(materials, GearHelper.getType(gear), this.partType, gear).forEach((trait, level) -> {
            TraitInstance inst = TraitInstance.of(trait, level);
            if (inst.conditionsMatch(materials, GearHelper.getType(gear), this.partType, gear)) {
                ret.add(inst);
            }
        });

        return ret;
    }

    @Override
    public List<MaterialInstance> getMaterials(IPartData part) {
        return CompoundPartItem.getMaterials(part.getItem());
    }

    @Override
    public PartData randomizeData(GearType gearType, int tier) {
        for (ItemStack stack : this.getIngredient().getMatchingStacks()) {
            if (stack.getItem() instanceof CompoundPartItem) {
                int materialCount = getRandomMaterialCount(partType);
                List<MaterialInstance> materials = getRandomMaterials(gearType, materialCount, tier);
                ItemStack craftingItem = ((CompoundPartItem) stack.getItem()).create(materials);
                return PartData.of(this, craftingItem);
            }
        }
        return super.randomizeData(gearType, tier);
    }

    @Override
    public boolean canAddToGear(ItemStack gear, PartData part) {
        GearType type = GearHelper.getType(gear);
        return type.matches(this.gearType);
    }

    private static int getRandomMaterialCount(PartType partType) {
        if (partType == PartType.MAIN) {
            int ret = 1;
            for (int i = 0; i < 3; ++i) {
                if (SilentGear.RANDOM.nextInt(100) < 70 - 30 * i)
                    ++ret;
                else
                    break;
            }
            return ret;
        }
        return SilentGear.RANDOM.nextInt(2) + 1;
    }

    private List<MaterialInstance> getRandomMaterials(GearType gearType, int count, int tier) {
        // Excludes children, will select a random child material (if appropriate) below
        List<IMaterial> matsOfTier = MaterialManager.getValues(tier == 0).stream()
                .filter(m -> tier < 0 || tier == m.getTier(this.partType))
                .filter(m -> {
                    MaterialInstance inst = MaterialInstance.of(m);
                    return m.allowedInPart(inst, this.partType) && m.isCraftingAllowed(inst, this.partType, gearType);
                })
                .collect(Collectors.toList());

        if (!matsOfTier.isEmpty()) {
            List<MaterialInstance> ret = new ArrayList<>();
            for (int i = 0; i < count; ++i) {
                IMaterial material = matsOfTier.get(SilentGear.RANDOM.nextInt(matsOfTier.size()));
                ret.add(getRandomChildMaterial(material));
            }
            return ret;
        }

        if (tier == -1) {
            // Something went wrong...
            return Collections.emptyList();
        }

        // No materials of tier? Select randoms of any tier.
        return getRandomMaterials(gearType, count, -1);
    }

    private static MaterialInstance getRandomChildMaterial(IMaterial material) {
        // Selects a random child of the given material, or the material itself if it doesn't have any
        List<IMaterial> children = MaterialManager.getChildren(material);
        if (children.isEmpty()) {
            return MaterialInstance.of(material);
        }
        return MaterialInstance.of(children.get(SilentGear.RANDOM.nextInt(children.size())));
    }

    @Override
    public String toString() {
        return "CompoundPart{" +
                "id=" + this.getId() +
                ", partType=" + partType +
                '}';
    }

    public static class Serializer extends AbstractGearPart.Serializer<CompoundPart> {
        Serializer(ResourceLocation serializerId, Function<ResourceLocation, CompoundPart> function) {
            super(serializerId, function);
        }

        @Override
        public CompoundPart read(ResourceLocation id, JsonObject json) {
            CompoundPart part = super.read(id, json, false);
            String gearTypeStr = JSONUtils.getString(json, "gear_type");
            part.gearType = GearType.get(gearTypeStr);
            if (!part.gearType.isGear()) {
                throw new JsonParseException("Unknown gear type: " + gearTypeStr);
            }
            part.partType = PartType.get(new ResourceLocation(JSONUtils.getString(json, "part_type")));
            return part;
        }

        @Override
        public CompoundPart read(ResourceLocation id, PacketBuffer buffer) {
            CompoundPart part = super.read(id, buffer);
            part.gearType = GearType.get(buffer.readString());
            part.partType = PartType.get(buffer.readResourceLocation());
            return part;
        }

        @Override
        public void write(PacketBuffer buffer, CompoundPart part) {
            super.write(buffer, part);
            buffer.writeString(part.gearType.getName());
            buffer.writeResourceLocation(part.partType.getName());
        }
    }
}
