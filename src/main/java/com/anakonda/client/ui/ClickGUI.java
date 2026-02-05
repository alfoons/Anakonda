package com.anakonda.client.ui;

import com.anakonda.client.module.Category;
import com.anakonda.client.ui.component.Frame;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {
    public static final ClickGUI INSTANCE = new ClickGUI();
    private final List<Frame> frames = new ArrayList<>();

    public ClickGUI() {
        super(Text.of("ClickGUI"));
        int x = 10;
        for (Category category : Category.values()) {
            frames.add(new Frame(category, x, 10, 100, 15));
            x += 110;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background only once at the beginning
        this.renderBackground(context, mouseX, mouseY, delta);

        // Disable depth test to ensure UI draws on top of everything without Z-fighting or weird clipping
        // This is often the cause of "dimmed" or "blurred" looking UI elements if they are behind the near plane or fog
        com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();

        for (Frame frame : frames) {
            frame.render(context, mouseX, mouseY, delta);
            frame.updatePosition(mouseX, mouseY);
        }

        com.mojang.blaze3d.systems.RenderSystem.enableDepthTest();

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            frame.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Frame frame : frames) {
            frame.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
