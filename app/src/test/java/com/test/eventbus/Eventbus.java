package com.test.eventbus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Eventbus {
    private static Eventbus instance = new Eventbus();
    private Map<Object, List<SubscribleMethod>> cacheMap;
    private Handler handler;

    //线程池
    private ExecutorService executorService;

    public static Eventbus getDefault() {
        return instance;
    }

    private Eventbus() {
        this.cacheMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newCachedThreadPool();
    }

    //注册
    public void register(Object subscriber) {
        List<SubscribleMethod> subscribleMethods = cacheMap.get(subscriber);
        if(subscribleMethods == null){
            subscribleMethods = getSubscribleMethods(subscriber);
            cacheMap.put(subscriber, subscribleMethods);
        }

    }


    //获取注册事件的方法列表（@Subscribe）
    private List<SubscribleMethod> getSubscribleMethods(Object subscriber) {
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> list = new ArrayList<>();
        while (aClass != null) {
            //判断分类是在那个报下，（如果是系统的就不需要）
            String name = aClass.getName();
            if (name.startsWith("java.") ||
                    name.startsWith("javax.") ||
                    name.startsWith("android.") ||
                    name.startsWith("androidx.")) {
                break;
            }

            Method[] declaredMethods = aClass.getDeclaredMethods();
            for(Method method : declaredMethods){
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if(annotation == null){
                    continue;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length != 1){
                    throw new RuntimeException("eventbus只能接收一个参数");
                }
                //符合要求
                ThreadMode threadMode = annotation.threadMode();
                SubscribleMethod subscribleMethod = new SubscribleMethod(method, threadMode, parameterTypes[0]);
                list.add(subscribleMethod);

            }

            aClass = aClass.getSuperclass();
        }
        return list;
    }

    //取消注册
    public void unregister(Object subscriber) {
        Class<?> aClass = subscriber.getClass();
        List<SubscribleMethod> list = cacheMap.get(subscriber);
        //如果获取到
        if (list != null) {
            cacheMap.remove(subscriber);
        }
    }

    public void post(final Object obj) {
        Set<Object> set = cacheMap.keySet();
        Iterator<Object> iterator = set.iterator();
        while (iterator.hasNext()){
            final Object next = iterator.next();
            List<SubscribleMethod> list = cacheMap.get(next);
            for (final SubscribleMethod subscribleMethod : list) {
                if(subscribleMethod.getEventType() != obj.getClass()){
                    continue;
                }
                switch (subscribleMethod.getThreadMode()) {
                    case MAIN:
                        //如果接收方法在主线程执行的情况
                        if(Looper.myLooper() == Looper.getMainLooper()){
                            invoke(subscribleMethod, next, obj);
                        } else {
                            //post方法执行在子线程中，接收消息在主线程中
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscribleMethod, next, obj);
                                }
                            });
                        }
                        break;
                    //接收方法在子线程种情况
                    case ASYNC:
                        //post方法执行在主线程中
                        if(Looper.myLooper() == Looper.getMainLooper()){
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(subscribleMethod, next, obj);
                                }
                            });
                        } else {
                            //post方法执行在子线程中
                            invoke(subscribleMethod, next, obj);
                        }
                        break;

                    case POSTING:
                        break;
                }
            }
        }

    }


    private void invoke(SubscribleMethod subscribleMethod, Object next, Object obj) {
        Method method = subscribleMethod.getMethod();
        try {
            method.invoke(next, obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
