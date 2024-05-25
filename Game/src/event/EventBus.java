package event;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    private static final Map<Class<? extends Event>, List<Subscriber>> eventHandlers = new HashMap<>();

    public static void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventSubscriber.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                    Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];

                    List<Subscriber> subscribers = eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>());
                    subscribers.add(new Subscriber(obj, method));
                }
            }
        }
    }

    public static void raise(Event event) {
        List<Subscriber> subscribers = eventHandlers.get(event.getClass());
        if (subscribers != null) {
            for (Subscriber subscriber : subscribers) {
                try {
                    subscriber.invoke(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class Subscriber {
        private final Object parent;
        private final Method method;

        public Subscriber(Object parent, Method method) {
            this.parent = parent;
            this.method = method;
        }

        public void invoke(Event event) throws Exception {
            method.invoke(parent, event);
        }
    }
}
