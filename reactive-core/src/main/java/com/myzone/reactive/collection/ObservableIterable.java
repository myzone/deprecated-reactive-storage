package com.myzone.reactive.collection;

import com.myzone.reactive.event.ReferenceChangeEvent;
import com.myzone.reactive.observable.Observable;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ObservableIterable<T, E extends ReferenceChangeEvent<T>> extends Iterable<T>, Observable<T, E> {

}
