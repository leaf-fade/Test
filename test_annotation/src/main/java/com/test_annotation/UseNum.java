package com.test_annotation;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class UseNum {
    public static void use(Class cls){
        try {
            //反射生成的类进行函数调用
            ((BasePrint) (Class.forName("com.test.PrintImpl").getConstructor().newInstance())).printMe(cls.getSimpleName());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
