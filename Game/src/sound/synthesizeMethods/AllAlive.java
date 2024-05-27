package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.math.Vector2D;
import sound.SoundAutomata;

import java.util.ArrayList;

public class AllAlive implements SynthesizingMethod {

    @Override
    public ArrayList<Vector2D> cellsToPlay(SoundAutomata automata) {
        ArrayList<Vector2D> out = new ArrayList<>();
        Board<BooleanState> board = automata.getBoard();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getState(i, j).isChecked()) {
                    out.add(new Vector2D(i, j));
                }
            }
        }
        return out;
    }
}
