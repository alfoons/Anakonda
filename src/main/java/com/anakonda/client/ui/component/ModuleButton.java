package com.anakonda.client.ui.component;

import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.awt.Color;

public class ModuleButton {
    private final Module module;
    private final Frame parent;
    private int offset;
    private boolean extended;

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
             if (!module.isEnabled()) color = new Color(80, 80, 80, 255).getRGB();
        }

        context.fill(x, y, x + width, y + height, color);
        context.drawText(MinecraftClient.getInstance().textRenderer, module.getName(), x + 5, y + 4, -1, false);

        if (extended) {
            int settingOffset = height;
            for (Setting<?> setting : module.getSettings()) {
                // Render setting
                int sY = y + settingOffset;
                context.fill(x, sY, x + width, sY + height, new Color(30, 30, 30, 255).getRGB());
                String displayText = setting.getName() + ": " + setting.getValue();
                context.drawText(MinecraftClient.getInstance().textRenderer, displayText, x + 5, sY + 4, -1, false);
                settingOffset += height;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0) {
                module.toggle();
            } else if (button == 1) {
                extended = !extended;
            }
        } else if (extended) {
            int x = parent.getX();
            int y = parent.getY() + offset;
            int height = parent.getHeight();
            int settingOffset = height;

            for (Setting<?> setting : module.getSettings()) {
                if (mouseX >= x && mouseX <= x + parent.getWidth() && mouseY >= y + settingOffset && mouseY <= y + settingOffset + height) {
                    if (setting instanceof BooleanSetting boolSetting && button == 0) {
                        boolSetting.toggle();
                    }
                }
                settingOffset += height;
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

    public int getHeight() {
        int h = parent.getHeight();
        if (extended) {
            h += module.getSettings().size() * parent.getHeight();
        }
        return h;
    }
}
