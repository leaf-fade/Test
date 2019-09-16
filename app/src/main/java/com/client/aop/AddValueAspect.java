package com.client.aop;

import android.os.SystemClock;
import com.client.aop.AddValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Random;

@Aspect
public class AddValueAspect {

    @Pointcut("execution(@com.client.aop.AddValue *  *(..))")
    public void methodTrace() {
        System.out.println("====================");
    }

    @Around("methodTrace()")
    public Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("===========开始=========");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String value = methodSignature.getMethod().getAnnotation(AddValue.class).value();

        long begin = System.currentTimeMillis();
        //代码执行
        Object result = joinPoint.proceed();
        SystemClock.sleep(new Random().nextInt(2000));
        long duration = System.currentTimeMillis() - begin;
        System.out.println(String.format("%s功能：%s类的%s方法执行了，用时%d ms", value, className, methodName, duration));
        return result;
    }
}
