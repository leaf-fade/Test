package com.test.handler;

public class Looper {
    static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
    final MessageQueue mQueue;

    public Looper() {
        this.mQueue = new MessageQueue();
    }

    public static void prepare(){
        if(sThreadLocal.get() != null){
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop(){
        MessageQueue queue = myLooper().mQueue;
        while (true){
            Message msg = queue.next();
            if(msg!=null){
                msg.target.dispatchMessage(msg);
            }
        }
    }
}
