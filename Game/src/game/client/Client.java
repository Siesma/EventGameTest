package game.client;

import event.Event;
import event.EventSubscriber;
import event.events.InformationEvent;
import event.events.SoundUpdateEvent;
import game.packet.AbstractPacket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void connectToServer(String hostAddress, int port) {

    }

    @EventSubscriber
    public void handleInformationEvent(InformationEvent e) {
        System.out.println("Handled information packet");
    }


    @EventSubscriber
    public void handleSoundUpdateEvent (SoundUpdateEvent e) {
        for(String s : e.getKeys()) {

        }
    }


    public void handleIncomingData() {
        AbstractPacket packet = decodePacket();



    }

    public AbstractPacket decodePacket() {
        return null;
    }
}
