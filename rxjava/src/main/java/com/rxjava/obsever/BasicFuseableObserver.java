package com.rxjava.obsever;

import com.rxjava.Disposable;

//观察者
public abstract class BasicFuseableObserver<T, R> implements Observer<T>, Disposable<R> {
    //观察者
    protected final Observer<? super R> actual;

    protected Disposable disposable;

    public BasicFuseableObserver(Observer<? super R> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe(Disposable d) {
        this.disposable = d;
        actual.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        actual.onError(e);
    }

    @Override
    public void onComplete() {
        actual.onComplete();
    }

    @Override
    public void dispose(boolean bool) {
        disposable.dispose(bool);
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }
}
