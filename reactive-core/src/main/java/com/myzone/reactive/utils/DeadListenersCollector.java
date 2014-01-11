package com.myzone.reactive.utils;

import com.myzone.annotations.Immutable;
import com.myzone.annotations.NotNull;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

/**
 * @author myzone
 * @date 03.01.14
 */
public class DeadListenersCollector {

    private final ReferenceQueue<Object> dieables;
    private final CopyOnWriteArrayList<UnregisterTask<?, ?>> unregisterTasks;

    public DeadListenersCollector() {
        dieables = new ReferenceQueue<>();
        unregisterTasks = new CopyOnWriteArrayList<>();

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

    public @NotNull <@NotNull L> FluentInterfaceState1<L> collect(@NotNull L listener) {
        return new FluentInterfaceState1<L>() {
            public @Override <T> FluentInterfaceState2<L> afterDeathOf(WeakReference<T> dieable) {
                return new FluentInterfaceState2<L>() {
                    public @Override void via(Consumer<L> unregisterAction) {
                        T strongRef = dieable.get();

                        if (strongRef != null) { // there is no sense to wait until null will garbage collected
                            unregisterTasks.add(new UnregisterTask<L, T>(strongRef, dieables, listener, unregisterAction));
                        }
                    }
                };
            }
        };
    }

    public interface FluentInterfaceState1<@NotNull L> {

        @NotNull <T> FluentInterfaceState2<L> afterDeathOf(@NotNull WeakReference<T> dieable);

    }

    public interface FluentInterfaceState2<@NotNull L> {

        void via(@NotNull Consumer<L> unregisterAction);

    }

    protected static @Immutable class UnregisterTask<L, T> extends PhantomReference<T> implements Runnable {

        private final @NotNull L listener;
        private final @NotNull Consumer<@NotNull L> unregisterAction;

        public UnregisterTask(@NotNull T referent, @NotNull ReferenceQueue<? super T> queue, @NotNull L listener, @NotNull Consumer<@NotNull L> unregisterAction) {
            super(referent, queue);

            this.listener = listener;
            this.unregisterAction = unregisterAction;
        }

        public @Override void run() {
            unregisterAction.accept(listener);
        }

    }

}
