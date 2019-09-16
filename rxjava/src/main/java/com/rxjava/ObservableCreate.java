package com.rxjava;

import com.rxjava.obsever.Observer;

public class ObservableCreate<T> extends Observable<T> {
    final ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        CreateEmitter parent = new CreateEmitter(observer);
        observer.onSubscribe(parent);
        try {
            source.subscribe(parent);
        } catch (Exception e) {
            e.printStackTrace();
            parent.onError(e);
        }
    }

    static final class CreateEmitter<T> implements ObservableEmitter<T>, Disposable {
        final Observer<? super T> observer;
        private boolean bool;

        public CreateEmitter(Observer<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void dispose(boolean bool) {
            this.bool = bool;
        }

        @Override
        public boolean isDisposed() {
            return bool;
        }

        @Override
        public void onNext(T value) {
            if(!bool){
                observer.onNext(value); //observer === MapObserver
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if(!bool) {
                observer.onError(throwable);
            }
        }

        @Override
        public void onComplete() {
            if(!bool) {
                observer.onComplete();
            }
        }
    }
}
