package cyborgcabbage.spacepunk.util;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record PlanetProperties(float gravity, boolean hasAtmosphere, Identifier iconPath) {
    private static final Map<Identifier, PlanetProperties> map = new HashMap<>() {{
        put(Spacepunk.MOON.getValue(), new PlanetProperties(0.17f, false, Spacepunk.id("textures/gui/planet/moon.png")));
        put(Spacepunk.VENUS.getValue(), new PlanetProperties(0.9f, true, Spacepunk.id("textures/gui/planet/venus.png")));
    }};
    public static PlanetProperties DEFAULT_PROPERTIES = new PlanetProperties(1.0f, true, Spacepunk.id("textures/gui/planet/earth.png"));

    public static float getGravity(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).gravity();
    }

    public static boolean hasAtmosphere(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).hasAtmosphere();
    }

    public static Identifier getIcon(Identifier dimId){
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).iconPath();
    }
}
