package com.myzone.utils.funcional;

import com.myzone.annotations.NotNull;
import com.myzone.utils.UtilityClass;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * @author myzone
 * @date 30.01.14.
 */
public class Monoids extends UtilityClass {

    public static @NotNull Monoid<Lock> monoidLock() {
        return new Monoid<Lock>() {
            public @Override Lock getNeutral() {
                return new ReentrantLock();
            }

            public @Override @NotNull BiFunction<Lock, Lock, Lock> getFunction() {
                return (left, right) -> {
                    Lock tmp = new ReentrantLock();

                    if (left.tryLock()) {
                        if (right.tryLock()) {
                            tmp.lock();
                            right.unlock();
                        } else {
                            left.unlock();
                        }
                    }

                    return tmp;
                };
            }
        };
    }

}
