package com.myzone.utils.funcional;

import com.myzone.annotations.NotNull;

import java.util.function.Function;

/**
 * @author myzone
 * @date 02.02.14
 */
public interface Group<T> extends Monoid<T> {

    @NotNull Function<T, T> getInverseFunction();

}
