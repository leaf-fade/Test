package com.test.aop;

import android.content.SharedPreferences;
import android.graphics.Bitmap;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MyHandler implements InvocationHandler {

    private Object target;

    public MyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        if(false){
            result = method.invoke(target, args);
        }else {
            System.out.println("============去登录============");
        }
        return result;
    }
}
