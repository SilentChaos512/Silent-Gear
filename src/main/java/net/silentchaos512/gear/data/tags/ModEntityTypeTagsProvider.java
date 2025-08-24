package net.silentchaos512.gear.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.setup.SgEntities;
import net.silentchaos512.gear.setup.SgTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends IntrinsicHolderTagsProvider<EntityType<?>> {
    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        //noinspection deprecation
        super(output, Registries.ENTITY_TYPE, lookupProvider, et -> et.builtInRegistryHolder().key(), SilentGear.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(SgTags.EntityTypes.TRIDENTS)
                .add(EntityType.TRIDENT)
                .add(SgEntities.TRIDENT_PROJECTILE.get());
    }
}
