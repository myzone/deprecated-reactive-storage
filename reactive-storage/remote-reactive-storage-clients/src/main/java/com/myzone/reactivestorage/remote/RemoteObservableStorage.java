package com.myzone.reactivestorage.remote;

import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;
import com.myzone.reactivestorage.accessor.ObservableDataAccessor;
import com.myzone.reactivestorage.accessor.ObservableStorage;
import com.myzone.utils.tuple.ImmutableTuple;

import static com.myzone.reactivestorage.accessor.ObservableStorage.ConfigurationTuple;

/**
 * @author myzone
 * @date 26.01.14
 */
public class RemoteObservableStorage<C extends ConfigurationTuple> implements ObservableStorage<C> {

    private final C configuration;

    protected RemoteObservableStorage(C configuration) {
        this.configuration = configuration;
    }

    public @Override @NotNull C getConfiguration() {
        return configuration;
    }

    public @Override <T> ObservableDataAccessor<T> getAccessor(@NotNull Collection<T> collection) {
        throw new UnsupportedOperationException();
    }

    public static @NotNull ConfigurationBuilder<ConfigurationEnd> configurationBuilder() {
        return new ConfigurationBuilder<>(ConfigurationEnd.END);
    }

    public static @NotNull Binder binder() {
        return new Binder() {
            public @Override @NotNull <C extends ConfigurationTuple> ObservableStorage<C> bind(@NotNull C configuration) throws BindFailureException {
                return new RemoteObservableStorage<>(configuration);
            }

            public @Override @NotNull Binder to(@NotNull String host) {
                return this;
            }
        };
    }

    protected static class ConfigurationBuilder<T extends ConfigurationTuple<?, ?>> implements ObservableStorage.ConfigurationBuilder<T> {

        private final T tuple;

        public ConfigurationBuilder(T tuple) {
            this.tuple = tuple;
        }

        public @Override ObservableStorage.ConfigurationBuilder.BuildingPhase1<T> use(@NotNull String collectionName) {
            return new ObservableStorage.ConfigurationBuilder.BuildingPhase1<T>() {
                public @Override <F> ObservableStorage.ConfigurationBuilder<ConfigurationTuple<F, T>> as(@NotNull Class<F> entityClass) {
                    return new ConfigurationBuilder<>(ImmutableConfigurationTuple.of(ImmutableCollection.of(collectionName, entityClass), tuple));
                }
            };
        }

        public @Override T build() {
            return tuple;
        }

    }

    protected static @Immutable class ImmutableCollection<T> implements Collection<T> {

        private final @NotNull String name;
        private final @NotNull Class<T> entitiesClass;

        private ImmutableCollection(@NotNull String name, @NotNull Class<T> entitiesClass) {
            this.name = name;
            this.entitiesClass = entitiesClass;
        }

        public @Override @NotNull String getName() {
            return name;
        }

        public @Override @NotNull Class<T> getEntitiesClass() {
            return entitiesClass;
        }

        public static @NotNull <C extends ConfigurationTuple, T> ImmutableCollection<T> of(@NotNull String name, @NotNull Class<T> entitiesClass) {
            return new ImmutableCollection<T>(name, entitiesClass);
        }

    }

    protected static class ImmutableConfigurationTuple<T, C extends ConfigurationTuple> extends ImmutableTuple<@NotNull Collection<T>, C> implements ConfigurationTuple<T, C> {

        protected ImmutableConfigurationTuple(Collection<T> data, @NotNull C next) {
            super(data, next);
        }

        public static @NotNull <T, C extends ConfigurationTuple> ImmutableConfigurationTuple<T, C> of(Collection<T> data, @NotNull C next) {
            return new ImmutableConfigurationTuple<>(data, next);
        }

    }

}
