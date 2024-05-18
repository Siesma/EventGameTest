package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;

import java.util.ArrayList;
import java.util.Set;

public class HighestNewbornOnString implements SynthesizingMethod {

    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
        ArrayList<Pair> notes = new ArrayList<>();
        for (int i = 0; i < Settings.gridSize; i++) {
            int highest = -1;
            for (int j = 0; j < Settings.gridSize; j++) {
                if (automata.getBoard().getState(i, j).isChecked() && automata.getNewBornInStep().contains(new Pair(i, j))) {
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
