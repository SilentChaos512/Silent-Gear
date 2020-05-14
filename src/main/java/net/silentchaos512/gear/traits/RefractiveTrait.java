/*
 * Silent Gear -- RefractiveTrait
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.gear.traits;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.traits.ITraitSerializer;
import net.silentchaos512.gear.block.PhantomLight;
import net.silentchaos512.gear.init.ModBlocks;
import net.silentchaos512.gear.util.GearHelper;
import net.silentchaos512.lib.item.FakeItemUseContext;

public class RefractiveTrait extends SimpleTrait {
    private static final ResourceLocation SERIALIZER_ID = SilentGear.getId("refractive");
    static final ITraitSerializer<RefractiveTrait> SERIALIZER = new Serializer<>(SERIALIZER_ID, RefractiveTrait::new);

    private static final int DURABILITY_COST = 5;

    public RefractiveTrait(ResourceLocation name) {
        super(name, SERIALIZER);
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context, int traitLevel) {
        ItemStack stack = context.getItem();

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        if (!world.isRemote && stack.getDamage() < stack.getMaxDamage() - DURABILITY_COST - 1) {
            // Try place light, damage tool if successful
            ItemStack fakeBlockStack = new ItemStack(ModBlocks.PHANTOM_LIGHT);
            ActionResultType result = fakeBlockStack.onItemUse(new FakeItemUseContext(context, fakeBlockStack));
            if (result == ActionResultType.SUCCESS) {
                GearHelper.attemptDamage(stack, DURABILITY_COST, context.getPlayer(), context.getHand());
                // TODO: Custom sound effect?
                world.playSound(null, pos, SoundEvents.BLOCK_GLASS_PLACE, SoundCategory.BLOCKS, 1f, 1f);
            }
            return result;
        }

        for (int i = 0; i < 5; i++) {
            PhantomLight.spawnParticle(world, pos.offset(context.getFace()), SilentGear.random);
        }

        return ActionResultType.SUCCESS;
    }
}
