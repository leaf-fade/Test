package com.okhttp;

import com.okhttp.chain.Interceptor;

import java.util.ArrayList;
import java.util.List;

public class HttpClient {
    //分发器
    private Dispatcher dispatcher;
    //连接池
    private ConnectionPool connectionPool;
    //重试连接次数
    private int retrys;
    //拦截器
    private List<Interceptor> interceptors;

    public HttpClient() {
        this(new Builder());
    }

    public HttpClient(Builder builder){
        dispatcher = builder.dispatcher;
        connectionPool = builder.connectionPool;
        retrys = builder.retrys;
        interceptors = builder.interceptors;
    }

    public static final class Builder{
        Dispatcher dispatcher = new Dispatcher();
        ConnectionPool connectionPool = new ConnectionPool();
        int retrys = 3;
        List<Interceptor> interceptors = new ArrayList<>();

        public Builder retrys(int retrys) {
            this.retrys = retrys;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }
    }

    public Call newCall(Request request){
        return new Call(request,this);
    }

    public int retrys() {
        return retrys;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    public List<Interceptor> interceptors() {
        return interceptors;
    }
}
