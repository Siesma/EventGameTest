package event.events;

import event.Event;
import other.Pair;

public class MouseReleasedEvent extends Event {


    public MouseReleasedEvent (Pair<?, ?>... content) {
        super(content);
    }

    @Override
    public String postInformation() {
        return "Information Event";
    }


}
