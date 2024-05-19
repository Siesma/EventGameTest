import other.Vector2D;
import rendering.Window;
import sound.*;
import sound.keyLayouts.KeyLayout;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        (new Window()).run();

        SoundGenerator gen = new SoundGenerator();
        SoundAutomata automata = new SoundAutomata(Settings.gridSize, Settings.gridSize);

//        automata.playAutomate(gen);

        System.exit(1);

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
                if (Settings.synthesizingMethod.cellsToPlay(automata).contains(new Vector2D(i, j))) {
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
