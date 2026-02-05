package com.anakonda.client.ui.modern;

import net.minecraft.client.gui.DrawContext;

public class RenderUtils {
    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    // Placeholder for more advanced rendering if needed (rounded rects etc)
}
