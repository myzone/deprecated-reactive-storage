package com.myzone.reactivestorage.accessor;

import com.myzone.reactive.stream.ObservableStream;

import java.util.stream.Stream;

/**
 * Created by myzone on 30.12.13.
 */
public interface DataAccessor<T> {

    ObservableStream<T> getAll();

    Transaction<T> beginTransaction();

    interface Transaction<T> extends AutoCloseable {

        T transactional(T o);

        Stream<T> getAll();

        void save(T o);

        void update(T o);

        void delete(T o);

        void commit() throws DataModificationException;

        void rollback();

        default @Override void close() {
            rollback();
        }

    }

    class DataModificationException extends Exception {

    }

}
