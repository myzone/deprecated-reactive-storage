package com.myzone.utils.async;

import com.google.common.base.Objects;
import com.myzone.annotations.Callback;
import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;
import com.myzone.utils.UtilityClass;
import com.myzone.utils.math.Counter;
import com.myzone.utils.math.Range;

import java.util.Arrays;
import java.util.Iterator;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.myzone.utils.async.AsyncProviders.LoggingAsyncProvider.ProvidedLoggingAction;
import static com.myzone.utils.async.AsyncProviders.LoggingAsyncProvider.RequestLoggingAction;

public class AsyncProviders extends UtilityClass {

    public static @NotNull <T, D> AsyncProvider<T> createDelayedProvider(
            @NotNull BiConsumer<Runnable, D> delayMaker,
            @NotNull D delay,
            @NotNull AsyncProvider<T> asyncProvider
    ) {
        return new DelayedAsyncProvider<>(delayMaker, delay, asyncProvider);
    }
    
    public static @NotNull <T>  AsyncProvider<T> createProviderWithFallback(
            @NotNull AsyncProvider<T> origin,
            @NotNull Predicate<? super T> failurePredicate,
            @NotNull AsyncProvider<T> fallbackAsyncProvider
    ) {
        return new AsyncProviderWithFallback<>(origin, failurePredicate, fallbackAsyncProvider);
    }
    
    public static @NotNull <T, C> AsyncProvider<T> createRetryProvider(
            @NotNull AsyncProvider<T> origin,
            @NotNull Range<C> retriesRange,
            @NotNull Predicate<? super T> failurePredicate,
            @NotNull  AsyncProvider<T> fallbackAsyncProvider
    ) {
        return new RetryAsyncProvider<>(origin, retriesRange, failurePredicate, fallbackAsyncProvider);
    }

    public static @NotNull <T> AsyncProvider<T> createParallelProvider(AsyncProvider<T>... diagnosticsAsyncProviders) {
        return new ParallelAsyncProvider<>(diagnosticsAsyncProviders);
    }

    public static @NotNull <T> AsyncProvider<T> createValueProvider(T value) {
        return new ValueAsyncProvider<>(value);
    }
    
    public static @NotNull <T, C> AsyncProvider<T> createLoggingProvider(
            @NotNull AsyncProvider<T> asyncProvider,
            @NotNull RequestLoggingAction<T, C> onProvide,
            @NotNull ProvidedLoggingAction<T, C> onResult,
            @NotNull Counter<C> operationCounter
    ) {
        return new LoggingAsyncProvider<>(asyncProvider, onProvide, onResult, operationCounter);
    }

    public static @NotNull <T> Function<@NotNull AsyncProvider<T>, @NotNull AsyncProvider<T>> createOnlyFirstProviderFactory() {
        AtomicBoolean provided = new AtomicBoolean(false);
        WeakHashMap<AsyncProvider<T>, AsyncProvider<T>> providersMap = new WeakHashMap<>();

        return from -> providersMap.computeIfAbsent(from, provider -> providedValueConsumer -> {
            if (!provided.get()) {
                provider.provide(providedValue -> {
                    provided.set(true);

                    providedValueConsumer.accept(providedValue);
                });
            }
        });
    }
    
    protected static @Immutable class DelayedAsyncProvider<T, D> implements AsyncProvider<T> {

        protected final @NotNull BiConsumer<@NotNull Runnable, @NotNull D> delayMaker;
        protected final @NotNull D delay;
        protected final @NotNull AsyncProvider<T> asyncProvider;

        public DelayedAsyncProvider(BiConsumer<@NotNull Runnable, @NotNull D> delayMaker, @NotNull D delay, @NotNull AsyncProvider<T> asyncProvider) {
            this.delayMaker = delayMaker;
            this.delay = delay;
            this.asyncProvider = asyncProvider;
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            delayMaker.accept(() -> asyncProvider.provide(providedValueConsumer), delay);
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DelayedAsyncProvider)) return false;

            DelayedAsyncProvider that = (DelayedAsyncProvider) o;

            if (!asyncProvider.equals(that.asyncProvider)) return false;
            if (!delay.equals(that.delay)) return false;
            if (!delayMaker.equals(that.delayMaker)) return false;

            return true;
        }

        public @Override int hashCode() {
            int result = delayMaker.hashCode();
            result = 31 * result + delay.hashCode();
            result = 31 * result + asyncProvider.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("delayMaker", delayMaker)
                    .add("delay", delay)
                    .add("asyncProvider", asyncProvider)
                    .toString();
        }

    }

    protected static @Immutable class AsyncProviderWithFallback<T> implements AsyncProvider<T> {

        protected final @NotNull AsyncProvider<T> originAsyncProvider;
        protected final @NotNull Predicate<? super T> failurePredicate;
        protected final @NotNull AsyncProvider<T> fallbackAsyncProvider;

        public AsyncProviderWithFallback(@NotNull AsyncProvider<T> originAsyncProvider, @NotNull Predicate<? super T> failurePredicate, @NotNull AsyncProvider<T> fallbackAsyncProvider) {
            this.originAsyncProvider = originAsyncProvider;
            this.failurePredicate = failurePredicate;
            this.fallbackAsyncProvider = fallbackAsyncProvider;
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            originAsyncProvider.provide(consumedValue -> {
                if (failurePredicate.test(consumedValue)) {
                    fallbackAsyncProvider.provide(providedValueConsumer);
                } else {
                    providedValueConsumer.accept(consumedValue);
                }
            });
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AsyncProviderWithFallback)) return false;

            AsyncProviderWithFallback that = (AsyncProviderWithFallback) o;

            if (!failurePredicate.equals(that.failurePredicate)) return false;
            if (!fallbackAsyncProvider.equals(that.fallbackAsyncProvider)) return false;
            if (!originAsyncProvider.equals(that.originAsyncProvider)) return false;

            return true;
        }

        public @Override int hashCode() {
            int result = originAsyncProvider.hashCode();
            result = 31 * result + failurePredicate.hashCode();
            result = 31 * result + fallbackAsyncProvider.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("originProvider", originAsyncProvider)
                    .add("failurePredicate", failurePredicate)
                    .add("fallbackProvider", fallbackAsyncProvider)
                    .toString();
        }
        
    }
    
    protected static @Immutable class RetryAsyncProvider<T, C> extends AsyncProviderWithFallback<T> {

        private final @NotNull Range<C> retriesRange;

        public RetryAsyncProvider(@NotNull AsyncProvider<T> origin, @NotNull Range<C> retriesRange, @NotNull Predicate<? super T> failurePredicate, AsyncProvider<T> fallbackAsyncProvider) {
            super(origin, failurePredicate, fallbackAsyncProvider);

            this.retriesRange = retriesRange;
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            Iterator<Counter<C>> retriesIterator = retriesRange.iterator();

            if (retriesIterator.hasNext()) {
                retriesIterator.next();

                originAsyncProvider.provide(new Consumer<T>() {
                    public @Override void accept(T providedValue) {
                        if (failurePredicate.test(providedValue)) {
                            if (retriesIterator.hasNext()) {
                                retriesIterator.next();

                                originAsyncProvider.provide(this);
                            } else {
                                fallbackAsyncProvider.provide(providedValueConsumer);
                            }
                        } else {
                            providedValueConsumer.accept(providedValue);
                        }
                    }
                });
            }
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RetryAsyncProvider)) return false;
            if (!super.equals(o)) return false;

            RetryAsyncProvider that = (RetryAsyncProvider) o;

            return retriesRange.equals(that.retriesRange);
        }

        public @Override int hashCode() {
            int result = super.hashCode();
            result = 31 * result + retriesRange.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("originProvider", originAsyncProvider)
                    .add("retriesRange", retriesRange)
                    .add("failurePredicate", failurePredicate)
                    .add("fallbackProvider", fallbackAsyncProvider)
                    .toString();
        }
        
    }

    protected static @Immutable class ParallelAsyncProvider<T> implements AsyncProvider<T> {

        protected final @NotNull AsyncProvider<T>[] diagnosticsAsyncProviders;

        public ParallelAsyncProvider(AsyncProvider<T>... diagnosticsAsyncProviders) {
            if (diagnosticsAsyncProviders == null || diagnosticsAsyncProviders.length == 0)
                throw new IllegalArgumentException("diagnosticsAsyncProviders can't be empty");

            this.diagnosticsAsyncProviders = diagnosticsAsyncProviders;
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            AtomicBoolean provided = new AtomicBoolean(false);
            Consumer<T> dataConsumer = (providedValue) -> {
                if (!provided.getAndSet(true)) {
                    providedValueConsumer.accept(providedValue);
                }
            };

            for (AsyncProvider<T> diagnosticsAsyncProvider : diagnosticsAsyncProviders) {
                diagnosticsAsyncProvider.provide(dataConsumer);
            }
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ParallelAsyncProvider)) return false;

            ParallelAsyncProvider that = (ParallelAsyncProvider) o;

            return Arrays.equals(diagnosticsAsyncProviders, that.diagnosticsAsyncProviders);
        }

        public @Override int hashCode() {
            return Arrays.hashCode(diagnosticsAsyncProviders);
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("diagnosticsProviders", diagnosticsAsyncProviders)
                    .toString();
        }
        
    }

    protected static @Immutable class ValueAsyncProvider<T> implements AsyncProvider<T> {

        protected final T value;

        public ValueAsyncProvider(T value) {
            this.value = value;
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            providedValueConsumer.accept(value);
        }

        public @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ValueAsyncProvider)) return false;

            ValueAsyncProvider that = (ValueAsyncProvider) o;

            return !(value != null ? !value.equals(that.value) : that.value != null);

        }

        public @Override int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        public @Override String toString() {
            return Objects
                    .toStringHelper(this)
                    .add("value", value)
                    .toString();
        }
        
    }
    
    protected static class LoggingAsyncProvider<T, C> implements AsyncProvider<T> {

        protected final @NotNull AsyncProvider<T> asyncProvider;
        protected final @NotNull RequestLoggingAction<T, C> onProvide;
        protected final @NotNull ProvidedLoggingAction<T, C> onResult;
        protected final @NotNull AtomicReference<Counter<C>> operationCounter;

        public LoggingAsyncProvider(@NotNull AsyncProvider<T> asyncProvider, @NotNull RequestLoggingAction<T, C> onProvide, @NotNull ProvidedLoggingAction<T, C> onResult, @NotNull Counter<C> operationCounter) {
            this.asyncProvider = asyncProvider;
            this.onProvide = onProvide;
            this.onResult = onResult;
            this.operationCounter = new AtomicReference<>(operationCounter);
        }

        public @Override void provide(@NotNull @Callback Consumer<T> providedValueConsumer) {
            Counter<C> operationId = nextOperationId();

            onProvide.log(operationId, asyncProvider, providedValueConsumer);
            
            asyncProvider.provide(consumedValue -> {
                onResult.log(operationId, asyncProvider, providedValueConsumer, consumedValue);

                providedValueConsumer.accept(consumedValue);
            });
        }

        protected Counter<C>  nextOperationId() {
            while (true) {
                Counter<C> currentCounter = operationCounter.get();
                Counter<C> nextCounter = currentCounter.increment();

                if (operationCounter.compareAndSet(currentCounter, nextCounter)) {
                    return currentCounter;
                }
            }
        }

        public  @Override boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LoggingAsyncProvider)) return false;

            LoggingAsyncProvider that = (LoggingAsyncProvider) o;

            if (!asyncProvider.equals(that.asyncProvider)) return false;
            if (!onProvide.equals(that.onProvide)) return false;
            if (!onResult.equals(that.onResult)) return false;
            if (!operationCounter.equals(that.operationCounter)) return false;

            return true;
        }

        public @Override int hashCode() {
            int result = asyncProvider.hashCode();
            result = 31 * result + onProvide.hashCode();
            result = 31 * result + onResult.hashCode();
            result = 31 * result + operationCounter.hashCode();
            return result;
        }

        public @Override String toString() {
            return Objects.toStringHelper(this)
                    .add("asyncProvider", asyncProvider)
                    .add("onProvide", onProvide)
                    .add("onResult", onResult)
                    .add("operationCounter", operationCounter)
                    .toString();
        }

        public @FunctionalInterface interface RequestLoggingAction<T, C> {

            void log(@NotNull Counter<C> operationId, AsyncProvider<T> provider, Consumer<T> consumer);

        }

        public @FunctionalInterface interface ProvidedLoggingAction<T, C> {

            void log(@NotNull Counter<C> operationId, AsyncProvider<T> provider, Consumer<T> consumer, T value);

        }

    }

}
