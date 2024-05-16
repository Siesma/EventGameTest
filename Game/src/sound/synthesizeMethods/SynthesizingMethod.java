package sound.synthesizeMethods;

import board.Board;
import sound.SoundAutomata;
import sound.keyLayouts.KeyLayout;

import java.util.ArrayList;

public interface SynthesizingMethod {

    ArrayList<Integer> notesToPlay(SoundAutomata board);

}
