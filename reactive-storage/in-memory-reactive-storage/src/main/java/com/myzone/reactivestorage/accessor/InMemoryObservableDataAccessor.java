package com.myzone.reactivestorage.accessor;

import com.google.common.base.Preconditions;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.event.ImmutableReferenceChangeEvent;
import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.observable.ObservableHelper;
import com.myzone.reactive.stream.AbstractObservableStream;
import com.myzone.reactive.stream.ObservableStream;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static com.google.common.collect.Iterators.unmodifiableIterator;
import static com.myzone.reactive.observable.Observables.newObservableHelper;
import static java.lang.Thread.currentThread;
import static java.lang.reflect.Modifier.isStatic;

/**
 * @author myzone
 * @date 11.01.14
 */
public class InMemoryObservableDataAccessor<T> implements ObservableDataAccessor<T> {

    private static final Unsafe UNSAFE = getUnsafe();

    private final @NotNull ObservableHelper<T, ReferenceChangeEvent<T>> observableHelper;
    private final @NotNull ReadWriteLock dataReadWriteLock;
    private volatile @NotNull IdentityHashMap<@NotNull T, @NotNull Integer> data;

    public InMemoryObservableDataAccessor() {
        observableHelper = newObservableHelper();
        dataReadWriteLock = new ReentrantReadWriteLock(true);
        data = new IdentityHashMap<>();
    }

    public @Override @NotNull ObservableStream<@NotNull T> getAll() {
        dataReadWriteLock.readLock().lock();
        try {
            IdentityHashMap<@NotNull T, @NotNull Integer> localData = data;

            return new AbstractObservableStream<T>(observableHelper) {
                public @Override Iterator<T> iterator() {
                    return unmodifiableIterator(localData.keySet().iterator());
                }
            };
        } finally {
            dataReadWriteLock.readLock().unlock();
        }
    }

    public @Override @NotNull Transaction<T> beginTransaction() {
        return new InMemoryTransaction();
    }

    protected static <T> T shallowClone(T origin) {
        T copy = newInstance((Class<T>) origin.getClass());

        merge(origin, copy);

        return copy;
    }

    protected static <T> void merge(T from, T to) {
        if (!from.getClass().equals(to.getClass())) {
            throw new IllegalArgumentException();
        }

        Field[] fields = to.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (!isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    field.set(to, field.get(from));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T newInstance(Class<T> clazz) {
        try {
            return (T) UNSAFE.allocateInstance(clazz);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected class InMemoryTransaction implements Transaction<T> {

        private final @NotNull Thread ownerThread;
        private final @NotNull IdentityHashMap<@NotNull T, @NotNull Integer> localData;
        private final @NotNull IdentityHashMap<@NotNull T, @NotNull Boolean> updated;
        private final @NotNull IdentityHashMap<@NotNull T, @NotNull T> localIdentities;

        private boolean closed;

        public InMemoryTransaction() {
            ownerThread = currentThread();

            dataReadWriteLock.readLock().lock();
            try {
                localData = new IdentityHashMap<>(data);
            } finally {
                dataReadWriteLock.readLock().unlock();
            }

            updated = new IdentityHashMap<>();
            localIdentities = new IdentityHashMap<>();
            localData.forEach((dataEntry, version) -> {
                localIdentities.put(dataEntry, shallowClone(dataEntry));
            });
            closed = false;
        }

        public @Override @NotNull T transactional(@NotNull T o) {
            checkState();

            return localIdentities.get(o);
        }

        public @Override @NotNull Stream<@NotNull T> getAll() {
            checkState();

            return localIdentities.keySet().stream();
        }

        public @Override void save(@NotNull T o) {
            checkState();

            localData.put(o, 0);
            updated.put(o, false);
        }

        public @Override void update(@NotNull T o) {
            checkState();

            localData.put(o, localData.get(o) + 1);
            updated.put(o, true);
        }

        public @Override void delete(@NotNull T o) {
            checkState();

            localData.remove(o);
            updated.put(o, false);
        }

        public @Override void commit() throws DataModificationException {
            dataReadWriteLock.writeLock().lock();

            List<ReferenceChangeEvent<T>> eventsToFire = new ArrayList<>();
            try {
                for (T updatedObject : updated.keySet()) {
                    Integer sharedRevision = data.get(updatedObject);
                    Integer localRevision = localData.get(updatedObject);

                    if (localRevision == null) {
                        if (sharedRevision == null) {
                            throw new DataModificationException(); // updatedObject has been removed before
                        }
                    } else {
                        if (sharedRevision != null && localRevision <= sharedRevision) {
                            throw new DataModificationException(); // updatedObject has been updated before
                        }
                    }
                }

                updated.forEach((updatedObject, wasUpdated) -> {
                    @NotNull T oldOne = shallowClone(updatedObject);
                    @NotNull T newOne = localIdentities.get(updatedObject);

                    if (oldOne == null || newOne == null) {
                        Integer version = localData.get(updatedObject);

                        if (Integer.valueOf(0).equals(version)) { // saved, so values should be swapped
                            newOne = oldOne;
                            oldOne = null;
                        }
                    }

                    eventsToFire.add(ImmutableReferenceChangeEvent.of(oldOne, newOne));
                });

                updated.forEach((updatedObject, wasUpdated) -> {
                    if (wasUpdated) {
                        merge(localIdentities.get(updatedObject), updatedObject);
                    }
                });

                data = localData;
            } finally {
                dataReadWriteLock.readLock().lock();
                dataReadWriteLock.writeLock().unlock();
                try {
                    eventsToFire.forEach(observableHelper::fireEvent);
                } finally {
                    dataReadWriteLock.readLock().unlock();
                }
            }

            closed = true;
        }

        public @Override void rollback() {
            closed = true;
        }

        private void checkState() {
            Preconditions.checkState(!closed, "Should be not closed");
            Preconditions.checkState(currentThread().equals(ownerThread), "Should be used in single thread");
        }

    }

}
