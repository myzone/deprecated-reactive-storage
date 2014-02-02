package com.myzone.utils.funcional;

import com.myzone.annotations.NotNull;

import java.util.function.BiFunction;

/**
 * @author myzone
 * @date 02.02.14.
 */
public interface CommutativeGroup<T> extends Group<T> {

    /**
     * @return <b>associative<b> and <b>commutative</b> binary function
     */
    @Override @NotNull BiFunction<T, T, T> getFunction();

}
