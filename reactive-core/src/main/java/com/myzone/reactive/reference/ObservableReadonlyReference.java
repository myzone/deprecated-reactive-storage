package com.myzone.reactive.reference;

import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableReadonlyReference<T, E extends ReferenceChangeEvent<T>> extends Observable<T, E> {

    T get();

}
