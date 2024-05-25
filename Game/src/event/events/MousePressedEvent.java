package event.events;

import event.Event;
import other.Pair;

public class MousePressedEvent extends Event {


    public MousePressedEvent (Pair<?, ?>... content) {
        super(content);
    }

    @Override
    public String postInformation() {
        return "Information Event";
    }


}
