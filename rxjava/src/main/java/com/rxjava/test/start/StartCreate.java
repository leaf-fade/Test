package com.rxjava.test.start;

import com.rxjava.test.end.EndPrint;

public class StartCreate extends Start {

    final Num n;

    public StartCreate(Num n) {
        this.n = n;
    }

    @Override
    protected void showActual(EndPrint end) {

        CreateEmitter parent = new CreateEmitter(end);
        n.subscribe(parent);
    }

    private class CreateEmitter implements Emitter{
        EndPrint end;

        public CreateEmitter(EndPrint end) {
            this.end = end;
        }

        @Override
        public void onNext(int value) {
            System.out.println("========2=======");
            end.print(value);
        }
    }
}
