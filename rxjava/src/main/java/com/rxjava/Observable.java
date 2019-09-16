package com.rxjava;

import com.rxjava.obsever.Observer;

public abstract class Observable<T> implements ObservableSource<T> {

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source){
        return new ObservableCreate<T>(source);
    }

    @Override
    public void subscribe(Observer observer) {
        subscribeActual(observer);
    }

    protected abstract void subscribeActual(Observer<? super T> observer);

    public <R> Observable<R> map(Function<? super T, ? extends R> function) {
        //传入上一个被观察者
        return new ObservableMap(this, function);
    }
}
