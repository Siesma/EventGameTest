package sound;

import sound.keyLayouts.KeyLayout;

import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.LineUnavailableException;
import java.util.HashMap;
import java.util.Map;

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


    public static int findOctave (int x, int y, int n) {
        int necessaryOctaves = (n % 7) + 1;
        int bracket = x % 7;
        int octave = bracket + (2 - necessaryOctaves / 2);
        return 2;
    }


    public static String getFromInt (int midi) {
        for (Map.Entry<String, Integer> entry : noteToMidiMap.entrySet()) {
            if (entry.getValue() == midi) {
                return entry.getKey();
            }
        }
        return "-1";

    }

    public static String getFromMap (KeyLayout layout, int x, int y) {
        return layout.getKey(x, y);
    }

    public static int intFromString (String noteName) {
        return noteToMidiMap.getOrDefault(noteName, -1);
    }
}
