package com.anakonda.client.ui.component;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Frame {
    private final Category category;
    private int x, y, width, height;
    private boolean dragging;
    private int dragX, dragY;
    private boolean open = true;
    private final List<ModuleButton> buttons = new ArrayList<>();

    public Frame(Category category, int x, int y, int width, int height) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        int offset = height;
        for (Module module : ModuleManager.INSTANCE.getModulesByCategory(category)) {
            buttons.add(new ModuleButton(module, this, offset));
            offset += height;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw Header
        context.fill(x, y, x + width, y + height, new Color(40, 40, 40, 255).getRGB());
        context.drawText(MinecraftClient.getInstance().textRenderer, category.name, x + 5, y + 3, -1, false);

        // Draw Expand/Collapse indicator
        String expandText = open ? "-" : "+";
        context.drawText(MinecraftClient.getInstance().textRenderer, expandText, x + width - 10, y + 3, -1, false);

        if (open) {
            int offset = height;
            for (ModuleButton button : buttons) {
                button.setOffset(offset);
                button.render(context, mouseX, mouseY, delta);
                offset += height;
            }
        }
    }

    public void updatePosition(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) { // Left click to drag
                dragging = true;
                dragX = (int) (mouseX - x);
                dragY = (int) (mouseY - y);
            } else if (button == 1) { // Right click to expand/collapse
                open = !open;
            }
        }

        if (open) {
            for (ModuleButton btn : buttons) {
                btn.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
