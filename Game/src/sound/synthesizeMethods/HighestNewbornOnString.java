package sound.synthesizeMethods;

import other.math.Vector2D;
import sound.Settings;
import sound.SoundAutomata;

import java.util.ArrayList;

public class HighestNewbornOnString implements SynthesizingMethod {

    @Override
    public ArrayList<Vector2D> cellsToPlay(SoundAutomata automata) {
        ArrayList<Vector2D> notes = new ArrayList<>();
        for (int i = 0; i < Settings.gridSize; i++) {
            int highest = -1;
            for (int j = 0; j < Settings.gridSize; j++) {
                if (automata.getBoard().getState(i, j).isChecked() && automata.getNewBornInStep().contains(new Vector2D(i, j))) {
                    if (highest == -1 || j > highest) {
                        highest = j;
                    }
                }
            }
            if (highest == -1) {
                continue;
            }
            notes.add(new Vector2D(i, highest));
        }
        return notes;
    }
}
