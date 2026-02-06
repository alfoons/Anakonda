package com.anakonda.client.ui.modern.component;

import com.anakonda.client.module.setting.ModeSetting;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class ModeSelector extends Component {
    private final ModeSetting setting;
    private final Component parent;

    public ModeSelector(ModeSetting setting, Component parent, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.setting = setting;
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(x, y, x + width, y + height, new Color(35, 35, 35, 255).getRGB());
        String text = setting.getName() + ": " + setting.getValue();
        context.drawText(mc.textRenderer, text, x + 5, y + 4, -1, false);
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY) && button == 0) {
            setting.cycle();
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }
}
