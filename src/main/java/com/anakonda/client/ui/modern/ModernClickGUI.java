package com.anakonda.client.ui.modern;

import com.anakonda.client.module.Category;
import com.anakonda.client.ui.modern.component.Frame;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ModernClickGUI extends Screen {
    public static final ModernClickGUI INSTANCE = new ModernClickGUI();
    private final List<Frame> frames = new ArrayList<>();

    public ModernClickGUI() {
        super(Text.of("AnakondaGUI"));
        int x = 20;
        for (Category category : Category.values()) {
            frames.add(new Frame(category, x, 20, 110, 18));
            x += 125;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        for (Frame frame : frames) {
            frame.render(context, mouseX, mouseY, delta);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        // Removed super.render() to prevent overlay blur issues
        // Manually render tooltips if necessary, but Screen.render mainly draws background and children
        // Since we manage children manually in the loop above, super.render is redundant/harmful here.
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
