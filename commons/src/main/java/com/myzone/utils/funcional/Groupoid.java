package com.myzone.utils.funcional;

import com.myzone.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * @author myzone
 * @date 02.02.14
 */
public interface Groupoid<T> {

    @NotNull BiFunction<T, T, T> getFunction();

}
