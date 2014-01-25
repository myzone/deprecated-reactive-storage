package com.myzone.utils.async;

import com.myzone.annotations.Callback;
import com.myzone.annotations.NotNull;

import java.util.function.Consumer;

public interface AsyncProvider<T> {
    
    void provide(@NotNull @Callback Consumer<T> providedValueConsumer);
    
}
