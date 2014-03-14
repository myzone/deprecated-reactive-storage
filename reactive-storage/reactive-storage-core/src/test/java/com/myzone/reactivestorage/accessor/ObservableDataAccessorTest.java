package com.myzone.reactivestorage.accessor;

import com.myzone.reactive.collection.ObservableIterable;
import com.myzone.reactive.event.ChangeEvent;
import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.stream.collectors.ObservableCollectors;
import com.myzone.utils.Matchers;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static com.myzone.reactive.observable.Observable.ChangeListener;
import static com.myzone.reactivestorage.accessor.ObservableDataAccessor.DataModificationException;
import static com.myzone.reactivestorage.accessor.ObservableDataAccessor.Transaction;
import static com.myzone.utils.Matchers.TransformationMatcher.namedTransformation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * @author myzone
 * @date 9.7.13
 */
public abstract class ObservableDataAccessorTest {

    private final Supplier<ObservableDataAccessor<?>> dataAccessorFactory;

    private ObservableDataAccessor<MutablePoint> accessor;

    protected ObservableDataAccessorTest(Supplier<ObservableDataAccessor<?>> dataAccessorFactory) {
        this.dataAccessorFactory = dataAccessorFactory;
    }

    @Before
    public void setUp() throws Exception {
        accessor = (ObservableDataAccessor<MutablePoint>) dataAccessorFactory.get();

        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(0, 0));
            transaction.save(new MutablePoint(1, 1));
            transaction.save(new MutablePoint(2, 2));
            transaction.save(new MutablePoint(4, 4));

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();

            throw e;
        }
    }

    @Test
    public void testListeners1() throws Exception {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> ys = accessor.getAll()
                .filter(p -> p.getX() % 2 == 0)
                .map(p -> p.getY())
                .collect(ObservableCollectors.<Integer>toObservableIterable());

        ChangeListener<Integer, ChangeEvent<Integer>> changeListenerMock = mock(ChangeListener.class);
        ys.addListener(changeListenerMock);

        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(5, 5));

            transaction.commit();
        }

        verify(changeListenerMock, never()).onChange(same(ys), any(ChangeEvent.class));

        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(6, 6));

            transaction.commit();
        }

        verify(changeListenerMock).onChange(same(ys), argThat(Matchers.<ReferenceChangeEvent<Integer>>transformationMatcher()
                .with(namedTransformation("getOld()", ReferenceChangeEvent<Integer>::getOld), new IsNull<Integer>())
                .with(namedTransformation("getNew()", ReferenceChangeEvent<Integer>::getNew), new IsEqual<Integer>(6))));
    }


    @Test
    public void testListeners2() throws Exception {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> ys = accessor.getAll()
                .filter(p -> p.getX() % 2 == 0)
                .map(p -> p.getY())
                .collect(ObservableCollectors.<Integer>toObservableIterable());

        ChangeListener<Integer, ChangeEvent<Integer>> changeListenerMock = mock(ChangeListener.class);
        ys.addListener(changeListenerMock);

        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(5, 5));

            transaction.commit();
        }

        verify(changeListenerMock, never()).onChange(same(ys), any(ChangeEvent.class));

        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(6, 6));

            transaction.commit();
        }

        verify(changeListenerMock).onChange(same(ys), argThat(Matchers.<ReferenceChangeEvent<Integer>>transformationMatcher()
                .with(namedTransformation("getOld", ReferenceChangeEvent<Integer>::getOld), new IsNull<Integer>())
                .with(namedTransformation("getNew", ReferenceChangeEvent<Integer>::getNew), new IsEqual<Integer>(6))));
    }

    @Test
    public void testListeners3() throws Exception {
        ObservableIterable<Integer, ReferenceChangeEvent<Integer>> ys = accessor.getAll()
                .filter(p -> p.getX() % 2 == 0)
                .map(p -> p.getY())
                .collect(ObservableCollectors.<Integer>toObservableIterable());

        ChangeListener<Integer, ChangeEvent<Integer>> changeListenerMock = mock(ChangeListener.class);
        ys.addListener(changeListenerMock);


        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(new MutablePoint(5, 5));

            transaction.commit();
        }

        verify(changeListenerMock, never()).onChange(same(ys), any(ChangeEvent.class));

        try (Transaction<MutablePoint> pointTransaction = accessor.beginTransaction()) {
            MutablePoint mutablePoint = pointTransaction.getAll()
                    .filter((t) -> pointTransaction.transactional(t).getX() == 2)
                    .findFirst()
                    .get();

            pointTransaction.transactional(mutablePoint).setY(15);

            pointTransaction.update(mutablePoint);
            pointTransaction.commit();
        }

        verify(changeListenerMock).onChange(same(ys), argThat(Matchers.<ReferenceChangeEvent<Integer>>transformationMatcher()
                .with(namedTransformation("getOld", ReferenceChangeEvent<Integer>::getOld), new IsEqual<Integer>(2))
                .with(namedTransformation("getNew", ReferenceChangeEvent<Integer>::getNew), new IsEqual<Integer>(15))));
    }

    @Test
    public void testMutableDataCorruption1() throws Exception {
        try (Transaction<MutablePoint> transaction1 = accessor.beginTransaction()) {
            MutablePoint mutablePoint = transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 0)
                    .findAny()
                    .get();

            transaction1.transactional(mutablePoint).setX(10);
            transaction1.update(mutablePoint);

            transaction1.commit();
        }

        try (Transaction<MutablePoint> transaction2 = accessor.beginTransaction()) {
            assertEquals(transaction2.getAll()
                    .filter(point -> transaction2.transactional(point).getX() == 10)
                    .count(), 1);
        }
    }

    @Test
    public void testMutableDataCorruption2() throws Exception {
        try (Transaction<MutablePoint> transaction1 = accessor.beginTransaction()) {
            assertEquals(0, transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 10)
                    .count());

            MutablePoint mutablePoint = transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 0)
                    .findAny()
                    .get();

            transaction1.transactional(mutablePoint).setX(10);

            transaction1.commit();
        }

        try (Transaction<MutablePoint> transaction2 = accessor.beginTransaction()) {
            assertEquals(0, transaction2.getAll()
                    .filter(point -> transaction2.transactional(point).getX() == 10)
                    .count());
        }
    }

    @Test
    public void testMutableDataCorruption3() throws Exception {
        try (Transaction<MutablePoint> transaction1 = accessor.beginTransaction()) {
            assertEquals(0, transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 10)
                    .count());

            MutablePoint mutablePoint = transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 0)
                    .findAny()
                    .get();

            transaction1.transactional(mutablePoint).setX(10);
        }

        try (Transaction<MutablePoint> transaction2 = accessor.beginTransaction()) {
            assertEquals(0, transaction2.getAll()
                    .filter(point -> transaction2.transactional(point).getX() == 10)
                    .count());
        }
    }

    @Test
    public void testDataCorruption() throws Exception {
        try (Transaction<MutablePoint> transaction1 = accessor.beginTransaction()) {
            assertEquals(transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 0)
                    .count(), 1);

            MutablePoint mutablePoint = transaction1.getAll()
                    .filter(point -> transaction1.transactional(point).getX() == 0)
                    .findAny()
                    .get();

            transaction1.delete(mutablePoint);

            transaction1.rollback();
        }


        try (Transaction<MutablePoint> transaction2 = accessor.beginTransaction()) {
            assertEquals(transaction2.getAll()
                    .filter(point -> transaction2.transactional(point).getX() == 0)
                    .count(), 1);
        }
    }

    @Test(expected = DataModificationException.class)
    public void testRace() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Transaction<MutablePoint> transaction1 = executor.submit(() -> {
            Transaction<MutablePoint> innerTransaction = accessor.beginTransaction();
            MutablePoint innerMutablePoint = innerTransaction.getAll()
                    .filter(point -> innerTransaction.transactional(point).getX() == 0)
                    .findAny()
                    .get();

            innerTransaction.transactional(innerMutablePoint).setX(10);
            innerTransaction.update(innerMutablePoint);

            return innerTransaction;
        }).get();

        Transaction<MutablePoint> transaction2 = accessor.beginTransaction();
        MutablePoint mutablePoint = transaction2.getAll()
                .filter(point -> transaction2.transactional(point).getX() == 0)
                .findAny()
                .get();

        assertEquals(0, transaction2.transactional(mutablePoint).getX());
        transaction2.transactional(mutablePoint).setX(100);
        transaction2.update(mutablePoint);

        executor.submit(() -> {
            transaction1.commit();
            return null;
        }).get();

        transaction2.commit();
    }

    @Test(expected = DataModificationException.class)
    public void testRaceWithSave() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        MutablePoint mutablePoint = new MutablePoint(10, 10);
        try (Transaction<MutablePoint> transaction = accessor.beginTransaction()) {
            transaction.save(mutablePoint);

            transaction.commit();
        }

        Transaction<MutablePoint> transaction2 = executor.submit(() -> {
            Transaction<MutablePoint> innerTransaction = accessor.beginTransaction();
            innerTransaction.transactional(mutablePoint).setX(101);
            innerTransaction.update(mutablePoint);
            return innerTransaction;
        }).get();

        Transaction<MutablePoint> transaction1 = accessor.beginTransaction();
        assertEquals(10, transaction1.transactional(mutablePoint).getX());
        transaction1.transactional(mutablePoint).setX(100);
        transaction1.update(mutablePoint);

        executor.submit(() -> {
            transaction2.commit();
            return null;
        }).get();

        transaction1.commit();
    }

    @Test
    public void testManyAccessors() throws Exception {
        ObservableDataAccessor<MutablePoint> accessor1 = (ObservableDataAccessor<MutablePoint>) dataAccessorFactory.get();
        ObservableDataAccessor<MutablePoint> accessor2 = (ObservableDataAccessor<MutablePoint>) dataAccessorFactory.get();

        MutablePoint mutablePoint = new MutablePoint(10, 10);

        try (Transaction<MutablePoint> transaction1 = accessor1.beginTransaction()) {
            transaction1.save(mutablePoint);
            transaction1.commit();
        }

        try (Transaction<MutablePoint> transaction2 = accessor2.beginTransaction()) {
            transaction2.save(mutablePoint);
            transaction2.commit();
        }

        try (Transaction<MutablePoint> transaction3 = accessor1.beginTransaction()) {
            transaction3.transactional(mutablePoint).setY(100);
            transaction3.update(mutablePoint);

            try (Transaction<MutablePoint> transaction4 = accessor2.beginTransaction()) {
                assertEquals(transaction4.transactional(mutablePoint).getY(), 10);
            }

            transaction3.commit();
        }

        try (Transaction<MutablePoint> transaction5 = accessor2.beginTransaction()) {
            assertEquals(transaction5.transactional(mutablePoint).getY(), 100);
        }
    }

    protected static class MutablePoint {

        private int x;
        private int y;

        private MutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public @Override boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;

            MutablePoint that = (MutablePoint) o;

            if (x != that.x)
                return false;
            if (y != that.y)
                return false;

            return true;
        }

        public @Override int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        public @Override String toString() {
            return "MutablePoint{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}