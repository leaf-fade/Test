package com.test.handler;

import org.junit.Test;

public class ActivityThread {


    @Test
    public void main(){

        Looper.prepare();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println(msg.obj.toString());
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                message.obj = "大家好！";
                handler.sendMessage(message);
            }
        }).start();


        Looper.loop();
    }
}
