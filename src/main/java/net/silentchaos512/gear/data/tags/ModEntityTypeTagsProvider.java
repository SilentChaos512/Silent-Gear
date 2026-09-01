package net.silentchaos512.gear.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityTypeIds;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.SgTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SilentGear.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(SgTags.EntityTypes.TRIDENTS)
                .add(EntityTypeIds.TRIDENT)
                .add(SgEntities.TRIDENT_PROJECTILE.get().builtInRegistryHolder().key());
    }
}
