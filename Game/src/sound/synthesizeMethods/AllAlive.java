package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;
import sound.keyLayouts.AMajorScale;

import java.util.ArrayList;

public class AllAlive implements SynthesizingMethod {


    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
        ArrayList<Pair> out = new ArrayList<>();
        Board<BooleanState> board = automata.getBoard();
        for (int i = 0; i < board.getWidth(); i++) {
            for (int j = 0; j < board.getHeight(); j++) {
                if (board.getState(i, j).isChecked()) {
                    out.add(new Pair(i, j));
                }
            }
        }
        return out;
    }
}
