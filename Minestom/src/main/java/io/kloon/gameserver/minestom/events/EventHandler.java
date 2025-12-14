package io.kloon.gameserver.minestom.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    boolean ignoreCancelled() default true; // if true, will NOT run when the event is cancelled
}
