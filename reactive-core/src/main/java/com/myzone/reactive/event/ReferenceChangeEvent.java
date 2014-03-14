package com.myzone.reactive.event;

/**
 * @author myzone
 * @date 30.12.13
 */
public interface ReferenceChangeEvent<T> extends ChangeEvent<T> {

    T getOld();

    T getNew();

}
