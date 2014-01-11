package com.myzone.reactive.events;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ListChangeEvent<T> extends ReferenceChangeEvent<T> {

    int getIndex();

}
