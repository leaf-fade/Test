package com.rxjava.obsever;

import com.rxjava.Disposable;

public interface Observer<T> {
    void onSubscribe(Disposable d);

    void onNext(T t);

    void onError(Throwable e);

    void onComplete();
}
