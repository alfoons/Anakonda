package com.anakonda.client.ui.modern.component;

import com.anakonda.client.module.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;

public class Slider extends Component {
    private final NumberSetting setting;
    private final Component parent;
    private boolean dragging;

    public Slider(NumberSetting setting, Component parent, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (dragging) {
            double diff = Math.min(width, Math.max(0, mouseX - x));
            double min = setting.getMin();
            double max = setting.getMax();
            double val = (diff / width) * (max - min) + min;
            setting.setValue(Math.round(val * 10.0) / 10.0); // Round to 1 decimal
        }

        context.fill(x, y, x + width, y + height, new Color(35, 35, 35, 255).getRGB());

        // Draw filled part
        double percent = (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin());
        int filledWidth = (int) (width * percent);
        context.fill(x, y, x + filledWidth, y + height, new Color(0, 150, 200, 255).getRGB());

        String text = setting.getName() + ": " + setting.getValue();
        context.drawText(mc.textRenderer, text, x + 5, y + 4, -1, false);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }
}
