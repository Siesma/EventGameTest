package sound.synthesizeMethods;

import other.Vector2D;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundMap;

import java.util.ArrayList;

public interface SynthesizingMethod {

    default ArrayList<Integer> notesToPlay(SoundAutomata automata) {
        ArrayList<Integer> notes = new ArrayList<>();
        int grizdSize = automata.getBoard().getWidth();
        for (Vector2D p : cellsToPlay(automata)) {
            notes.add(SoundMap.intFromString(SoundMap.getFromMap(Settings.soundLayout, p.x(), p.y()) + SoundMap.findOctave(p.x(), p.y(), grizdSize)));
        }
        return notes;

    }

    ArrayList<Vector2D> cellsToPlay(SoundAutomata automata);

}
