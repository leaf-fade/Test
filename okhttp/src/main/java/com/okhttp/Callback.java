package com.okhttp;



public interface Callback {
    //失败
    void onFailure(Call call, Throwable throwable);
    //成功
    void onResponse(Call call, Response response);
}
