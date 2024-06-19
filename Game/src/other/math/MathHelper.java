package other.math;

import org.joml.Vector2f;
import rendering.IsoWindow;

import static rendering.IsoWindow.windowSize;

public class MathHelper {

    static public final float map(float value,
                                  float istart,
                                  float istop,
                                  float ostart,
                                  float ostop) {
        return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
    }

    public static Vector2f translateToIsometric (int[] position) {
        float screenX = (position[0] - position[1]) * (IsoWindow.tileSize.x() / 2.0f) + windowSize.x() / 2.0f;
        float screenY = (position[0] + position[1]) * (IsoWindow.tileSize.y() / 2.0f) + windowSize.y() / 2.0f;
        return new Vector2f(screenX, screenY);
    }
}
