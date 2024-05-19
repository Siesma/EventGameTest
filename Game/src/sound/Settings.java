package sound;

import game.math.Vector2I;
import sound.keyLayouts.*;
import sound.synthesizeMethods.*;

public class Settings {

    public static KeyLayout soundLayout = new BMajorPentatonic();
    public static int soundNoteDuration = 500;
    public static StartPositions startPosition = StartPositions.TABLE;

    public static final int soundAccuracy = 100;
    public static int gridSize = 17;

    public static Vector2I soundGridOffset = new Vector2I(0, 0);
    public static SynthesizingMethod synthesizingMethod = new HighestNewbornOnString();

}
