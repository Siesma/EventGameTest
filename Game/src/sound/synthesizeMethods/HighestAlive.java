package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.Vector2D;
import sound.SoundAutomata;

import java.util.ArrayList;

public class HighestAlive implements SynthesizingMethod {

    @Override
    public ArrayList<Vector2D> cellsToPlay(SoundAutomata automata) {
        ArrayList<Vector2D> notes = new ArrayList<>();
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
            notes.add(new Vector2D(i, highest));
        }
        return notes;
    }

}
