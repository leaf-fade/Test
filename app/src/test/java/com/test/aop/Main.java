package com.test.aop;

import android.arch.lifecycle.LiveData;
import org.junit.Test;

import java.lang.reflect.Proxy;

public class Main implements ILogin{
    private ILogin proxyLogin;

    @Test
    public void main(){
        catalan();
        /**
         * 第一个参数：类加载器
         * 第二个参数：代理对象的目标类
         * 第三个参数：回调处理类
         */
        proxyLogin = (ILogin) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(), new Class[]{ILogin.class}, new MyHandler(this));

        testAddSome();
    }

    public void testAddSome(){
        System.out.println("====正文部分====");
        proxyLogin.toLogin();
    }

    @Override
    public void toLogin() {
        System.out.println("====登录成功====");
    }

    void catalan(){
        int num;
        int[] front = new int[6];
        int[] back = new int[6];
        int counter = 0;
        int i, j , k;
        for(num = 0; num < (1<<11); num++){
            if((num&1) == 0) continue;
            //是否含有六个1?
            if(bitCount(num) == 6){
                i = j = 0;

                for(k = 11; k >=0; k--){
                    //填充前后排
                    if((num & (1<<k)) == 0){
                        //从左到右扫描num，如果碰见的是0，即num&(1<<k)==0,则填充前排对应的位置
                        //因为我们要从右到左扫描num，所以k是从11开始，这样的话1<<11才能落在num的最高位
                        front[i++] = 11-k;
                    }else{
                        //如果碰见的不是是0，即num&(1<<k)!=0,则填充后排对应的位置
                        back[j++] = 11-k;
                    }
                }
                boolean ok = true;
                //前后排填充完毕，开始判断是否符合前排要低于后排的条件
                for(int m = 0; m < 6; m++){
                    if(front[m] > back[m]){
                        ok = false;
                        break;
                    }
                }

                if(ok){
                    logBin(num);
                    counter++;
                }
            }
        }

        System.out.println("TOTAL: " + counter);
    }

    private int bitCount(int n){
        int counter = 0;
        while(n>0){
            n &= (n-1);
            counter++;
        }
        return counter;
    }

    private void logBin(int n){
        System.out.println(Integer.toBinaryString(n));
    }
}
