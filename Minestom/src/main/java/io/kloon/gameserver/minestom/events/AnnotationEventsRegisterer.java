package io.kloon.gameserver.minestom.events;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

public class AnnotationEventsRegisterer {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEventsRegisterer.class);

    private final EventNode<? extends Event> eventNode;

    public AnnotationEventsRegisterer() {
        this.eventNode = MinecraftServer.getGlobalEventHandler();
    }

    public AnnotationEventsRegisterer(EventNode<? extends Event> eventNode) {
        this.eventNode = eventNode;
    }

    public void register(Object object) {
        getEventListeners(object).forEach(eventNode::addListener);
    }

    private List<EventListener> getEventListeners(Object object) {
        Class<?> objClass = object.getClass();
        Set<Method> methods = getMethods(objClass);

        List<EventListener> eventListeners = new ArrayList<>(Math.max(2, methods.size() / 2));

        for (Method method : methods) {
            EventHandler annotation = method.getAnnotation(EventHandler.class);
            if (annotation == null) continue;
            Class<?> eventClass;
            if (method.getParameterTypes().length != 1) {
                LOG.error(STR."Attempted to register EventHandler \{method.getName()} in \{objClass} which doesn't have exactly 1 Event parameter");
                continue;
            }
            eventClass = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventClass)) {
                LOG.error(STR."Attempted to register EventHandler with parameter which isn't a Minestom event");
                continue;
            }
            method.setAccessible(true);
            AnnotationEventListener listener = new AnnotationEventListener(eventClass, object, annotation, method);
            eventListeners.add(listener);
        }
        return eventListeners;
    }

    private Set<Method> getMethods(Class<?> listenerClass) {
        Set<Method> methods = new HashSet<>();
        for (Method method : listenerClass.getMethods()) {
            methods.add(method);
        }
        for (Method method : listenerClass.getDeclaredMethods()) {
            methods.add(method);
        }
        return methods;
    }
}
