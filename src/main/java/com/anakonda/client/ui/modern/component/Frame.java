package com.anakonda.client.ui.modern.component;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Frame extends Component {
    private final Category category;
    private final List<Button> buttons = new ArrayList<>();
    private boolean open = true;
    private boolean dragging;
    private int dragX, dragY;

    public Frame(Category category, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.category = category;

        for (Module module : ModuleManager.INSTANCE.getModulesByCategory(category)) {
            buttons.add(new Button(module, this, x, y, width, 16));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Dragging Logic
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        // Draw Header (Title Bar)
        context.fill(x, y, x + width, y + height, new Color(10, 10, 10, 255).getRGB());
        // Gradient line at bottom of header
        context.fill(x, y + height - 1, x + width, y + height, new Color(0, 120, 215, 255).getRGB());

        context.drawText(mc.textRenderer, category.name, x + 5, y + 4, -1, false);

        if (open) {
            int offset = height;
            for (Button button : buttons) {
                button.setX(x);
                button.setY(y + offset);
                button.render(context, mouseX, mouseY, delta);
                offset += button.getHeight();
            }
            // Draw border around the list
            // context.drawBorder(x, y + height, width, offset - height, new Color(20, 20, 20, 255).getRGB());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                dragging = true;
                dragX = (int)mouseX - x;
                dragY = (int)mouseY - y;
            } else if (button == 1) {
                open = !open;
            }
            return;
        }

        if (open) {
            for (Button btn : buttons) {
                btn.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (open) {
            for (Button btn : buttons) {
                btn.mouseReleased(mouseX, mouseY, button);
            }
        }
    }
}
