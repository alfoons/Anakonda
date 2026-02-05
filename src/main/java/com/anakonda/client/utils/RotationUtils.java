package com.anakonda.client.utils;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtils {
    public static float[] getRotations(Vec3d start, Vec3d target) {
        double dX = target.x - start.x;
        double dY = target.y - start.y;
        double dZ = target.z - start.z;
        double dist = Math.sqrt(dX * dX + dZ * dZ);

        float yaw = (float) (MathHelper.atan2(dZ, dX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(MathHelper.atan2(dY, dist) * 180.0 / Math.PI));

        return new float[]{yaw, pitch};
    }
}
