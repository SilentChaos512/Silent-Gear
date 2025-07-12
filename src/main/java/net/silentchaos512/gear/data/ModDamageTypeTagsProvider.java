package net.silentchaos512.gear.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.setup.SgTags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {
    protected ModDamageTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, SilentGear.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(SgTags.DamageTypes.BOUNCE_PROTECTS)
                .add(DamageTypes.FLY_INTO_WALL);
    }
}
