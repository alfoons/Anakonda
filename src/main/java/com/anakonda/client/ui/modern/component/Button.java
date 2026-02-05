package com.anakonda.client.ui.modern.component;

import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.NumberSetting;
import com.anakonda.client.module.setting.Setting;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Button extends Component {
    private final Module module;
    private final Frame parent;
    private boolean extended;
    private final List<Component> settingComponents = new ArrayList<>();

    public Button(Module module, Frame parent, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.module = module;
        this.parent = parent;

        // Initialize settings components
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BooleanSetting bs) {
                settingComponents.add(new Checkbox(bs, this, x, 0, width, height));
            } else if (setting instanceof NumberSetting ns) {
                settingComponents.add(new Slider(ns, this, x, 0, width, height));
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int color = module.isEnabled() ? new Color(0, 120, 215, 255).getRGB() : new Color(40, 40, 40, 255).getRGB();
        if (isHovered(mouseX, mouseY)) {
            // Lighten on hover
            if (module.isEnabled()) color = new Color(30, 140, 235, 255).getRGB();
            else color = new Color(60, 60, 60, 255).getRGB();
        }

        context.fill(x, y, x + width, y + height, color);
        context.drawText(mc.textRenderer, module.getName(), x + 5, y + 4, -1, false);

        // Draw extended indicator
        if (!settingComponents.isEmpty()) {
            String indicator = extended ? "-" : "+";
            context.drawText(mc.textRenderer, indicator, x + width - 10, y + 4, -1, false);
        }

        if (extended) {
            int offset = height;
            for (Component comp : settingComponents) {
                comp.setX(x);
                comp.setY(y + offset);
                comp.render(context, mouseX, mouseY, delta);
                offset += comp.getHeight();
            }
        }
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1 && !settingComponents.isEmpty()) {
                extended = !extended;
            }
            return;
        }

        if (extended) {
            for (Component comp : settingComponents) {
                comp.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (extended) {
            for (Component comp : settingComponents) {
                comp.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    @Override
    public int getHeight() {
        int h = height;
        if (extended) {
            for (Component comp : settingComponents) {
                h += comp.getHeight();
            }
        }
        return h;
    }
}
