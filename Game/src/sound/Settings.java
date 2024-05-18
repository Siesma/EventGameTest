package sound;

import game.math.Vector2I;
import sound.keyLayouts.AMajorScale;
import sound.keyLayouts.CMelodicMinor;
import sound.keyLayouts.CMelodicMinorAsc;
import sound.keyLayouts.KeyLayout;
import sound.synthesizeMethods.*;

public class Settings {

    public static KeyLayout soundLayout = new AMajorScale();
    public static int soundNoteDuration = 125;
    public static StartPositions startPosition = StartPositions.LOAFER;

    public static int gridSize = 17;

    public static Vector2I soundGridOffset = new Vector2I(0, 0);
    public static SynthesizingMethod synthesizingMethod = new NewBornOnly();

}
