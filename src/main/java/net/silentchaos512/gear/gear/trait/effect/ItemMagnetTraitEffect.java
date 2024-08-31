package net.silentchaos512.gear.gear.trait.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.silentchaos512.gear.api.traits.TraitActionContext;
import net.silentchaos512.gear.api.traits.TraitEffect;
import net.silentchaos512.gear.api.traits.TraitEffectType;
import net.silentchaos512.gear.core.MagnetPullTracker;
import net.silentchaos512.gear.setup.gear.TraitEffectTypes;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ItemMagnetTraitEffect extends TraitEffect {
    public static final MapCodec<ItemMagnetTraitEffect> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.FLOAT.fieldOf("pull_strength").forGetter(e -> e.pullStrength),
                    Codec.FLOAT.fieldOf("effect_range").forGetter(e -> e.effectRange),
                    Ingredient.CODEC.optionalFieldOf("affected_items", Ingredient.EMPTY).forGetter(e -> e.affectedItems),
                    Codec.STRING.optionalFieldOf("affected_items_text_for_wiki").forGetter(e ->
                            e.affectedItems.isEmpty() ? Optional.empty() : Optional.of(e.affectedItemsTextForWiki)
                    )
            ).apply(instance, (pullStrength, pullRange, affectedItems, wikiText) ->
                    wikiText.map(s -> new ItemMagnetTraitEffect(pullStrength, pullRange, affectedItems, s))
                            .orElseGet(() -> new ItemMagnetTraitEffect(pullStrength, pullRange, affectedItems))
            )
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemMagnetTraitEffect> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, e -> e.pullStrength,
            ByteBufCodecs.FLOAT, e -> e.effectRange,
            Ingredient.CONTENTS_STREAM_CODEC, e -> e.affectedItems,
            ItemMagnetTraitEffect::new
    );

    private final float pullStrength; // ORIGINAL: 0.06
    private final float effectRange; // ORIGINAL: 3.0
    private final Ingredient affectedItems;
    private final String affectedItemsTextForWiki;

    public ItemMagnetTraitEffect(float pullStrength, float effectRange, Ingredient affectedItems) {
        this(pullStrength, effectRange, affectedItems, !affectedItems.isEmpty() ? "some items" : "all items");
    }

    public ItemMagnetTraitEffect(float pullStrength, float effectRange, Ingredient affectedItems, String affectedItemsTextForWiki) {
        this.pullStrength = pullStrength;
        this.effectRange = effectRange;
        this.affectedItems = affectedItems;
        this.affectedItemsTextForWiki = affectedItemsTextForWiki;
    }

    @Override
    public TraitEffectType<?> type() {
        return TraitEffectTypes.ITEM_MAGNET.get();
    }

    @Override
    public Collection<String> getExtraWikiLines() {
        return List.of(
                "Attracts " + this.affectedItemsTextForWiki + " towards the player"
        );
    }

    @Override
    public void onUpdate(TraitActionContext context, boolean isEquipped) {
        var player = context.player();
        if (player == null || player.level().isClientSide) return;

        tickMagnet(player, context.traitLevel());
    }

    private boolean canAffectItem(ItemStack stack) {
        return affectedItems.isEmpty() || affectedItems.test(stack);
    }

    private boolean canMagneticPullItem(ItemEntity entity) {
        return !entity.hasPickUpDelay()
                && !entity.getPersistentData().getBoolean("PreventRemoteMovement")
                && canAffectItem(entity.getItem());
    }

    private void tickMagnet(Player player, int traitLevel) {
        if (player.isCrouching()) return;

        final float range = this.effectRange * traitLevel + 1;
        Vec3 target = new Vec3(player.getX(), player.getY(0.5), player.getZ());

        AABB aabb = new AABB(
                player.getX() - range,
                player.getY() - range,
                player.getZ() - range,
                player.getX() + range + 1,
                player.getY() + range + 1,
                player.getZ() + range + 1
        );
        for (ItemEntity entity : player.level().getEntitiesOfClass(ItemEntity.class, aabb, e -> e.distanceToSqr(player) < range * range)) {
            if (canMagneticPullItem(entity)) {
                // Accelerate to target point
                Vec3 vec = entity.getDismountLocationForPassenger(player).vectorTo(target);
                vec = vec.normalize().scale(this.pullStrength);
                if (entity.getY() < target.y) {
                    double xzDistanceSq = (entity.getX() - target.x) * (entity.getX() - target.x) + (entity.getZ() - target.z) * (entity.getZ() - target.z);
                    vec = vec.add(0, 0.005 + xzDistanceSq / 1000, 0);
                }
                MagnetPullTracker.pushItem(entity, vec);
            }
        }
    }
}
