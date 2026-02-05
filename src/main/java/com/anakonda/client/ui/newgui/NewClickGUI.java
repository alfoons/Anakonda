package com.anakonda.client.ui.newgui;

import com.anakonda.client.module.Category;
import com.anakonda.client.ui.newgui.component.Panel;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NewClickGUI extends Screen {
    public static final NewClickGUI INSTANCE = new NewClickGUI();
    private final List<Panel> panels = new ArrayList<>();

    public NewClickGUI() {
        super(Text.of("NewClickGUI"));
        int x = 20;
        for (Category category : Category.values()) {
            panels.add(new Panel(category, x, 20, 100, 15));
            x += 120;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        // Depth handling to fix "blur" issues
        RenderSystem.disableDepthTest();
        // RenderSystem.clear(256, false); // Removed again due to compilation failure on types

        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY, delta);
        }

        RenderSystem.enableDepthTest();
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            panel.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
