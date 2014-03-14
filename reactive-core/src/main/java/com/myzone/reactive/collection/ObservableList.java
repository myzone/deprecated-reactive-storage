package com.myzone.reactive.collection;

import com.myzone.reactive.event.ListChangeEvent;

import java.util.List;

/**
 * @author myzone
 * @date 03.01.14
 */
public interface ObservableList<T, E extends ListChangeEvent<T>> extends List<T>, ObservableIterable<T, E> {

}
