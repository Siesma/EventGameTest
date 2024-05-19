package sound.synthesizeMethods;

import other.Vector2D;
import sound.SoundAutomata;

import java.util.ArrayList;

public class NewBornOnly implements SynthesizingMethod {

    @Override
    public ArrayList<Vector2D> cellsToPlay(SoundAutomata automata) {
        /*
        Board<BooleanState> board = automata.getBoard();
        Board<BooleanState> prevBoard = automata.getPrevBoard();

        int width = board.getWidth();
        int height = board.getHeight();
        ArrayList<Pair> notes = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board.getState(i, j).isChecked()) {
                    if (!prevBoard.getState(i, j).isChecked()) {
                        notes.add(new Pair(i, j));
                    }
                }
            }
        }
        */
        return new ArrayList<>(automata.getNewBornInStep());
    }
}
