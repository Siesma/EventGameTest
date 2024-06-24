package event.events;

import event.Event;
public class TimerEvent extends Event {
    @Override
    protected String postInformation() {
        return "Timer Event";
    }
}
