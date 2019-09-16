package com.okhttp.chain;

import com.okhttp.Response;

import java.io.IOException;

public interface Interceptor {

    Response intercept(InterceptorChain chain) throws IOException;
}
