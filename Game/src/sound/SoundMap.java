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

        int middleCOctave = 3;
        Pair centerPoint = new Pair(n / 2, n / 2); // (8,8)

        int dx = x - centerPoint.x();
        int dy = y - centerPoint.y();

        int numOctavesSkippedX = (int) Math.floor((double) dx / Settings.soundLayout.getLayout().split(" ").length);
        int numOctavesSkippedY = (int) Math.floor((double) dy / Settings.soundLayout.getLayout().split(" ").length);

        int MIDIMiddleC = 60;
        int dMiddleC = 0;

        return middleCOctave + numOctavesSkippedY - 2 * numOctavesSkippedX;

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
