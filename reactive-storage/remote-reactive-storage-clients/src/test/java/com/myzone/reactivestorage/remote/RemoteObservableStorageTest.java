package com.myzone.reactivestorage.remote;

import com.google.common.base.Objects;
import com.myzone.reactivestorage.accessor.ObservableDataAccessor;
import com.myzone.reactivestorage.accessor.ObservableStorage;
import org.junit.Test;

import static com.myzone.reactivestorage.accessor.ObservableStorage.ConfigurationEnd;
import static com.myzone.reactivestorage.accessor.ObservableStorage.ConfigurationTuple;

/**
 * @author myzone
 * @date 26.01.14
 */
public class RemoteObservableStorageTest {

    @Test
    public void testBinder() throws Exception {
        ConfigurationTuple<Integer, ConfigurationTuple<Foo, ConfigurationTuple<Foo, ConfigurationEnd>>> configuration = RemoteObservableStorage
                .configurationBuilder()
                .use("foo").as(Foo.class)
                .use("foo1").as(Foo.class)
                .use("ids").as(Integer.class)
                .build();

        ObservableStorage<ConfigurationTuple<Integer, ConfigurationTuple<Foo, ConfigurationTuple<Foo, ConfigurationEnd>>>> observableStorage = RemoteObservableStorage
                .binder()
                .to("localhost:9898")
                .bind(configuration);

        ObservableDataAccessor<Foo> foo1Accessor = observableStorage.getAccessor(configuration.next().get());

        try (ObservableDataAccessor.Transaction<Foo> transaction = foo1Accessor.beginTransaction()) {
            transaction.save(new Foo("fuck yeah!!1"));
            transaction.commit();
        }
    }

    private static class Foo {

        private String bar;

        private Foo(String bar) {
            this.bar = bar;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("bar", bar)
                    .toString();
        }
    }

}
