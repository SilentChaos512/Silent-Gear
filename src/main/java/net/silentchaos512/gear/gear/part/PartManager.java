package net.silentchaos512.gear.gear.part;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.part.GearPart;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.core.DataResourceManager;
import net.silentchaos512.gear.gear.PartJsonException;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class PartManager extends DataResourceManager<GearPart> {
    public PartManager() {
        super(
                PartSerializers.DISPATCH_CODEC,
                PartJsonException::new,
                "part",
                "silentgear_parts",
                "PartManager",
                SilentGear.LOGGER
        );
    }

    @Override
    public void attachExtraData(GearPart value, String packName, JsonObject json) {
        if (value instanceof AbstractGearPart gearPart) {
            gearPart.packName = packName;
        }
    }

    public List<GearPart> getPartsOfType(PartType type) {
        return stream()
                .filter(part -> part.getType() == type)
                .collect(Collectors.toList());
    }

    @Nullable
    public GearPart fromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;

        // We can't reliably keep an IItemProvider -> IGearPart map anymore
        for (GearPart part : this) {
            if (part.getIngredient().test(stack)) {
                return part;
            }
        }

        return null;
    }
}
