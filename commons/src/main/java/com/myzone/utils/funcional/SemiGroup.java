package com.myzone.utils.funcional;

import com.myzone.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * @author myzone
 * @date 30.01.14.
 */
public interface SemiGroup<T> {

    @NotNull BiFunction<T, T, T> getFunction();

}
