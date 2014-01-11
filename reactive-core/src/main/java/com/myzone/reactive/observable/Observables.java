package com.myzone.reactive.observable;

import com.myzone.annotations.NotNull;
import com.myzone.reactive.utils.DeadListenersCollector;
import com.myzone.utils.UtilityClass;

/**
 * @author myzone
 * @date 03.01.14
 */
public class Observables extends UtilityClass {

    private static @NotNull DeadListenersCollector listenersCollector = new DeadListenersCollector();

    /**
     * DeadListenersCollector is a quite expensive, because of its new thread for processing dead listeners creation.
     * So this method is a way to provide cached one.
     * But you should keep in mind that statics are failure-point of testability and this method should be used carefully.
     *
     * @return cached instance of DeadListenersCollector
     */
    public static @NotNull DeadListenersCollector getListenersCollector() {
        return listenersCollector;
    }

}
