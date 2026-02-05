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
        // Just render background normally
        this.renderBackground(context, mouseX, mouseY, delta);

        // Render frames without global translate first, relying on sequential draw order
        // Frames draw their own backgrounds and content.

        for (Frame frame : frames) {
            // We push a pose for each frame to ensure clean state if needed,
            // but standard 2D drawing usually doesn't need Z translation unless overlap is complex.
            // If previous Z+100 caused blurring on some but not others, it might be due to clipping.

            // We will render each frame at a distinct Z if necessary, or just rely on order.
            // Let's try rendering simply on top.
            frame.render(context, mouseX, mouseY, delta);
            frame.updatePosition(mouseX, mouseY);
        }

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
