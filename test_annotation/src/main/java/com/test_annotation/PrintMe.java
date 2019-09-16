package com.test_annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
//注解的生命周期
//RetentionPolicy.SOURCE  源码阶段
//RetentionPolicy.CLASS   编译阶段
//RetentionPolicy.RUNTIME 运行阶段
@Retention(RetentionPolicy.CLASS)
public @interface PrintMe{
    String value();
}