package net.ivangeevo.selfsustainable.util;

import net.minecraft.util.math.Vec3d;

public class VectorUtils {

    public static Vec3d tiltVector(Vec3d originalVector, int facing) {
        double x = originalVector.x;
        double y = originalVector.y;
        double z = originalVector.z;

        switch (facing) {
            case 0 -> {
                // j - 1
                y = 1D - y;
                x = 1D - x;
            }
            case 2 -> {
                // k - 1
                double tempZ = 1D - y;
                y = originalVector.z;
                z = tempZ;
            }
            case 3 -> {
                // k + 1
                double tempZ = y;
                y = 1D - z;
                z = tempZ;
            }
            case 4 -> {
                // i - 1
                double tempY = x;
                x = 1D - y;
                y = tempY;
            }
            case 5 -> {
                // i + 1
                double tempY = 1D - x;
                x = y;
                y = tempY;
            }
        }
        return new Vec3d(x, y, z);
    }
}
