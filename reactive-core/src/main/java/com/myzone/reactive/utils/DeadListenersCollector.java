package com.myzone.reactive.utils;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author myzone
 * @date 03.01.14
 */
public class DeadListenersCollector {

    private final ReferenceQueue<Object> dieables;
    private final ConcurrentLinkedQueue<UnregisterTask<?, ?>> unregisterTasks;

    public DeadListenersCollector() {
        dieables = new ReferenceQueue<>();
        unregisterTasks = new ConcurrentLinkedQueue<>();

        newSingleThreadExecutor().submit((Runnable) () -> {
            while (true) {
                try {
                    UnregisterTask<?, ?> unregisterTask = (UnregisterTask<?, ?>) dieables.remove();
                    try {
                        unregisterTask.run();
                    } finally {
                        unregisterTasks.remove(unregisterTask);
                    }
                } catch (Throwable ignored) {
                }
            }
        });
    }

    public <L> FluentInterfaceState1<L> collect(L listener) {
        return new FluentInterfaceState1<L>() {
            @Override
            public <T> FluentInterfaceState2<L> afterDeathOf(WeakReference<T> dieable) {
                return new FluentInterfaceState2<L>() {
                    @Override
                    public void via(Consumer<L> unregisterAction) {
                        T strongRef = dieable.get();

                        if (strongRef != null) { // there is no sense to wait until null will garbage collected
                            unregisterTasks.add(new UnregisterTask<L, T>(strongRef, dieables, listener, unregisterAction));
                        }
                    }
                };
            }
        };
    }

    ;

    public interface FluentInterfaceState1<L> {

        <T> FluentInterfaceState2<L> afterDeathOf(WeakReference<T> dieable);

    }

    public interface FluentInterfaceState2<L> {

        void via(Consumer<L> unregisterAction);

    }

    protected class UnregisterTask<L, T> extends PhantomReference<T> implements Runnable {

        private final L listener;
        private final Consumer<L> unregisterAction;

        public UnregisterTask(T referent, ReferenceQueue<? super T> q, L listener, Consumer<L> unregisterAction) {
            super(referent, q);

            this.listener = listener;
            this.unregisterAction = unregisterAction;
        }

        @Override
        public void run() {
            unregisterAction.accept(listener);
        }

    }

}
