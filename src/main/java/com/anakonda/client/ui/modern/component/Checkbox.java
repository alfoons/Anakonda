package com.anakonda.client.ui.modern.component;

import com.anakonda.client.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class Checkbox extends Component {
    private final BooleanSetting setting;
    private final Component parent;

    public Checkbox(BooleanSetting setting, Component parent, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, new Color(35, 35, 35, 255).getRGB());
        context.drawText(mc.textRenderer, setting.getName(), x + 5, y + 4, -1, false);

        // Draw check box
        int boxSize = 10;
        int boxX = x + width - 15;
        int boxY = y + (height - boxSize) / 2;

        context.fill(boxX, boxY, boxX + boxSize, boxY + boxSize, new Color(10, 10, 10, 255).getRGB());
        if (setting.getValue()) {
            context.fill(boxX + 2, boxY + 2, boxX + boxSize - 2, boxY + boxSize - 2, new Color(0, 200, 0, 255).getRGB());
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            setting.toggle();
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }
}
