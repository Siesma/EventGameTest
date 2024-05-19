package event.events;

import event.Event;
import event.EventBus;
import other.Pair;
import sound.SoundGenerator;

public class SoundUpdateEvent extends Event {

    public SoundUpdateEvent (Pair<?, ?>... content) {
        super(content);
    }

    @Override
    protected String postInformation() {
        return null;
    }
}
