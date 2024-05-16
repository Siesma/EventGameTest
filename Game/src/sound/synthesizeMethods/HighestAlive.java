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
    public ArrayList<Integer> notesToPlay(SoundAutomata automata) {
        ArrayList<Integer> notes = new ArrayList<>();
        int grizdSize = automata.getBoard().getWidth();
        for(Pair p : cellsToPlay(automata)) {
            notes.add(SoundMap.intFromString(SoundMap.getFromMap(Settings.soundLayout, p.x(), p.y()) + SoundMap.findOctave(p.x(), p.y(), grizdSize)));
        }
        return notes;
    }

    @Override
    public ArrayList<Pair> cellsToPlay(SoundAutomata automata) {
        ArrayList<Pair> notes = new ArrayList<>();
        Board<BooleanState> board = automata.getBoard();
        for(int j = 0; j < board.getHeight(); j++) {
            int highest = -1;
            for(int i = 0; i < board.getWidth(); i++) {
                if(board.getState(i, j).isChecked()) {
                    if(highest == -1 || i > highest) {
                        highest = i;
                    }
                }
            }
            if(highest == -1) {
                continue;
            }
            notes.add(new Pair(highest, j));
        }
        return notes;
    }

}
