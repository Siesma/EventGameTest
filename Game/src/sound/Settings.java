package sound;

import sound.keyLayouts.AMajorScale;
import sound.keyLayouts.CMelodicMinor;
import sound.keyLayouts.CMelodicMinorAsc;
import sound.keyLayouts.KeyLayout;
import sound.synthesizeMethods.AllAlive;
import sound.synthesizeMethods.HighestAlive;
import sound.synthesizeMethods.NewBornOnly;
import sound.synthesizeMethods.SynthesizingMethod;

public class Settings {

    public static KeyLayout soundLayout = new AMajorScale();

    public static int soundNoteDuration = 100;
    public static StartPositions startPosition = StartPositions.GLIDER;

    public static SynthesizingMethod synthesizingMethod = new HighestAlive();

}
