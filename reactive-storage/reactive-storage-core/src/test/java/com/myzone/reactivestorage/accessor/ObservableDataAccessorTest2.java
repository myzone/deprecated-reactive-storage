package com.myzone.reactivestorage.accessor;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.Assert.assertTrue;

/**
 * @author myzone
 * @date 26.01.14.
 */
public class ObservableDataAccessorTest2 {

    private final Supplier<ObservableDataAccessor<?>> dataAccessorFactory;

    private ObservableDataAccessor<User> usersAccessor;
    private ObservableDataAccessor<Group> groupsAccessor;
    private ObservableDataAccessor<List> listsAccessor;

    private Location l1;
    private Group g1;
    private User u1;

    protected ObservableDataAccessorTest2(Supplier<ObservableDataAccessor<?>> dataAccessorFactory) {
        this.dataAccessorFactory = dataAccessorFactory;
    }

    @Before
    public void setUp() throws Exception {
        l1 = new Location();
        l1.name = "l1";

        g1 = new Group();
        g1.name = "g1";
        g1.users = new ArrayList<>();

        u1 = new User();
        u1.name = "u1";
        u1.location = l1;
        u1.groups = new ArrayList<>();

        g1.users.add(u1);
        u1.groups.add(g1);

        usersAccessor = (ObservableDataAccessor<User>) dataAccessorFactory.get();
        groupsAccessor = (ObservableDataAccessor<Group>) dataAccessorFactory.get();
        listsAccessor = (ObservableDataAccessor<List>) dataAccessorFactory.get();

        try (ObservableDataAccessor.Transaction<User> usersTransaction = usersAccessor.beginTransaction();
             ObservableDataAccessor.Transaction<Group> groupsTransaction = groupsAccessor.beginTransaction();
             ObservableDataAccessor.Transaction<List> listsTransaction = listsAccessor.beginTransaction()) {
            usersTransaction.save(u1);
            groupsTransaction.save(g1);

            listsTransaction.save(u1.groups);
            listsTransaction.save(g1.users);

            listsTransaction.commit();
            groupsTransaction.commit();
            usersTransaction.commit();
        }
    }

    @Test
    public void testContains() throws Exception {
        ObservableDataAccessor.Transaction<User> usersTransaction = usersAccessor.beginTransaction();
        ObservableDataAccessor.Transaction<Group> groupsTransaction = groupsAccessor.beginTransaction();

        assertTrue(groupsTransaction.transactional(g1).users.contains(u1));
    }

    @Test
    public void testRemove() throws Exception {
        ExecutorService executor = newSingleThreadExecutor();

        executor.submit(() -> {
            ObservableDataAccessor.Transaction<User> usersTransaction = usersAccessor.beginTransaction();
            ObservableDataAccessor.Transaction<Group> groupsTransaction = groupsAccessor.beginTransaction();
            ObservableDataAccessor.Transaction<List> listsTransaction = listsAccessor.beginTransaction();

            System.out.println(g1.users);
            System.out.println(listsTransaction.transactional(g1.users));

            assertTrue(listsTransaction.transactional(g1.users).remove(u1));

            System.out.println(g1.users);
            System.out.println(listsTransaction.transactional(g1.users));
        }).get();

        ObservableDataAccessor.Transaction<User> usersTransaction = usersAccessor.beginTransaction();
        ObservableDataAccessor.Transaction<Group> groupsTransaction = groupsAccessor.beginTransaction();
        ObservableDataAccessor.Transaction<List> listsTransaction = listsAccessor.beginTransaction();

        assertTrue(listsTransaction.transactional(g1.users).contains(u1));
        assertTrue(g1.users.contains(u1));
    }


    private static class User {

        public String name;
        public Location location;
        public List<Group> groups;

    }

    private static class Location {

        public String name;

    }

    private static class Group {

        public String name;
        public List<User> users;

    }


}
