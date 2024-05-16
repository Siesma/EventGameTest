import event.EventBus;
import event.events.InformationEvent;
import game.client.Client;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundGenerator;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        EventBus.register(client);
        EventBus.raise(new InformationEvent());
        int dur = 100;
        SoundGenerator gen = new SoundGenerator(dur);
        //gen.playSound("Eb4", 1000);
        int gridSize = 17;
        SoundAutomata board = new SoundAutomata(gridSize, gridSize);
        for (int i = 0; i < 100; i++) {
            gen.playBoard(board);
            Thread.sleep(dur);
            board.step();
        }

    }
}
