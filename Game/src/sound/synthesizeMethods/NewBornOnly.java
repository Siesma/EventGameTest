package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;
import sound.keyLayouts.AMajorScale;

import java.util.ArrayList;

public class NewBornOnly implements SynthesizingMethod {
    @Override
    public ArrayList<Integer> notesToPlay(SoundAutomata automata) {
        ArrayList<Integer> notes = new ArrayList<>();
        int gridSize = automata.getBoard().getWidth();
        for(Pair p : cellsToPlay(automata)) {
            notes.add(SoundMap.intFromString(SoundMap.getFromMap(Settings.soundLayout, p.x(), p.y()) + SoundMap.findOctave(p.x(), p.y(), gridSize)));
        }
        return notes;
    }

    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
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


        return notes;
    }
}
