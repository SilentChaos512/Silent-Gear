package net.silentchaos512.gear.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;
import net.silentchaos512.gear.SilentGear;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Registration {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = create(ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final DeferredRegister<Block> BLOCKS = create(ForgeRegistries.BLOCKS);
    public static final DeferredRegister<MenuType<?>> CONTAINERS = create(ForgeRegistries.MENU_TYPES);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = create(ForgeRegistries.ENCHANTMENTS);
    public static final DeferredRegister<EntityType<?>> ENTITIES = create(ForgeRegistries.ENTITY_TYPES);
    public static final DeferredRegister<Item> ITEMS = create(ForgeRegistries.ITEMS);
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS = create(Registry.LOOT_ITEM_REGISTRY);
    public static final DeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = create(Registry.LOOT_FUNCTION_REGISTRY);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS);
    public static final DeferredRegister<PoiType> POINTS_OF_INTEREST = create(ForgeRegistries.POI_TYPES);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = create(ForgeRegistries.VILLAGER_PROFESSIONS);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = create(ForgeRegistries.RECIPE_SERIALIZERS);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = create(ForgeRegistries.RECIPE_TYPES);

    private Registration() {throw new IllegalAccessError("Utility class");}

    public static void register() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCK_ENTITIES.register(modEventBus);
        BLOCKS.register(modEventBus);
        CONTAINERS.register(modEventBus);
        ENCHANTMENTS.register(modEventBus);
        ENTITIES.register(modEventBus);
        ITEMS.register(modEventBus);
        LOOT_CONDITIONS.register(modEventBus);
        LOOT_FUNCTIONS.register(modEventBus);
        LOOT_MODIFIERS.register(modEventBus);
        POINTS_OF_INTEREST.register(modEventBus);
        PROFESSIONS.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);

        ModBlockEntities.register();
        ModBlocks.register();
        ModContainers.register();
        GearEnchantments.register();
        ModEntities.register();
        ModItems.register();
        ModLootStuff.init();
        ModRecipes.register();
//        GearVillages.register();
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

    private static <T> DeferredRegister<T> create(IForgeRegistry<T> registry) {
        return DeferredRegister.create(registry, SilentGear.MOD_ID);
    }

    private static <B> DeferredRegister<B> create(ResourceKey<Registry<B>> registry) {
        return DeferredRegister.create(registry, SilentGear.MOD_ID);
    }
}
