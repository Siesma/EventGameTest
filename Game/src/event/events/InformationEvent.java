package event.events;

import event.Event;

public class InformationEvent implements Event {
    @Override
    public String postInformation() {
        return "Information Event";
    }
}
