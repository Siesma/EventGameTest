package event.events;

import event.Event;

public class InformationEvent extends Event {
    @Override
    public String postInformation() {
        return "Information Event";
    }
}
