package com.rxjava.test.start;

import com.rxjava.test.end.End;
import com.rxjava.test.end.EndPrint;

public class StartMap extends StartBase {

    protected Function function;

    public StartMap(Start start, Function function) {
        super(start);
        this.num = start.num;
        this.function = function;
    }

    @Override
    protected void showActual(EndPrint end) {
        this.start.showActual(new MapEnd(function,end));
    }

    private class MapEnd extends End{
        private Function function;
        private EndPrint end;

        public MapEnd(Function function, EndPrint end) {
            this.function = function;
            this.end = end;
        }

        @Override
        public void print(int num) {
            System.out.println("========1=======");
            end.print(function.apply(num));
        }
    }
}
