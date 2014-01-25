package com.myzone.utils.math;

import com.myzone.annotations.NotNull;
import com.myzone.utils.UtilityClass;

/**
 * @author myzone
 * @date 25.01.14.
 */
public class Counters extends UtilityClass {

    public static @NotNull <T> Counter<T> min(@NotNull Counter<T> l, @NotNull Counter<T> r) {
        return l.compareTo(r) < 0 ? l : r;
    }

    public static @NotNull <T> Counter<T> max(@NotNull Counter<T> l, @NotNull Counter<T> r) {
        return l.compareTo(r) > 0 ? l : r;
    }

}
