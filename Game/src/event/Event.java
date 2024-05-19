package event;

import java.util.HashMap;

public abstract class Event {
    private HashMap<String, Object> content;

    protected abstract String postInformation();

    public Event () {

    }

}
