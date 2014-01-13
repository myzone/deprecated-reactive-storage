package com.myzone.reactive.observable;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;

/**
 * This interface should be used as observable functionality "engine" only.
 *
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableHelper<T, E extends ChangeEvent<T>> extends Observable<T, E> {

    @Override void addListener(@NotNull ChangeListener<T, ? super E> changeListener);

    @Override void removeListener(@NotNull ChangeListener<T, ? super E> changeListener);

    void fireEvent(@NotNull E changeEvent);

}
