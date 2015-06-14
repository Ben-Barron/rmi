package com.benbarron.rmi.common;

import com.benbarron.flow.Flow;

import java.util.function.Consumer;

public interface TaskResult<T> extends Flow<T> {

    default void run(Consumer<T> resultCallback,
                     Consumer<Throwable> exceptionCallback,
                     Runnable completeCallback) {

        subscribe(resultCallback, exceptionCallback, completeCallback);
    }
}
