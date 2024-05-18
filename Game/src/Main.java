import event.EventBus;
import event.events.InformationEvent;
import game.client.Client;
import other.Pair;
import rendering.Window;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundGenerator;
import sound.StartPositions;
import sound.keyLayouts.AMajorScale;
import sound.keyLayouts.KeyLayout;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        (new Window()).run();


        SoundAutomata automata = new SoundAutomata(17, 17);

        KeyLayout layout = new AMajorScale();

//        automata.printBoard();
//        System.exit(1);

        String cell = "| %-3s %s ";

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 17; j++) {
                System.out.printf(cell, layout.getKey(i, j), automata.getBoard().getState(i, j).getState() ? 1 : 0);
            }
            System.out.println();

            System.out.println("|       ".repeat(17));
            for(int j = 0; j < 17; j++) {
                System.out.print(String.format("| %-2s %-2s ", i, j));
            }
            System.out.println();
            System.out.println("-".repeat(17 * (cell.length() - 2)));
        }


    }
}
