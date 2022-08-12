package cyborgcabbage.spacepunk.util;

import cyborgcabbage.spacepunk.Spacepunk;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record PlanetProperties(float gravity, boolean hasAtmosphere) {
    private static final Map<Identifier, PlanetProperties> map = new HashMap<>() {{
        put(Spacepunk.MOON.getValue(), new PlanetProperties(0.17f, false));
        put(Spacepunk.VENUS.getValue(), new PlanetProperties(0.9f, true));
    }};
    public static PlanetProperties DEFAULT_PROPERTIES = new PlanetProperties(1.0f, true);

    public static float getGravity(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).gravity();
    }

    public static boolean hasAtmosphere(Identifier dimId) {
        return map.getOrDefault(dimId, DEFAULT_PROPERTIES).hasAtmosphere();
    }
}
