package com.anakonda.client.ui.newgui.component;

import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.NumberSetting;
import com.anakonda.client.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class ModuleButton extends Component {
    private final Module module;
    private final Panel parent;
    private boolean extended;

    public ModuleButton(Module module, int x, int y, int width, int height, Panel parent) {
        super(x, y, width, height);
        this.module = module;
        this.parent = parent;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = module.isEnabled() ? new Color(0, 150, 0, 255).getRGB() : new Color(40, 40, 40, 255).getRGB();
        if (isHovered(mouseX, mouseY)) {
            color = module.isEnabled() ? new Color(0, 180, 0, 255).getRGB() : new Color(60, 60, 60, 255).getRGB();
        }

        context.fill(x, y, x + width, y + height, color);
        context.drawText(mc.textRenderer, module.getName(), x + 5, y + 4, -1, false);

        if (extended) {
            int off = height;
            for (Setting<?> setting : module.getSettings()) {
                int sY = y + off;
                int sH = 15;

                // Background for settings
                context.fill(x, sY, x + width, sY + sH, new Color(30, 30, 30, 255).getRGB());

                String text = setting.getName();
                if (setting instanceof BooleanSetting bs) {
                    text += ": " + (bs.getValue() ? "On" : "Off");
                } else if (setting instanceof NumberSetting ns) {
                    text += ": " + String.format("%.1f", ns.getValue());
                }

                context.drawText(mc.textRenderer, text, x + 5, sY + 4, -1, false);
                off += sH;
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                extended = !extended;
            }
            return;
        }

        if (extended) {
            int off = height;
            for (Setting<?> setting : module.getSettings()) {
                int sY = y + off;
                int sH = 15;
                if (mouseX >= x && mouseX <= x + width && mouseY >= sY && mouseY <= sY + sH) {
                    if (setting instanceof BooleanSetting bs && button == 0) {
                        bs.toggle();
                    }
                    // Implement slider logic for NumberSetting here if needed (requires dragging handling)
                }
                off += sH;
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
    }

    public int getHeight() {
        int h = height;
        if (extended) {
            h += module.getSettings().size() * 15; // Assuming fixed setting height
        }
        return h;
    }
}
