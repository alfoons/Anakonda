package com.anakonda.client.ui.newgui.component;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Panel extends Component {
    private final Category category;
    private final List<ModuleButton> buttons = new ArrayList<>();
    private boolean open = true;
    private boolean dragging;
    private int dragX, dragY;

    public Panel(Category category, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.category = category;

        int offset = height;
        for (Module module : ModuleManager.INSTANCE.getModulesByCategory(category)) {
            buttons.add(new ModuleButton(module, x, y + offset, width, 15, this));
            offset += 15;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Draw Header
        context.fill(x, y, x + width, y + height, new Color(20, 20, 20, 255).getRGB());
        context.drawText(mc.textRenderer, category.name, x + 5, y + 4, -1, false);

        if (open) {
            int offset = height;
            for (ModuleButton button : buttons) {
                button.x = x;
                button.setY(y + offset);
                button.render(context, mouseX, mouseY, delta);
                offset += button.getHeight();
            }
            // Draw border or background for list if needed
        }

        if (dragging) {
            x = (int)mouseX - dragX;
            y = (int)mouseY - dragY;
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
            for (ModuleButton btn : buttons) {
                btn.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        if (open) {
            for (ModuleButton btn : buttons) {
                btn.mouseReleased(mouseX, mouseY, button);
            }
        }
    }
}
