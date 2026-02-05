package com.anakonda.client.module.render;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {
    public HUD() {
        super("HUD", "Heads-up display", Category.RENDER);
        // Register HUD callback immediately or only when enabled
        // For simplicity, we register once and check isEnabled inside
        HudRenderCallback.EVENT.register(this::onRender);
        setEnabled(true);
    }

    private void onRender(DrawContext context, RenderTickCounter tickCounter) {
        if (!isEnabled()) return;

        int x = mc.getWindow().getScaledWidth() - 2;
        int y = 2;
        int height = mc.textRenderer.fontHeight;

        List<Module> enabledModules = ModuleManager.INSTANCE.getModules().stream()
                .filter(Module::isEnabled)
                .filter(m -> !m.getName().equals("HUD"))
                .sorted(Comparator.comparingInt(m -> -mc.textRenderer.getWidth(m.getName())))
                .collect(Collectors.toList());

        int rainbowState = 0;
        for (Module module : enabledModules) {
            String name = module.getName();
            int width = mc.textRenderer.getWidth(name);

            // Rainbow color
            float hue = (System.currentTimeMillis() % 10000) / 10000f + (rainbowState * 0.05f);
            int color = Color.HSBtoRGB(hue, 0.8f, 1.0f);

            context.fill(x - width - 2, y - 1, x, y + height - 1, new Color(0, 0, 0, 100).getRGB());
            context.drawText(mc.textRenderer, name, x - width - 1, y, color, true);

            y += height + 1;
            rainbowState++;
        }
    }
}
