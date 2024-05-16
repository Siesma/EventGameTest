package sound;

import board.Board;
import sound.synthesizeMethods.SynthesizingMethod;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

public class SoundGenerator {

    private SynthesizingMethod method;
    private int evolveDuration;

    private Synthesizer synthesizer;

    private List<MidiChannel> channels;

    public SoundGenerator() {
        this.method = Settings.synthesizingMethod;
        this.evolveDuration = Settings.soundNoteDuration;
        try {
            init();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }


    private void init() throws MidiUnavailableException {
        synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();

        // Get default MIDI channels
        channels = new ArrayList<>();
        MidiChannel[] midiChannels = synthesizer.getChannels();
        for (MidiChannel channel : midiChannels) {
            if (channel != null) {
                channels.add(channel);
            }
        }
        // Set instrument (optional, you can choose different instruments)
        Instrument[] instruments = synthesizer.getDefaultSoundbank().getInstruments();
        synthesizer.loadInstrument(instruments[0]);

    }

    private void destroy() {
        synthesizer.close();
    }

    public void playBoard(SoundAutomata automata) {
        ArrayList<Integer> notesToPlay = method.notesToPlay(automata);
        playSounds(evolveDuration, notesToPlay);
    }

    public void playSounds(String note, int duration) {
        ArrayList<Integer> listOfNotes = new ArrayList<>();

        if (note.matches("^[A-Z][a-z]?(-?[1-9]|0)$")) {
            listOfNotes.add(SoundMap.intFromString(note));
        } else {
            listOfNotes.add(SoundMap.intFromString(note + 4));
        }
        this.playSounds(duration, listOfNotes);
    }

    public void playSounds(String note, int octave, int duration) {
        ArrayList<Integer> listOfNotes = new ArrayList<>();
        listOfNotes.add(SoundMap.intFromString(note + octave));
        this.playSounds(duration, listOfNotes);
    }

    public void playSounds(int duration, List<Integer> notes) {
        try {
            System.out.print("Playing: { ");
            for (int i = 0; i < notes.size(); i++) {
                int note = notes.get(i);
                double frequency = 440.0 * Math.pow(2.0, (note - 69) / 12.0);
                System.out.print(SoundMap.getFromInt(note) + " ");
                channels.get(i % channels.size()).noteOn(note, 100); // Note on
            }
            System.out.println("}");

            // Sleep for the duration
            Thread.sleep(duration);

            // Turn off all notes
            for (int i = 0; i < notes.size(); i++) {
                int note = notes.get(i);
                channels.get(i % channels.size()).noteOff(note); // Note off
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
