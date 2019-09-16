package com.rxjava.test.start;

public abstract class StartBase extends Start {
    protected Start start;

    public StartBase(Start start) {
        this.start = start;
    }
}
