package com.myzone.reactivestorage.accessor;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.stream.ObservableStream;

import java.util.stream.Stream;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface DataAccessor<T> {

    @NotNull ObservableStream<@NotNull T> getAll();

    @NotNull Transaction<T> beginTransaction();

    interface Transaction<T> extends AutoCloseable {

        @NotNull T transactional(@NotNull T o);

        Stream<@NotNull T> getAll();

        void save(@NotNull T o);

        void update(@NotNull T o);

        void delete(@NotNull T o);

        void commit() throws DataModificationException;

        void rollback();

        default @Override void close() {
            rollback();
        }

    }

    class DataModificationException extends Exception {

    }

}
