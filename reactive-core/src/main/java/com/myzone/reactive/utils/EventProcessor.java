package com.myzone.reactive.utils;

import com.myzone.reactive.events.ChangeEvent;
import com.myzone.reactive.observable.Observable;

import java.util.concurrent.ExecutorService;

import static java.lang.String.format;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author myzone
 * @date 04.03.14.
 */
public class EventProcessor {

   public static EventProcessor getInstance() {
        return Holder.INSTANCE;
   }

    protected final ExecutorService executorService;

    private EventProcessor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public <T, E extends ChangeEvent<T>> void submitEvent(Observable<T, E> observable, Observable.ChangeListener<T, ? super E> listener, E event) {
        executorService.submit(() -> listener.onChange(observable, event));
    }

    protected static class Holder {

        public static final EventProcessor INSTANCE = new EventProcessor(newSingleThreadExecutor(runnable -> new Thread(runnable, format("%s-thread", EventProcessor.class.getSimpleName()))));

    }

}
