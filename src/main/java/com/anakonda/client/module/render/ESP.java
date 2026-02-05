package com.anakonda.client.module.render;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.awt.Color;

public class ESP extends Module {
    public ESP() {
        super("ESP", "See entities through walls", Category.RENDER);
    }

    // Called from WorldRenderer mixin
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.world == null || mc.player == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                renderBox(context, entity, tickDelta);
            }
        }
    }

    private void renderBox(DrawContext context, Entity entity, float tickDelta) {
        Vec3d pos = entity.getLerpedPos(tickDelta).subtract(mc.gameRenderer.getCamera().getPos());
        Box box = entity.getBoundingBox().offset(pos.subtract(entity.getPos()));

        // We can't easily draw 3D lines with DrawContext directly in 1.21 without proper setup in mixin
        // This method is a placeholder for where the logic resides.
        // Actual rendering usually happens in WorldRenderer.
    }
}
