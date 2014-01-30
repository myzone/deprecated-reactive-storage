package com.myzone.utils.funcional;

/**
 * @author myzone
 * @date 30.01.14
 */
public interface Monoid<T> extends SemiGroup<T> {

    T getNeutral();

}
