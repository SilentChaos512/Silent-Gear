package net.silentchaos512.gear.client.gui.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.silentchaos512.gear.client.gui.component.LabelButton;
import net.silentchaos512.gear.setup.SgRegistries;

import java.util.List;

public class MaterialBookScreen extends AbstractMaterialBookScreen {
    public MaterialBookScreen() {
        super(null, List.of());
    }

    @Override
    protected void init() {
        super.init();
        this.createMenuControls();
        this.createCategoryLabels();
    }

    private void createMenuControls() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_315823_) -> {
            this.onClose();
        }).bounds(this.width / 2 - 100, 256, 200, 20).build());
    }

    private void createCategoryLabels() {
        var testButton = this.addRenderableWidget(new LabelButton(this.width / 2 + 50, this.height / 2 - 50, 100, this.font.lineHeight, Component.literal("All Materials"), this.font, this::onPressAllMaterials));
    }

    private void onPressAllMaterials(LabelButton button) {
        var newScreen = new MaterialListBookScreen(this, SgRegistries.MATERIAL.getValues(true));
        Minecraft.getInstance().setScreen(newScreen);
    }
}
