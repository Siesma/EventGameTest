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

    public static Vector2f translateCoordinateToScreen(int[] position) {
        float screenX = (position[0] - position[1]) * (IsoWindow.tileSize.x() / 2.0f) + windowSize.x() / 2.0f;
        float screenY = (position[0] + position[1]) * (IsoWindow.tileSize.y() / 2.0f) + windowSize.y() / 2.0f;
        return new Vector2f(screenX, screenY);
    }

    public static Vector2f screenToIso(float screenX, float screenY, int tileWidth, int tileHeight) {
        float offsetX = screenX - windowSize.x() / 2.0f;
        float offsetY = screenY - windowSize.y() / 2.0f;

        float isoX = (offsetX / (tileWidth / 2.0f) + offsetY / (tileHeight / 2.0f)) / 2.0f;
        float isoY = (offsetY / (tileHeight / 2.0f) - offsetX / (tileWidth / 2.0f)) / 2.0f;

        float tileX = (float) Math.floor(isoX);
        float tileY = (float) Math.floor(isoY);

        return new Vector2f(tileX, tileY + 1);
    }

}
