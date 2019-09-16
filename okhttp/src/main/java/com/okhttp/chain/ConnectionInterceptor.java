package com.okhttp.chain;

import android.util.Log;
import com.okhttp.*;


import java.io.IOException;

public class ConnectionInterceptor implements Interceptor{
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.e("interceprot","连接拦截器....");
        Request request = chain.call.request();
        HttpClient client = chain.call.client();
        HttpUrl url = request.url();
        String host = url.getHost();
        int port = url.getPort();
        HttpConnection httpConnection = client.connectionPool().get(host, port);
        if(httpConnection == null){
            httpConnection = new HttpConnection();
        } else {
            Log.e("call", "使用连接池......");
        }
        httpConnection.setRequest(request);
        Response response = chain.proceed(httpConnection);
        if(response.isKeepAlive()){   //长连接
            client.connectionPool().put(httpConnection);
        }
        return null;
    }
}
