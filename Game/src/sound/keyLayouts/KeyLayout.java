package sound.keyLayouts;

import java.util.HashMap;

import static sound.Settings.*;


public abstract class KeyLayout {

    public abstract String getLayout();

    public String getKey(int x, int y) {
        String[] keySet = getLayout().split(" ");

        x += soundGridOffset.getY();
        y += soundGridOffset.getX();
        //int index = (x-(2*y));
        int index = (y - (2 * x));


        while (index < 0) {
            index += keySet.length;
        }
        index %= keySet.length;
        return keySet[index];
    }

    public int getMIDINote(int x, int y) {
        int middleC = 60;

        return 1;
    }

    public int[] getStepDifferenceBetweenTwoNotes() {
        String[] keys = getLayout().split(" ");

        int[] out = new int[keys.length];
        for(int i = 0; i < keys.length; i++) {
            out[i] = dStep(keys[i], keys[(i+1) % keys.length]);
        }
        return out;
    }

    private static HashMap<String, Integer> noteToPosition;

    static {
        noteToPosition.put("C", 0);
        noteToPosition.put("C#", 1);
        noteToPosition.put("D", 2);
        noteToPosition.put("D#", 3);
        noteToPosition.put("E", 4);
        noteToPosition.put("F", 5);
        noteToPosition.put("F#", 6);
        noteToPosition.put("G", 7);
        noteToPosition.put("G#", 8);
        noteToPosition.put("A", 9);
        noteToPosition.put("A#", 10);
        noteToPosition.put("B", 11);

    }

    protected int dStep(String keyA, String keyB) {
        return noteToPosition.get(keyB) - noteToPosition.get(keyA);
    }

}
