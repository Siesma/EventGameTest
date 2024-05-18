package sound.keyLayouts;

import static sound.Settings.*;


public interface KeyLayout {

    String getLayout();

    default String getKey(int x, int y) {
        String[] keySet = getLayout().split(" ");

        x += soundGridOffset.getY();
        y += soundGridOffset.getX();
        x = x;
        y = y;
        //int index = (x-(2*y));
        int index = (y-(2*x));



        while(index < 0) {
            index += keySet.length;
        }
        index %= keySet.length;
        return keySet[index];
    }

    default int getMIDINote () {
        int middleC = 60;

        return 1;
    }

}
