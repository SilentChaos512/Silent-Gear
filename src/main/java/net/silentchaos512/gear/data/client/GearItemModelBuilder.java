package net.silentchaos512.gear.data.client;

import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.silentchaos512.gear.api.item.GearItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.client.setup.SgItemTintSources;
import net.silentchaos512.gear.item.GearItemSet;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class GearItemModelBuilder {
    private static final List<TextureSlot> TEXTURE_SLOTS = List.of(
            TextureSlot.LAYER0,
            TextureSlot.LAYER1,
            TextureSlot.LAYER2,
            ModItemModelProvider.ExtraSlots.LAYER3,
            ModItemModelProvider.ExtraSlots.LAYER4,
            ModItemModelProvider.ExtraSlots.LAYER5
    );
    private static final List<ModelTemplate> MODEL_TEMPLATES = List.of(
            ModelTemplates.FLAT_ITEM,
            ModelTemplates.TWO_LAYERED_ITEM,
            ModelTemplates.THREE_LAYERED_ITEM,
            ModItemModelProvider.ExtraModelTemplates.FOUR_LAYERED_ITEM,
            ModItemModelProvider.ExtraModelTemplates.FIVE_LAYERED_ITEM,
            ModItemModelProvider.ExtraModelTemplates.SIX_LAYERED_ITEM
    );

    private final GearItemSet<? extends GearItem> itemSet;
    private final ResourceLocation typeKey;
    private final Map<PartType, ResourceLocation> layers = new LinkedHashMap<>();
    private final Map<PartType, ItemTintSource> tints = new HashMap<>();

    public GearItemModelBuilder(GearItemSet<? extends GearItem> itemSet) {
        this.itemSet = itemSet;
        this.typeKey = Objects.requireNonNull(SgRegistries.GEAR_TYPE.getKey(this.itemSet.type()));
    }

    public GearItemModelBuilder simpleLayer(Supplier<PartType> partType, String texturePath) {
        var texture = ResourceLocation.fromNamespaceAndPath(this.typeKey.getNamespace(), "item/" + this.typeKey.getPath() + "/" + texturePath);
        this.layers.put(partType.get(), texture);
        return this;
    }

    public GearItemModelBuilder tintedLayer(Supplier<PartType> partType, String texturePath) {
        var texture = ResourceLocation.fromNamespaceAndPath(this.typeKey.getNamespace(), "item/" + this.typeKey.getPath() + "/" + texturePath);
        this.layers.put(partType.get(), texture);
        this.tints.put(partType.get(), SgItemTintSources.gearPartColor(partType));
        return this;
    }

    public void generateModel(ItemModelOutput itemModelOutput, BiConsumer<ResourceLocation, ModelInstance> modelOutput) {
        var textureMapping = new TextureMapping();
        List<ItemTintSource> tintSourceList = new ArrayList<>();
        int i = 0;
        for (PartType partType : this.layers.keySet()) {
            textureMapping.put(TEXTURE_SLOTS.get(i), this.layers.get(partType));
            tintSourceList.add(this.tints.getOrDefault(partType, ItemModelGenerators.BLANK_LAYER));
            i += 1;
            if (i >= TEXTURE_SLOTS.size()) {
                throw new IllegalStateException("Too many layers for gear model");
            }
        }
        var modelTemplate = MODEL_TEMPLATES.get(this.layers.size() - 1);
        ResourceLocation modelKey = modelTemplate.create(
                this.itemSet.gearItem(),
                textureMapping,
                modelOutput
        );
        itemModelOutput.accept(
                this.itemSet.gearItem(),
                ItemModelUtils.tintedModel(
                        modelKey,
                        tintSourceList.toArray(new ItemTintSource[0])
                )
        );
    }
}
