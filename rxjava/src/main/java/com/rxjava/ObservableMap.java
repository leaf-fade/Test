package com.rxjava;

import com.rxjava.obsever.BasicFuseableObserver;
import com.rxjava.obsever.Observer;

class ObservableMap<T,U> extends Emitter.AbstractObservableWithUpstream<T, U> {
    final Function<? super T, ? extends U> function;

    public ObservableMap(ObservableSource<T> source, Function<? super T, ? extends U> function) {
        super(source);
        this.function = function;
    }


    @Override
    protected void subscribeActual(Observer<? super U> observer) {
        source.subscribe(new MapObserver<>(observer, function));
    }

    private class MapObserver<T, U> extends BasicFuseableObserver<T, U> {
        final Function<? super T, ? extends U> mapper;

        MapObserver(Observer<? super U> actual, Function<? super T, ? extends U> mapper) {
            super(actual);
            this.mapper = mapper;
        }

        @Override
        public void onNext(T t) {
            U apply = mapper.apply(t);
            actual.onNext(apply);
        }
    }
}
