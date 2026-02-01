package net.silentchaos512.gear.client.gui.book;

import net.silentchaos512.gear.api.material.Material;
import net.silentchaos512.gear.client.gui.book.page.Page;
import net.silentchaos512.gear.client.gui.component.IngredientLabelButton;
import net.silentchaos512.gear.gear.material.MaterialInstance;
import net.silentchaos512.gear.setup.gear.PartTypes;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MaterialListBookScreen extends AbstractMaterialBookScreen {
    private final List<Material> materials;

    public MaterialListBookScreen(@Nullable MaterialBookScreen previousScreen, List<Material> materials) {
        super(previousScreen, List.of(new Page(), new Page(), new Page()));
        this.materials = new ArrayList<>(materials);
        this.materials.sort((m1, m2) -> {
            var partType = PartTypes.MAIN.get();
            var name1 = m1.getDisplayName(MaterialInstance.of(m1), partType).getString().toLowerCase(Locale.ROOT);
            var name2 = m2.getDisplayName(MaterialInstance.of(m2), partType).getString().toLowerCase(Locale.ROOT);
            return name1.compareTo(name2);
        });
    }

    @Override
    protected void init() {
        super.init();
//        this.materialsInitTest();
    }

    private void materialsInitTest() {
        for (int i = 0; i < 10; ++i) {
            var material = this.materials.get(i);
            this.addRenderableWidget(new IngredientLabelButton(this.width / 2 - 125, 40 + i * (this.font.lineHeight + 2), 100, this.font.lineHeight, material.getIngredient(), material.getDisplayName(MaterialInstance.of(material), PartTypes.MAIN.get()), this.font, button -> {}));
        }
    }
}
