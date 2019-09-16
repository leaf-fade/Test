package com.okhttp;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.*;

class Dispatcher {
    //最多同时请求
    private int maxRequests;
    //同一个host同时最多请求
    private int maxRequestsPerHost;

    //线程池，发送异步请求
    private ExecutorService executorService;

    /**
     * 等待执行队列
     */
    private final Deque<Call.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    /**
     * 正在执行队列
     */
    private final Deque<Call.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    public Dispatcher() {
        this(64, 2);
    }

    public Dispatcher(int maxRequests, int maxRequestsPerHost) {
        this.maxRequests = maxRequests;
        this.maxRequestsPerHost = maxRequestsPerHost;
    }

    public synchronized ExecutorService executorService(){
        if(executorService == null){
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull Runnable r) {
                    Thread result = new Thread(r, "okhttp dispatcher");
                    return result;
                }
            };

            executorService = new ThreadPoolExecutor(0,Integer.MAX_VALUE,60, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),threadFactory);
        }
        return executorService;
    }

    public void enqueue(Call.AsyncCall call){
        if(runningAsyncCalls.size() < maxRequests &&
          runningCallsForHost(call)< maxRequestsPerHost){
            runningAsyncCalls.add(call);
            executorService().execute(call);
        }else {
            readyAsyncCalls.add(call);
        }
    }

    /**
     * 同一host 的 同时请求数
     *
     * @param call
     * @return
     */
    private int runningCallsForHost(Call.AsyncCall call) {
        int result = 0;
        //如果执行这个请求，则相同的host数量是result
        for (Call.AsyncCall c : runningAsyncCalls) {
            if (c.host().equals(call.host())) {
                result++;
            }
        }
        return result;
    }

    /*
     *请求结束 移出正在运行队列
     *并判断是否执行等待队列中的请求
     */
    public void finished(Call.AsyncCall asyncCall) {
        synchronized (this) {
            runningAsyncCalls.remove(asyncCall);
            //判断是否执行等待队列中的请求
            promoteCalls();
        }
    }

    /**
     * 判断是否执行等待队列中的请求
     */
    private void promoteCalls() {
        //同时请求达到上限
        if (runningAsyncCalls.size() >= maxRequests) {
            return;
        }
        //没有等待执行请求
        if (readyAsyncCalls.isEmpty()) {
            return;
        }
        for (Iterator<Call.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            Call.AsyncCall call = i.next();
            //同一host同时请求为达上限
            if (runningCallsForHost(call) < maxRequestsPerHost) {
                i.remove();
                runningAsyncCalls.add(call);
                executorService().execute(call);
            }
            //到达同时请求上限
            if (runningAsyncCalls.size() >= maxRequests) {
                return;
            }
        }

    }
}
