package net.silentchaos512.gear.init;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.recipe.GearCrafting;
import net.silentchaos512.gear.crafting.recipe.RecipeModularItem;

import java.util.HashMap;
import java.util.Map;

public class ModRecipes {
    public static final Map<String, RecipeModularItem> gearCrafting = new HashMap<>();

    private ModRecipes() {}

    public static void registerAll() {
        // Gear recipes TODO: move to JSON
        /*
        RecipeMaker recipes = reg.getRecipeMaker();
        for (ICoreItem item : ModItems.toolClasses.values()) {
            final RecipeModularItem recipe = new RecipeModularItem(item);
            gearCrafting.put(item.getGearClass(), recipe);
            recipes.addCustomRecipe("core_" + item.getGearClass(), recipe);
        }

        // Smelting recipes
        recipes.addSmelting(ModBlocks.crimsonIronOre, new ItemStack(CraftingItems.CRIMSON_IRON_INGOT.getItem()), 0.6f);

        // Repair recipe "fix" - prevents gear items from being destroyed by vanilla
        SilentGear.LOGGER.info("Replacing vanilla repair recipe");
        IRecipe rec = new RepairItemRecipeFix();
        rec.setRegistryName(new ResourceLocation("minecraft", "repairitem"));
        ForgeRegistries.RECIPES.register(rec);
        */
    }

    public static void init() {
        // Recipe serializers
        RecipeSerializers.register(GearCrafting.Serializer.INSTANCE);

        // Ingredient serializers
        CraftingHelper.register(GearPartIngredient.Serializer.NAME, GearPartIngredient.Serializer.INSTANCE);

        if (SilentGear.isDevBuild()) {
            MinecraftForge.EVENT_BUS.addListener(ModRecipes::onPlayerJoinServer);
        }
    }

    private static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player.world.isRemote || player.world.getServer() == null) return;

        ResourceLocation[] recipes = player.world.getServer().getRecipeManager().getRecipes()
                .stream()
                .map(IRecipe::getId)
                .filter(name -> name.getNamespace().equals(SilentGear.MOD_ID))
                .toArray(ResourceLocation[]::new);

        SilentGear.LOGGER.info("DEV: Unlocking {} recipes in recipe book", recipes.length);
        player.unlockRecipes(recipes);
    }
}