package net.silentchaos512.gear.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.SilentGear;

public class ModResourceLocation extends ResourceLocation {
    public static final Codec<ModResourceLocation> CODEC = Codec.STRING
            .comapFlatMap(ModResourceLocation::readModLocation, ModResourceLocation::writeModLocation)
            .stable();

    public ModResourceLocation(String resourceName) {
        super(addModNamespace(resourceName));
    }

    public ModResourceLocation(ResourceLocation resourceLocation) {
        this(resourceLocation.toString());
    }

    private static String addModNamespace(String resourceName) {
        if (resourceName.contains(":")) {
            return resourceName;
        }
        return SilentGear.MOD_ID + ":" + resourceName;
    }

    public static DataResult<ModResourceLocation> readModLocation(String str) {
        try {
            return DataResult.success(new ModResourceLocation(str));
        } catch (ResourceLocationException ex) {
            return DataResult.error(() -> "Not a valid resource location: " + str + " " + ex.getMessage());
        }
    }

    public String writeModLocation() {
        if (this.getNamespace().equalsIgnoreCase(SilentGear.MOD_ID)) {
            return this.getPath();
        }
        return super.toString();
    }
}
