package net.silentchaos512.gear.client.material;

import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.material.IMaterialLayerList;
import net.silentchaos512.gear.api.material.MaterialLayer;
import net.silentchaos512.gear.api.material.MaterialLayerList;
import net.silentchaos512.gear.api.part.IPartData;
import net.silentchaos512.gear.client.model.PartTextures;
import net.silentchaos512.utils.Color;

public class DefaultMaterialDisplay extends MaterialDisplay {
    public static final DefaultMaterialDisplay INSTANCE = new DefaultMaterialDisplay();

    @Override
    public IMaterialLayerList getLayers(GearType gearType, IPartData part) {
        createMissingEntry(part);
        return super.getLayers(gearType, part);
    }

    @Override
    public int getLayerColor(GearType gearType, IPartData part, int layer) {
        createMissingEntry(part);
        // This would likely cause a stack overflow
        /*if (part instanceof PartData) {
            return ((PartData) part).getColor(ItemStack.EMPTY, layer, 0);
        }*/
        return Color.VALUE_WHITE;
    }

    private void createMissingEntry(IPartData part) {
        // Create a new entry in the layers map, if needed
        PartGearKey key = PartGearKey.of(GearType.ALL, part.getType());
        if (!map.containsKey(key)) {
            PartTextures texture = part.getType().getDefaultTexture();
            if (texture != null) {
                map.put(key, new MaterialLayerList(new MaterialLayer(texture, 0)));
            } else {
                map.put(key, MaterialLayerList.DEFAULT);
            }
        }
    }
}
