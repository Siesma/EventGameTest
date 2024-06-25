package event.events;

import event.Event;
import other.Pair;

public class TimerEvent extends Event {

    public TimerEvent (Pair<?, ?>... content) {
        super(content);
    }

    @Override
    protected String postInformation() {
        return "Timer Event";
    }
}
