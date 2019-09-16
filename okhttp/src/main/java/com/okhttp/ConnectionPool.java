package com.okhttp;

import android.support.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.*;

public class ConnectionPool {
    private final long keepAliveDuration;

    private final Deque<HttpConnection> connections = new ArrayDeque<>();

    private boolean cleanupRunning;

    public ConnectionPool() {
        this(1, TimeUnit.MINUTES);
    }

    public ConnectionPool(long keepAliveDuration, TimeUnit timeUnit) {
        this.keepAliveDuration = timeUnit.toMillis(keepAliveDuration);
    }

    private static  ThreadFactory threadFactory = new ThreadFactory(){

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread result = new Thread(r, "connection pool");
            result.setDaemon(true);
            return result;
        }
    };

    private static  final Executor executor = new ThreadPoolExecutor(0,Integer.MAX_VALUE,60L,TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), threadFactory);

    private final Runnable cleanupRunnable = new Runnable() {
        @Override
        public void run() {
          while(true){
              long waitTimes = cleanup(System.currentTimeMillis());
              if(waitTimes == -1){
                  return;
              }
              if(waitTimes > 0){
                  synchronized (ConnectionPool.this){
                      try {
                          ConnectionPool.this.wait();
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }
                  }
              }

          }
        }
    };

    private long cleanup(long now) {
        long longestIdleDuration = -1;
        synchronized (this){
            for(Iterator<HttpConnection> i = connections.iterator(); i.hasNext();){
                HttpConnection connection = i.next();
                //获得闲置时间，多长时间没使用这个了
                long idleDuration = now - connection.lastUsetime;
                //如果闲置时间超过允许
                if(idleDuration > keepAliveDuration){
                    connection.closeQuietly();
                    i.remove();
                    continue;
                }
                //获得最大闲置时间
                if(longestIdleDuration < idleDuration){
                    longestIdleDuration = idleDuration;
                }
            }
            if(longestIdleDuration >= 0){
                return keepAliveDuration - longestIdleDuration;
            }else {
                cleanupRunning = false;
                return longestIdleDuration;
            }

        }
    }

    public HttpConnection get(String host, int port) {
        Iterator<HttpConnection> iterator = connections.iterator();
        while (iterator.hasNext()) {
            HttpConnection connection = iterator.next();
            //查连接是否复用( 同样的host )
            if (connection.isSameAddress(host, port)) {
                //正在使用的移出连接池
                iterator.remove();
                return connection;
            }
        }
        return null;
    }


    public void put(HttpConnection connection) {
        //执行检测清理
        if (!cleanupRunning) {
            cleanupRunning = true;
            executor.execute(cleanupRunnable);
        }
        connections.add(connection);
    }

}
