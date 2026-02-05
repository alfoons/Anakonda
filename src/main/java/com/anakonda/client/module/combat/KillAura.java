package com.anakonda.client.module.combat;

import com.anakonda.client.module.Category;
import com.anakonda.client.module.Module;
import com.anakonda.client.module.setting.BooleanSetting;
import com.anakonda.client.module.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {
    private final NumberSetting range = new NumberSetting("Range", 4.0, 3.0, 6.0);
    private final BooleanSetting rotate = new BooleanSetting("Rotate", true);
    // private final BooleanSetting silent = new BooleanSetting("Silent", false); // Not implemented for "legit" feel

    public KillAura() {
        super("KillAura", "Attacks entities around you", Category.COMBAT);
        addSetting(range);
        addSetting(rotate);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Find target
        Entity target = null;
        double minDistance = range.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity living && entity != mc.player) {
                if (entity.distanceTo(mc.player) <= minDistance && !entity.isRemoved() && living.getHealth() > 0) {
                    target = entity;
                    minDistance = entity.distanceTo(mc.player);
                }
            }
        }

        if (target != null) {
            // Rotations (Legit-like: visible client rotation)
            if (rotate.getValue()) {
                faceEntity(target);
            }

            // Attack logic similar to Triggerbot but automatic
            if (mc.player.getAttackCooldownProgress(0.5f) >= 1.0f) {
                // Check if we are actually looking at it or close enough to hit via raycast
                // For simplified legit aura, we attack if close.
                // A stricter check would use crosshairTarget or Raycast.

                // Attack
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    private void faceEntity(Entity target) {
        Vec3d targetPos = target.getEyePos();
        Vec3d playerPos = mc.player.getEyePos();

        double dX = targetPos.x - playerPos.x;
        double dY = targetPos.y - playerPos.y;
        double dZ = targetPos.z - playerPos.z;
        double dist = Math.sqrt(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(MathHelper.atan2(dY, dist) * 180.0 / Math.PI));

        // Smooth rotation could be added here by interpolating current yaw/pitch towards target
        // For now, instant snap (or fast snap)

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
