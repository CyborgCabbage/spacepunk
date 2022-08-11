package cyborgcabbage.spacepunk.util;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class GravityMap {
    private static final Map<Identifier, Float> map  = new HashMap<>() {{
        put(new Identifier(Spacepunk.MOD_ID, "moon"), 0.17f);
        put(new Identifier(Spacepunk.MOD_ID, "venus"), 0.90f);
    }};

    public static float getGravity(Identifier dimId){
        return map.getOrDefault(dimId, 1.0f);
    }
}
