package net.silentchaos512.gear.data.client;

import net.minecraft.data.DataGenerator;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.silentchaos512.gear.SilentGear;
import net.silentchaos512.gear.api.item.GearType;
import net.silentchaos512.gear.api.item.ICoreItem;
import net.silentchaos512.gear.api.part.PartType;
import net.silentchaos512.gear.item.CompoundPartItem;
import net.silentchaos512.gear.item.FragmentItem;
import net.silentchaos512.gear.util.Const;
import net.silentchaos512.lib.util.NameUtils;

public class CompoundModelsProvider extends ModelProvider<ItemModelBuilder> {
    public CompoundModelsProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), SilentGear.MOD_ID, ITEM_FOLDER, CompoundModelBuilder::new, existingFileHelper);
    }

    @Override
    public String getName() {
        return "Silent Gear - Compound Item Models";
    }

    @Override
    protected void registerModels() {
        ModelFile itemGenerated = getExistingFile(mcLoc("item/generated"));
        ModelFile itemHandheld = getExistingFile(mcLoc("item/handheld"));

        /*Registration.getItems(CompoundPartItem.class).forEach(item ->
                partBuilder(item).parent(itemGenerated));*/

//        fragmentBuilder(SgItems.FRAGMENT.get()).parent(itemGenerated);

        // FIXME
//        gearBuilder(ModItems.SWORD.get()).parent(itemHandheld);
//        gearBuilder(ModItems.DAGGER.get()).parent(itemHandheld);
//        gearBuilder(ModItems.KATANA.get()).parent(itemHandheld);
//        gearBuilder(ModItems.MACHETE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.SPEAR.get()).parent(itemHandheld);
//        gearBuilder(ModItems.PICKAXE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.SHOVEL.get()).parent(itemHandheld);
//        gearBuilder(ModItems.AXE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.PAXEL.get()).parent(itemHandheld);
//        gearBuilder(ModItems.HAMMER.get()).parent(itemHandheld);
//        gearBuilder(ModItems.EXCAVATOR.get()).parent(itemHandheld);
//        gearBuilder(ModItems.LUMBER_AXE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.MATTOCK.get()).parent(itemHandheld);
//        gearBuilder(ModItems.PROSPECTOR_HAMMER.get()).parent(itemHandheld);
//        gearBuilder(ModItems.SICKLE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.SHEARS.get()).parent(itemHandheld);
//        gearBuilder(ModItems.BOW.get()).parent(getExistingFile(mcLoc("item/bow")));
//        gearBuilder(ModItems.CROSSBOW.get()).parent(getExistingFile(mcLoc("item/crossbow")));
//        gearBuilder(ModItems.SLINGSHOT.get()).parent(itemHandheld);
////        gearBuilder(ModItems.SHIELD.get()).parent(itemHandheld);
//        gearBuilder(ModItems.HELMET.get()).parent(itemHandheld);
//        gearBuilder(ModItems.CHESTPLATE.get()).parent(itemHandheld);
//        gearBuilder(ModItems.LEGGINGS.get()).parent(itemHandheld);
//        gearBuilder(ModItems.BOOTS.get()).parent(itemHandheld);
    }

    protected CompoundModelBuilder gearBuilder(ICoreItem item) {
        return ((CompoundModelBuilder) getBuilder(NameUtils.fromItem(item).getPath()))
                .setLoader(Const.GEAR_MODEL_LOADER)
                .setGearType(item.getGearType());
    }

    protected CompoundModelBuilder partBuilder(CompoundPartItem item) {
        CompoundModelBuilder builder = ((CompoundModelBuilder) getBuilder(NameUtils.fromItem(item).getPath()))
                .setLoader(Const.COMPOUND_PART_MODEL_LOADER)
                .setGearType(item.getGearType())
                .setPartType(item.getPartType());

        if (item.getGearType().isArmor() && item.getGearType() != GearType.ELYTRA) {
            builder.setTexturePath("part/armor");
            if (item.getGearType().matches(GearType.HELMET))
                builder.addExtraLayer(SilentGear.getId("blueprint_helmet"));
            if (item.getGearType().matches(GearType.CHESTPLATE))
                builder.addExtraLayer(SilentGear.getId("blueprint_chestplate"));
            if (item.getGearType().matches(GearType.LEGGINGS))
                builder.addExtraLayer(SilentGear.getId("blueprint_leggings"));
            if (item.getGearType().matches(GearType.BOOTS))
                builder.addExtraLayer(SilentGear.getId("blueprint_boots"));
        }

        return builder;
    }

    private CompoundModelBuilder fragmentBuilder(FragmentItem item) {
        return ((CompoundModelBuilder) getBuilder(NameUtils.fromItem(item).getPath()))
                .setLoader(Const.FRAGMENT_MODEL_LOADER)
                .setGearType(GearType.FRAGMENT)
                .setPartType(PartType.MAIN);
    }
}
