package com.myzone.reactive.events;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ReferenceChangeEvent<T> extends ChangeEvent<T> {

    T getOld();

    T getNew();

}
