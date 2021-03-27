package net.silentchaos512.gear.client.material;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.*;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.client.util.ColorUtils;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.item.CompoundMaterialItem;
import net.silentchaos512.gear.item.IColoredMaterialItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.utils.Color;

import java.util.List;

public class CompoundMaterialDisplay implements IMaterialDisplay {
    public static final CompoundMaterialDisplay INSTANCE = new CompoundMaterialDisplay();

    @Override
    public ResourceLocation getMaterialId() {
        return Const.NULL_ID;
    }

    @Override
    public IMaterialLayerList getLayerList(GearType gearType, IPartData part, IMaterialInstance material) {
        ItemStack stack = material.getItem();
        if (!material.isSimple() && stack.getItem() instanceof IColoredMaterialItem) {
            MaterialInstance primary = ((IColoredMaterialItem) stack.getItem()).getPrimarySubMaterial(stack);

            if (primary != null) {
                IMaterialDisplay model = MaterialDisplayManager.get(primary);
                return model.getLayerList(gearType, part, material);
            }
        }

        return MaterialLayerList.DEFAULT;
    }

    @Override
    public int getLayerColor(GearType gearType, IPartData part, IMaterialInstance materialIn, int layer) {
        List<MaterialLayer> layers = getLayerList(gearType, part, materialIn).getLayers();

        if (layer < layers.size()) {
            ItemStack stack = materialIn.getItem();

            if (stack.getItem() instanceof CompoundMaterialItem) {
                List<MaterialInstance> subMaterials = CompoundMaterialItem.getSubMaterials(stack);
                int color = ColorUtils.getBlendedColor((CompoundMaterialItem) stack.getItem(), subMaterials, layer);
//                return layers.get(layer).getColor();
                return color;
            }
        }
        return Color.VALUE_WHITE;
    }
}
