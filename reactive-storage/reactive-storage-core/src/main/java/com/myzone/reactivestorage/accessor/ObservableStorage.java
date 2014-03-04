package com.myzone.reactivestorage.accessor;

import com.myzone.annotations.NotNull;
import com.myzone.utils.tuple.Tuple;

/**
 * @author myzone
 * @date 25.01.14
 */
public interface ObservableStorage<C extends ObservableStorage.ConfigurationTuple> {

    @NotNull C getConfiguration();

    @NotNull <T> ObservableDataAccessor<T> getAccessor(@NotNull Collection<T> collection);

    interface Collection<T> {

        @NotNull String getName();

        @NotNull Class<T> getEntitiesClass();

    }

    interface ConfigurationTuple<T, C extends ConfigurationTuple> extends Tuple<@NotNull Collection<T>, C> {

    }

    enum ConfigurationEnd implements ConfigurationTuple<Object, ConfigurationEnd> {

        END {
            public @Override Collection<Object> get() {
                return null;
            }

            public @Override ConfigurationEnd next() {
                return END;
            }
        }

    }

    interface ConfigurationBuilder<T extends ConfigurationTuple<?, ?>> {

        @NotNull BuildingPhase1<T> use(@NotNull String collectionName);

        @NotNull T build();

        interface BuildingPhase1<T extends ConfigurationTuple<?, ?>> {

            @NotNull <F> ConfigurationBuilder<ConfigurationTuple<F, T>> as(@NotNull Class<F> entityClass);

        }

    }

    interface Binder {

        @NotNull <C extends ConfigurationTuple> ObservableStorage<C> bind(@NotNull C configuration) throws BindFailureException;

        @NotNull Binder to(@NotNull String host);

        class BindFailureException extends Exception {

        }

    }

}
