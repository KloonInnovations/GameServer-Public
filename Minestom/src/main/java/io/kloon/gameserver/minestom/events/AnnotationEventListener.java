package io.kloon.gameserver.minestom.events;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.trait.CancellableEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class AnnotationEventListener<T extends Event> implements EventListener<T> {
    private static final Logger LOG = LoggerFactory.getLogger(AnnotationEventListener.class);

    private final Class<T> eventClass;
    private final Object object;
    private final EventHandler eventHandler;
    private final Method method;

    public AnnotationEventListener(Class<T> eventClass, Object object, EventHandler eventHandler, Method method) {
        this.eventClass = eventClass;
        this.object = object;
        this.eventHandler = eventHandler;
        this.method = method;
    }

    @Override
    public @NotNull Class<T> eventType() {
        return eventClass;
    }

    @NotNull
    @Override
    public Result run(@NotNull T event) {
        if (eventHandler.ignoreCancelled()) {
            if (event instanceof CancellableEvent cancellable && cancellable.isCancelled()) {
                return Result.INVALID;
            }
        }

        try {
            method.invoke(object, event);
            return Result.SUCCESS;
        } catch (Throwable t) {
            LOG.error("Error running event handler", t);
            return Result.EXCEPTION;
        }
    }
}
