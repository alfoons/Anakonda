package com.anakonda.client.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import net.minecraft.client.MinecraftClient;

public class RotationUtils {
    public static float[] getRotations(Vec3d start, Vec3d target) {
        double dX = target.x - start.x;
        double dY = target.y - start.y;
        double dZ = target.z - start.z;
        double dist = Math.sqrt(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(MathHelper.atan2(dY, dist) * 180.0 / Math.PI));

        return applyGCD(new float[]{yaw, pitch});
    }

    // GCD Fix: Rounds rotation to the nearest "step" possible by the player's mouse sensitivity
    private static float[] applyGCD(float[] rotations) {
        MinecraftClient mc = MinecraftClient.getInstance();
        float sensitivity = mc.options.getMouseSensitivity().getValue().floatValue() * 0.6f + 0.2f;
        float gcd = sensitivity * sensitivity * sensitivity * 8.0f * 0.15f;

        rotations[0] = Math.round(rotations[0] / gcd) * gcd;
        rotations[1] = Math.round(rotations[1] / gcd) * gcd;

        return rotations;
    }
}
