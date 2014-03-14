package com.myzone.reactive.observable;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.event.ChangeEvent;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface Observable<T, E extends ChangeEvent<T>> {

    void addListener(@NotNull ChangeListener<T, ? super E> changeListener);

    void removeListener(@NotNull ChangeListener<T, ? super E> changeListener);

    interface ChangeListener<T, E extends ChangeEvent<T>> {

        void onChange(@NotNull Observable<T, ? extends E> source, @NotNull E event);

    }

}