package sound;

import board.Board;
import sound.synthesizeMethods.SynthesizingMethod;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SoundGenerator {

    private SynthesizingMethod method;
    private int evolveDuration;
    private Synthesizer synthesizer;
    private List<MidiChannel> channels;
    private final List<ActiveNote> activeNotes = new ArrayList<>();

    public SoundGenerator() {
        this.method = Settings.synthesizingMethod;
        this.evolveDuration = Settings.soundNoteDuration;
        try {
            init();
            startNoteMonitor();
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
        listInstruments(instruments);
        synthesizer.loadInstrument(instruments[3]);
    }

    private void listInstruments (Instrument[] instruments) {
        for (int i = 0; i < instruments.length; i++) {
            Instrument instrument = instruments[i];
            System.out.println(i + " " + instrument.toString());
        }

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
        for (int note : notes) {
            playNote(note, duration);
        }
    }

    public void playNote(int note, long ttl) {
        synchronized (activeNotes) {
            long expirationTime = System.currentTimeMillis() + ttl;
            activeNotes.add(new ActiveNote(note, expirationTime));

            int channelIndex = activeNotes.size() % channels.size();
            channels.get(channelIndex).noteOn(note, 30); // Note on
        }
    }

    private void startNoteMonitor() {
        Thread noteMonitor = new Thread(() -> {
            try {
                while (true) {
                    long currentTime = System.currentTimeMillis();
                    synchronized (activeNotes) {
                        Iterator<ActiveNote> iterator = activeNotes.iterator();
                        while (iterator.hasNext()) {
                            ActiveNote activeNote = iterator.next();
                            if (currentTime >= activeNote.expirationTime) {
                                int channelIndex = activeNotes.indexOf(activeNote) % channels.size();
                                channels.get(channelIndex).noteOff(activeNote.note); // Note off
                                iterator.remove();
                            }
                        }
                    }
                    Thread.sleep(Settings.soundAccuracy);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        noteMonitor.setDaemon(true);
        noteMonitor.start();
    }

    private static class ActiveNote {
        int note;
        long expirationTime;

        ActiveNote(int note, long expirationTime) {
            this.note = note;
            this.expirationTime = expirationTime;
        }
    }
}
