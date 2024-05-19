import event.EventBus;
import event.events.InformationEvent;
import game.client.Client;
import other.Pair;
import rendering.Window;
import sound.*;
import sound.keyLayouts.AMajorScale;
import sound.keyLayouts.KeyLayout;

import java.util.ArrayList;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        (new Window()).run();


        int x = 1;
        int cx = 8;
        int dx = x - cx;
        double curOctave = 4 + Math.floor((double) dx / Settings.soundLayout.getLayout().length());

        System.out.println(curOctave);

        SoundAutomata automata = new SoundAutomata(17, 17);
        System.out.println(automata.getNewBornInStep().toString());
        KeyLayout layout = Settings.soundLayout;
        automata.step();
        automata.step();
        automata.step();
        System.out.println("test:" + SoundMap.findOctave(8, 10, 17));


//        automata.printBoard();
    //    System.exit(1);

        String cell = "| %-3s %s ";

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                int state = 0;
                state = automata.getBoard().getState(i, j).getState() ? 1 : 0;
                if (Settings.synthesizingMethod.cellsToPlay(automata).contains(new Pair(i, j))) {
                    state = 2;
                }
                System.out.printf(cell, layout.getKey(i, j), state);
            }
            System.out.println();

            //System.out.println("|       ".repeat(17));

            for (int j = 0; j < 17; j++) {
                System.out.print(String.format("|  %-4s ", SoundMap.findOctave(i, j, Settings.gridSize)));
            }
            System.out.println();

            for (int j = 0; j < 17; j++) {
                System.out.print(String.format("| %-2s %-2s ", i, j));
            }
            System.out.println();
            System.out.println("-".repeat(17 * (cell.length() - 2)));
        }


    }
}
