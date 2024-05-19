package event;

import other.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class Event {
    private final HashMap<String, Object> content;

    protected abstract String postInformation();

    public Event(Pair<?, ?>... content) {
        this.content = new HashMap<>();
        for (Pair<?, ?> p : content) {
            this.content.put(p.x().toString(), p.y());
        }
    }

    public HashMap<String, Object> getContent() {
        return content;
    }

    public Set<String> getKeys () {
        return this.content.keySet();
    }
}
