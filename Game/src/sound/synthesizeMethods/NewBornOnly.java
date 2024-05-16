package sound.synthesizeMethods;

import board.Board;
import board.BooleanState;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;
import sound.keyLayouts.AMajorScale;

import java.util.ArrayList;

public class NewBornOnly implements SynthesizingMethod {
    @Override
    public ArrayList<Integer> notesToPlay(SoundAutomata automata) {
        Board<BooleanState> board = automata.getBoard();
        Board<BooleanState> prevBoard = automata.getPrevBoard();

        int width = board.getWidth();
        int height = board.getHeight();
        ArrayList<Integer> notes = new ArrayList<>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (board.getState(i, j).isChecked()) {
                    if (!prevBoard.getState(i, j).isChecked()) {
                        notes.add(SoundMap.intFromString(SoundMap.getFromMap(Settings.soundLayout, i, j) + SoundMap.findOctave(i, j, board.getWidth())));
                    }
                }
            }
        }


        return notes;
    }
}
