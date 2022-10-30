package cyborgcabbage.spacepunk.util;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record PlanetProperties(float gravity, boolean hasAtmosphere, Identifier iconPath, int timeDivisor) {
    private static final Map<Identifier, PlanetProperties> map = new HashMap<>() {{
        put(Spacepunk.MOON.getValue(), new PlanetProperties(0.17f, false, Spacepunk.id("textures/gui/planet/moon.png"), 8));
        put(Spacepunk.VENUS.getValue(), new PlanetProperties(0.9f, true, Spacepunk.id("textures/gui/planet/venus.png"), 1));
        put(Spacepunk.BETA_MOON.getValue(), new PlanetProperties(0.17f, false, Spacepunk.id("textures/gui/planet/moon.png"), 8));
        put(Spacepunk.BETA_OVERWORLD.getValue(), new PlanetProperties(1.0f, true, Spacepunk.id("textures/gui/planet/earth.png"), 1));
    }};
    public static PlanetProperties DEFAULT_PROPERTIES = new PlanetProperties(1.0f, true, Spacepunk.id("textures/gui/planet/earth.png"), 1);

    public static float getGravity(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).gravity();
    }

    public static boolean hasAtmosphere(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).hasAtmosphere();
    }
    public static Identifier getIcon(Identifier dimId){
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).iconPath();
    }
    public static int getTimeDivisor(Identifier dimId){
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).timeDivisor();
    }
}
