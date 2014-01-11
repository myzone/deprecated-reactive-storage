package com.myzone.reactive.reference;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.events.ReferenceChangeEvent;
import com.myzone.reactive.observable.AbstractObservable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author myzone
 * @date 30.12.13
 */
public class ConcurrentObservableReference<T> extends AbstractObservable<T, ReferenceChangeEvent<T>> implements ObservableReference<T, ReferenceChangeEvent<T>> {

    private @NotNull final ReadWriteLock readWriteLock;
    private volatile T value;

    public ConcurrentObservableReference() {
        this(null);
    }

    public ConcurrentObservableReference(T initialValue) {
        readWriteLock = new ReentrantReadWriteLock(true);
        value = initialValue;
    }

    public @Override T get() {
        Lock readLock = readWriteLock.readLock();

        readLock.lock();
        try {
            return value;
        } finally {
            readLock.unlock();
        }
    }

    public @Override void set(T newValue) {
        Lock writeLock = readWriteLock.writeLock();

        writeLock.lock();
        try {
            T oldValue = value;
            value = newValue;

            if (oldValue != newValue) {
                fireEvent(new ReferenceChangeEvent<T>() {
                    public @Override T getOld() {
                        return oldValue;
                    }

                    public @Override T getNew() {
                        return newValue;
                    }
                });
            }
        } finally {
            writeLock.unlock();
        }
    }

}
