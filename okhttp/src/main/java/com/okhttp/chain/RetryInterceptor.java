package com.okhttp.chain;

import com.okhttp.Call;
import com.okhttp.Response;

import java.io.IOException;

public class RetryInterceptor implements Interceptor{


    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Call call = chain.call;
        IOException exception = null;
        for (int i= 0; i < chain.call.client().retrys(); i ++){
            if(call.isCanceled()){
                throw new IOException("Canceled");
            }
            try {
                Response response = chain.proceed();
                return response;
            }catch (IOException e){
                exception = e;
            }
        }
        throw exception;
    }
}
