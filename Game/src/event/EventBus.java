package event;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {
    private static final Map<Class<? extends Event>, localPair> eventHandlers = new HashMap<>();

    public static void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventSubscriber.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                    Class<? extends Event> eventType = (Class<? extends Event>) parameterTypes[0];
                    eventHandlers.computeIfAbsent(eventType, k -> new localPair(obj)).addMethod(method);
                }
            }
        }
    }

    public static void raise(Event event) {
        localPair pair = eventHandlers.get(event.getClass());
        if (pair != null) {
            for (Method handler : pair.getMethods()) {
                try {
                    handler.invoke(pair.getParent(), event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class localPair {
        private final Object parent;
        private final List<Method> methods;

        public localPair(Object parent) {
            this.parent = parent;
            this.methods = new ArrayList<>();
        }

        public List<Method> getMethods() {
            return methods;
        }

        public Object getParent() {
            return parent;
        }

        public void addMethod(Method method) {
            this.methods.add(method);
        }
    }
}
