package com.okhttp;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpUrl {


    String protocol;  //协议http  https
    String host;      //192.6.2.3
    String file;    // 文件地址
    int port;     //端口

    /**
     * scheme://host:port/path?query#fragment
     * @param url
     * @throws MalformedURLException
     */
    public HttpUrl(String url) throws MalformedURLException {
        URL url1 = new URL(url);
        host = url1.getHost();
        file = url1.getFile();
        file = TextUtils.isEmpty(file) ? "/" : file;
        protocol = url1.getProtocol();
        port = url1.getPort();
        port = port == -1 ? url1.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }
}
