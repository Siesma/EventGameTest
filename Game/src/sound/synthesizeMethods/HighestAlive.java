package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;
import sound.keyLayouts.AMajorScale;

import java.util.ArrayList;

public class HighestAlive implements SynthesizingMethod {

    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
        ArrayList<Pair> notes = new ArrayList<>();
        Board<BooleanState> board = automata.getBoard();
        for (int i = 0; i < board.getWidth(); i++) {
            int highest = -1;
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getState(i, j).isChecked()) {
                    if (highest == -1 || j > highest) {
                        highest = j;
                    }
                }
            }
            if (highest == -1) {
                continue;
            }
            notes.add(new Pair(i, highest));
        }
        return notes;
    }

}
