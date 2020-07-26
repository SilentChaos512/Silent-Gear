package net.silentchaos512.gear.init;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.silentchaos512.gear.SilentGear;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Registration {
    public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = create(ForgeRegistries.CONTAINERS);
    public static final DeferredRegister<EntityType<?>> ENTITIES = create(ForgeRegistries.ENTITIES);
    public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = create(ForgeRegistries.TILE_ENTITIES);

    private Registration() {throw new IllegalAccessError("Utility class");}

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        CONTAINERS.register(modEventBus);
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        TILE_ENTITIES.register(modEventBus);

        ModBlocks.register();
        ModContainers.register();
        ModEntities.register();
        ModItems.register();
        ModTileEntities.register();
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> Collection<T> getBlocks(Class<T> clazz) {
        return BLOCKS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(clazz::isInstance)
                .map(block -> (T) block)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> getItems(Class<T> clazz) {
        return ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(clazz::isInstance)
                .map(item -> (T) item)
                .collect(Collectors.toList());
    }

    public static Collection<Item> getItems(Predicate<Item> predicate) {
        return ITEMS.getEntries().stream()
                .map(RegistryObject::get)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, SilentGear.MOD_ID);
    }
}
