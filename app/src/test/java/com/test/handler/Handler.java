package com.test.handler;

public class Handler {

    private Looper looper;
    private MessageQueue messageQueue;


    public Handler() {
        looper = Looper.myLooper();
        messageQueue= looper.mQueue;
    }

    public void handleMessage(Message msg) {
    }

    public void sendMessage(Message message) {
        //将消息放入消息队列
        enqueueMessage(message);
    }

    private void enqueueMessage(Message message) {
        //赋值当前消息
        message.target = this;
        //将消息传入
        messageQueue.enqueueMessage(message);
    }

    public void dispatchMessage(Message message) {
        handleMessage(message);
    }

}
