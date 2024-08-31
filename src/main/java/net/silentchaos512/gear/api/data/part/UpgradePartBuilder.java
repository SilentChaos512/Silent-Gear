package net.silentchaos512.gear.api.data.part;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.GearTypeMatcher;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.gear.part.PartSerializers;
import net.silentchaos512.gear.gear.part.UpgradeGearPart;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class UpgradePartBuilder extends PartBuilder {
    private GearTypeMatcher upgradeGearTypes = GearTypeMatcher.ALL;

    public UpgradePartBuilder(ResourceLocation id, Supplier<GearType> gearType, Supplier<PartType> partType) {
        super(id, gearType, partType);
    }

    public UpgradePartBuilder upgradeGearTypes(GearTypeMatcher matcher) {
        this.upgradeGearTypes = matcher;
        return this;
    }

    @Override
    public JsonElement serialize() {
        SilentGear.LOGGER.info("Trying to serialize upgrade part \"{}\"", this.id);

        UpgradeGearPart part = new UpgradeGearPart(
                this.gearType,
                this.partType,
                this.upgradeGearTypes,
                Objects.requireNonNull(this.crafting),
                Objects.requireNonNull(this.display),
                this.properties
        );

        var serializer = PartSerializers.UPGRADE.get();
        var jsonElementDataResult = serializer.codec().codec().encodeStart(JsonOps.INSTANCE, part);
        if (jsonElementDataResult.isError()) {
            SilentGear.LOGGER.error("Something went wrong when serializing upgrade part \"{}\"", this.id);
        }

        var json = jsonElementDataResult.getOrThrow();
        var serializerId = Objects.requireNonNull(SgRegistries.PART_SERIALIZER.getKey(serializer));
        json.getAsJsonObject().addProperty("type", serializerId.toString());
        return json;
    }
}
