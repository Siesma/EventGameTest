package sound;

import game.math.Vector2I;
import sound.keyLayouts.*;
import sound.synthesizeMethods.*;

public class Settings {

    public static KeyLayout soundLayout = new AMajorScale();
    public static int soundNoteDuration = 190;
    public static StartPositions startPosition = StartPositions.OSCILLATOR;

    public static final int soundAccuracy = 10;
    public static int gridSize = 9;

    public static Vector2I soundGridOffset = new Vector2I(0, 0);
    public static SynthesizingMethod synthesizingMethod = new NewBornOnly();

}
