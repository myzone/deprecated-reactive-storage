package com.myzone.reactive.observable;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ChangeEvent;

/**
 * This class should be used as observable functionality "engine" only.
 *
 * @author myzone
 * @date 30.12.13
 */
public class ObservableHelper<T, E extends ChangeEvent<T>> extends AbstractObservable<T, E> {

    /**
     * Just makes this method public
     */
    public @Override void fireEvent(@NotNull E changeEvent) {
        super.fireEvent(changeEvent);
    }

}
