package cyborgcabbage.spacepunk.util;

import java.util.Random;

public class TangentPlane {
    private final double a;
    private final double b;
    private final double c;
    private final double r;

    public TangentPlane(Random random, double distance) {
        this.a = random.nextDouble(-1.0,1.0);
        this.b = random.nextDouble(-1.0,1.0);
        this.c = random.nextDouble(-1.0,1.0);
        if(distance <= 0.0){
            this.r = 0.0;
        }else{
            this.r = random.nextDouble(distance/2.0,distance)*Math.sqrt(a*a+b*b+c*c);
        }

    }

    public boolean test(double x, double y, double z){
        return x*a+y*b+z*c < r;
    }
}
