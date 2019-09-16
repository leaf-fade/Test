package com.rxjava;

import com.rxjava.obsever.Observer;

public interface ObservableSource<T> {
    void subscribe(Observer observer);
}
