package com.anakonda.client.ui.component;

import com.anakonda.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class ModuleButton {
    private final Module module;
    private final Frame parent;
    private int offset;

    public ModuleButton(Module module, Frame parent, int offset) {
        this.module = module;
        this.parent = parent;
        this.offset = offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int x = parent.getX();
        int y = parent.getY() + offset;
        int width = parent.getWidth();
        int height = parent.getHeight();

        boolean hovered = isHovered(mouseX, mouseY);

        int color;
        if (module.isEnabled()) {
            color = new Color(0, 180, 0, 255).getRGB(); // Green enabled
        } else {
            color = new Color(60, 60, 60, 255).getRGB(); // Dark gray disabled
        }

        if (hovered) {
             // Lighten logic or simple override
             if (!module.isEnabled()) color = new Color(80, 80, 80, 255).getRGB();
        }

        context.fill(x, y, x + width, y + height, color);
        context.drawText(MinecraftClient.getInstance().textRenderer, module.getName(), x + 5, y + 4, -1, false);
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle();
            }
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        int x = parent.getX();
        int y = parent.getY() + offset;
        int width = parent.getWidth();
        int height = parent.getHeight();
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
