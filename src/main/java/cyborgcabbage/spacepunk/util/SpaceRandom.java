package cyborgcabbage.spacepunk.util;

import net.minecraft.util.math.random.Random;

public class SpaceRandom {
    public static double d(Random r, double min, double max) {
        double diff = max-min;
        return (r.nextDouble()+min)*diff;
    }
    public static float f(Random r, float min, float max) {
        float diff = max-min;
        return (r.nextFloat()+min)*diff;
    }
}
