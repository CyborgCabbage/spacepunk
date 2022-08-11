package cyborgcabbage.spacepunk.util;


import net.minecraft.util.math.random.Random;

public class TangentPlane {
    private final double a;
    private final double b;
    private final double c;
    private final double r;

    public TangentPlane(Random random, double distance) {

        this.a = SpaceRandom.d(random,-1.0,1.0);
        this.b = SpaceRandom.d(random,-1.0,1.0);
        this.c = SpaceRandom.d(random,-1.0,1.0);
        if(distance <= 0.0){
            this.r = 0.0;
        }else{
            double d = SpaceRandom.d(random,distance/2.0,distance);
            this.r = d*Math.sqrt(a*a+b*b+c*c);
        }

    }

    public boolean test(double x, double y, double z){
        return x*a+y*b+z*c < r;
    }
}
