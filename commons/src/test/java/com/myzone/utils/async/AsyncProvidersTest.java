package com.myzone.utils.async;

import com.myzone.annotations.Callback;
import com.myzone.annotations.NotNull;
import com.myzone.utils.math.IntegerCounter;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.myzone.utils.async.AsyncProviders.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author myzone
 * @date 25.01.14
 */
public class AsyncProvidersTest {

    private AsyncProvider<String> p1 = new AsyncProvider<String>() {
        public @Override void provide(@NotNull @Callback Consumer<String> consumer) {
            consumer.accept("1");
        }
    };
    private AsyncProvider<String> p2 = new AsyncProvider<String>() {
        public @Override void provide(@NotNull @Callback Consumer<String> consumer) {
            consumer.accept("2");
        }
    };
    private AsyncProvider<String> p3 = new AsyncProvider<String>() {
        public @Override void provide(@NotNull @Callback Consumer<String> consumer) {
            consumer.accept("3");
        }
    };

    @Test
    public void testProviderWithFallback1() throws Exception {
        Consumer<String> consumerMock = mock(Consumer.class);

        createProviderWithFallback(p1, (o) -> false, p2).provide(consumerMock);

        verify(consumerMock).accept(eq("1"));
    }

    @Test
    public void testProviderWithFallback2() throws Exception {
        Consumer<String> consumerMock = mock(Consumer.class);

        createProviderWithFallback(p1, (o) -> true, p2).provide(consumerMock);

        verify(consumerMock).accept(eq("2"));
    }

    @Test
    public void testParallelProvider() throws Exception {
        Consumer<String> consumerMock = mock(Consumer.class);

        AsyncProvider<String> p1Spy = spy(p1);
        AsyncProvider<String> p2Spy = spy(p2);

        createParallelProvider(p1Spy, p2Spy).provide(consumerMock);

        verify(consumerMock).accept(eq("1"));
        verify(p1Spy).provide(any(Consumer.class));
        verify(p2Spy).provide(any(Consumer.class));
    }

    @Test
    public void testOnlyFirstProviderFactory() throws Exception {
        Consumer<String> consumerMock = mock(Consumer.class);

        AsyncProvider<String> p1Spy = spy(p1);
        AsyncProvider<String> p2Spy = spy(p2);

        Function<AsyncProvider<String>, AsyncProvider<String>> onlyFirstProviderFactory = createOnlyFirstProviderFactory();

        createParallelProvider(onlyFirstProviderFactory.apply(p1Spy), onlyFirstProviderFactory.apply(p2Spy)).provide(consumerMock);

        verify(consumerMock).accept(eq("1"));
        verify(p1Spy).provide(any(Consumer.class));
        verify(p2Spy, never()).provide(any(Consumer.class));
    }

    @Test
    public void testRetryProvider1() throws Exception {
        AsyncProvider<String> p1Spy = spy(p1);
        AsyncProvider<String> p2Spy = spy(p2);

        createRetryProvider(p1Spy, IntegerCounter.of(0).to(IntegerCounter.of(10)), o -> true, p2Spy).provide(mock(Consumer.class));

        verify(p1Spy, times(10)).provide(any(Consumer.class));
        verify(p2Spy).provide(any(Consumer.class));
    }

    @Test
    public void testRetryProvider2() throws Exception {
        AsyncProvider<String> p1Spy = spy(p1);
        AsyncProvider<String> p2Spy = spy(p2);

        createRetryProvider(p1Spy, IntegerCounter.of(0).to(IntegerCounter.of(10)), o -> false, p2Spy).provide(mock(Consumer.class));

        verify(p1Spy).provide(any(Consumer.class));
        verify(p2Spy, never()).provide(any(Consumer.class));
    }

    @Test
    public void testRetryProvider3() throws Exception {
        AsyncProvider<String> p1Spy = spy(p1);
        AsyncProvider<String> p2Spy = spy(p2);

        AtomicInteger retriesLeft = new AtomicInteger(5);

        createRetryProvider(p1Spy, IntegerCounter.of(0).to(IntegerCounter.of(10)), o -> retriesLeft.decrementAndGet() > 0, p2Spy).provide(mock(Consumer.class));

        verify(p1Spy, times(5)).provide(any(Consumer.class));
        verify(p2Spy, never()).provide(any(Consumer.class));
    }

    @Test
    public void testValueProvider() throws Exception {
        Consumer<String> consumerMock = mock(Consumer.class);

        createValueProvider("2").provide(consumerMock);

        verify(consumerMock).accept(eq("2"));
    }

}
