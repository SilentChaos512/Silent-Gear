package net.silentchaos512.gear.init;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.crafting.ingredient.GearPartIngredient;
import net.silentchaos512.gear.crafting.recipe.*;

public final class ModRecipes {
    private ModRecipes() {}

    public static void init() {
        // Recipe serializers
        register(ShapedGearRecipe.NAME, ShapedGearRecipe.SERIALIZER);
        register(ShapelessGearRecipe.NAME, ShapelessGearRecipe.SERIALIZER);
        register(GearPartSwapRecipe.NAME, GearPartSwapRecipe.SERIALIZER);
        register(QuickRepairRecipe.NAME, QuickRepairRecipe.SERIALIZER);
        register(UpgradeGearRecipe.NAME, UpgradeGearRecipe.SERIALIZER);

        // Ingredient serializers
        CraftingHelper.register(GearPartIngredient.Serializer.NAME, GearPartIngredient.Serializer.INSTANCE);

        if (SilentGear.isDevBuild()) {
            MinecraftForge.EVENT_BUS.addListener(ModRecipes::onPlayerJoinServer);
        }
    }

    private static void register(ResourceLocation id, IRecipeSerializer<?> serializer) {
        IRecipeSerializer.register(id.toString(), serializer);
    }

    private static void onPlayerJoinServer(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
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