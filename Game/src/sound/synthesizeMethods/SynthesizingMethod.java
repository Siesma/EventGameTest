package sound.synthesizeMethods;

import board.Board;
import other.Pair;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;
import sound.keyLayouts.KeyLayout;

import java.util.ArrayList;

public interface SynthesizingMethod {

    default ArrayList<Integer> notesToPlay(SoundAutomata automata) {
        ArrayList<Integer> notes = new ArrayList<>();
        int grizdSize = automata.getBoard().getWidth();
        for (Pair p : cellsToPlay(automata)) {
            notes.add(SoundMap.intFromString(SoundMap.getFromMap(Settings.soundLayout, p.x(), p.y()) + SoundMap.findOctave(p.x(), p.y(), grizdSize)));
        }
        return notes;

    }

    ArrayList<Pair> cellsToPlay(SoundAutomata automata);

}
