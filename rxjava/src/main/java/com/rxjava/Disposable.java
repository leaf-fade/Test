package com.rxjava;

public interface Disposable<T> {
    void dispose(boolean bool);

    boolean isDisposed();
}
