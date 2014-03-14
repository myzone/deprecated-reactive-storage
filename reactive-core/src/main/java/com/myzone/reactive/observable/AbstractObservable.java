package com.myzone.reactive.observable;

import com.google.common.base.Objects;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.event.ChangeEvent;
import com.myzone.reactive.utils.EventProcessor;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Objects.ToStringHelper;

/**
 * @author myzone
 * @date 30.12.13
 */
public abstract class AbstractObservable<T, E extends ChangeEvent<T>> implements Observable<T, E> {

    private final @NotNull CopyOnWriteArrayList<ChangeListener<T, ? super E>> listeners;

    protected AbstractObservable() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public @Override void addListener(@NotNull ChangeListener<T, ? super E> changeListener) {
        synchronized (listeners) {
            listeners.add(changeListener);
        }
    }

    public @Override void removeListener(@NotNull ChangeListener<T, ? super E> changeListener) {
        synchronized (listeners) {
            listeners.remove(changeListener);
        }
    }

    protected void fireEvent(@NotNull E changeEvent) {
        synchronized (listeners) {
            listeners.forEach(listener -> {
                EventProcessor.getInstance().submitEvent(this, listener, changeEvent);
            });
        }
    }

    public @Override @NotNull String toString() {
        return toStringHelper().toString();
    }

    protected @NotNull ToStringHelper toStringHelper() {
        return Objects.toStringHelper(this)
                .add("listeners", listeners);
    }

}
