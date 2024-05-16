import event.EventBus;
import event.events.InformationEvent;
import game.client.Client;
import rendering.Window;
import sound.Settings;
import sound.SoundAutomata;
import sound.SoundGenerator;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        (new Window()).run();
    }
}
