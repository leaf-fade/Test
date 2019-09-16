package com.rxjava.test;

import com.rxjava.test.end.End;
import com.rxjava.test.start.Emitter;
import com.rxjava.test.start.Function;
import com.rxjava.test.start.Num;
import com.rxjava.test.start.Start;

public class test {
    public static void main(String[] args) {
        Start.create(new Num() {
            @Override
            public void subscribe(Emitter emitter) {
                emitter.onNext(2);
            }
        }).map(new Function() {
            @Override
            public int apply(int t) {
                return t*t;
            }
        }).map(new Function() {
            @Override
            public int apply(int t) {
                return 3*t ;
            }
        }).show(new End() {
            @Override
            public void print(int num) {
                System.out.println(num+"");
            }
        });
    }
}
