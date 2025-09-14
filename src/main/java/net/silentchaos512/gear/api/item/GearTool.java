package net.silentchaos512.gear.api.item;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.Lazy;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.core.component.GearPropertiesData;
import net.silentchaos512.gear.setup.gear.PartTypes;
import net.silentchaos512.gear.util.GearHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface GearTool extends GearItem {
    Supplier<Collection<PartType>> REQUIRED_PARTS = Suppliers.memoize(() -> ImmutableList.of(
            PartTypes.MAIN.get(),
            PartTypes.ROD.get()
    ));

    default Tool createToolProperties(ItemStack gear, GearPropertiesData properties, HolderGetter<Block> blocks) {
        return new Tool(List.of(), 1.0f, 2, true);
    }

    @Override
    default void onRecalculatePost(ItemStack gear, @Nullable Player player, GearPropertiesData finalProperties) {
        GearItem.super.onRecalculatePost(gear, player, finalProperties);
        if (!GearHelper.isBroken(gear)) {
            var blocksHolderGetter = Helper.REGISTRY_LOOKUP.get().lookupOrThrow(Registries.BLOCK);
            gear.set(DataComponents.TOOL, createToolProperties(gear, finalProperties, blocksHolderGetter));
            gear.set(DataComponents.WEAPON, new Weapon(2));
        }
    }

    @Override
    default Collection<PartType> getRequiredParts() {
        return REQUIRED_PARTS.get();
    }

    class Helper {
        // This doesn't feel quite right... Is there a better way?
        // We can't use `BuiltInRegistries.acquireBootstrapRegistrationLookup(...)` because registries are frozen.
        // The registry provider takes a long time to get, so caching it is necessary to prevent a 10+ second freeze on world load.
        private static Lazy<HolderGetter.Provider> REGISTRY_LOOKUP = Lazy.of(VanillaRegistries::createLookup);
    }
}
