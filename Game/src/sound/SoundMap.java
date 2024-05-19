package sound;

import other.Pair;
import sound.keyLayouts.KeyLayout;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.LineUnavailableException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SoundMap {

    private static final Map<String, Integer> noteToMidiMap = new HashMap<>();

    static {
        // Initialize the map with the mapping of note names to MIDI note numbers
        String[] notes = {"C", "C#", "Db", "D", "D#", "Eb", "E", "F", "F#", "Gb", "G", "G#", "Ab", "A", "A#", "Bb", "B"};
        int midiNote = 0;
        for (int octave = -1; octave <= 9; octave++) {
            for (String note : notes) {
                noteToMidiMap.put(note + octave, midiNote++);
            }
        }
    }


    public static int findOctave(int x, int y, int n) {

        int middleCOctave = 4;
        Pair startOfMiddleOctave = startOfMiddleOffset;
        int dx = x - startOfMiddleOctave.x();
        int dy = y - startOfMiddleOctave.y();

        int distance = dy-2*dx;
        return middleCOctave + (int) (Math.floor((double) distance / 7));

    }

    private static final Pair startOfMiddleOffset;
    static {
        int mx = 8;
        int my = 8;
        String curNote = Settings.soundLayout.getKey(mx, my);
        while(!(curNote.equals("C") || curNote.equals("C#") || curNote.equals("Cb"))) {
            my--;
            curNote = Settings.soundLayout.getKey(mx, my);

        }
        startOfMiddleOffset = new Pair(mx, my);
    }


    public static String getFromInt(int midi) {
        for (Map.Entry<String, Integer> entry : noteToMidiMap.entrySet()) {
            if (entry.getValue() == midi) {
                return entry.getKey();
            }
        }
        return "-1";

    }

    public static String getFromMap(KeyLayout layout, int x, int y) {
        return layout.getKey(x, y);
    }

    public static int intFromString(String noteName) {
        return noteToMidiMap.getOrDefault(noteName, -1);
    }
}
