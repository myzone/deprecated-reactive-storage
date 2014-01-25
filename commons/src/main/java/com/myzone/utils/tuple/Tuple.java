package com.myzone.utils.tuple;

import com.myzone.annotations.NotNull;

/**
 * @author myzone
 * @date 9/8/13
 */
public interface Tuple<D, T extends Tuple> {

    D get();

    @NotNull T next();

    enum End implements Tuple<Void, End> {

        END {
            public @Override Void get() {
                return null;
            }

            public @NotNull @Override End next() {
                return this;
            }
        }

    }

}