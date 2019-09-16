package com.rxjava.test.start;

import com.rxjava.test.end.EndPrint;

public abstract class Start implements StartSource {
    protected int num;

    public static Start create(Num n){
        return new StartCreate(n);
    }

    @Override
    public void show(EndPrint end) {
        showActual(end);
    }

    protected abstract void showActual(EndPrint end);

    public Start map(Function function){
        return new StartMap(this,function);
    }

}
