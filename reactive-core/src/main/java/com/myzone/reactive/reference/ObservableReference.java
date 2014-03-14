package com.myzone.reactive.reference;

import com.myzone.reactive.event.ReferenceChangeEvent;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableReference<T, E extends ReferenceChangeEvent<T>> extends ObservableReadonlyReference<T, E> {

    void set(T o);

}