package net.silentchaos512.gear.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.setup.SgSounds;

public class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    protected ModSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, SilentGear.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(
                SgSounds.GEAR_DAMAGED,
                definition()
                        .subtitle("subtitles.item.silentgear.gear_damaged")
                        .with(
                                sound(SilentGear.getId("kachink")),
                                sound(SilentGear.getId("snap"))
                        )
        );
    }
}
