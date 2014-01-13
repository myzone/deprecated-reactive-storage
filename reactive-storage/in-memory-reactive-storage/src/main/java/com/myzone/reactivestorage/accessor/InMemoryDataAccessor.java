package com.myzone.reactivestorage.accessor;

import com.google.common.base.Preconditions;
import com.myzone.annotations.NotNull;
import com.myzone.reactive.stream.ObservableStream;
import com.rits.cloning.Cloner;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;

/**
 * @author myzone
 * @date 11.01.14
 */
public class InMemoryDataAccessor<T> implements DataAccessor<T> {

    private static final @NotNull Cloner CLONER = new Cloner();

    private final @NotNull ReadWriteLock dataReadWriteLock;
    private volatile @NotNull IdentityHashMap<@NotNull T, @NotNull Integer> data;

    public InMemoryDataAccessor() {
        dataReadWriteLock = new ReentrantReadWriteLock(true);
        data = new IdentityHashMap<>();
    }

    public @Override @NotNull ObservableStream<@NotNull T> getAll() {
        return null;
    }

    public @Override @NotNull Transaction<T> beginTransaction() {
        return new InMemoryTransaction();
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
                localIdentities.put(dataEntry, CLONER.deepClone(dataEntry));
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
                    if (wasUpdated) {
                        merge(localIdentities.get(updatedObject), updatedObject);
                    }
                });

                data = localData;
            } finally {
                dataReadWriteLock.writeLock().unlock();
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

    protected static <T> void merge(T from, T to) {
        if (!from.getClass().equals(to.getClass())) {
            throw new IllegalArgumentException();
        }

        Field[] fields = to.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                field.set(to, field.get(from));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
