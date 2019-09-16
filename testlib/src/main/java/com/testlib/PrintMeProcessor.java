package com.testlib;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.test_annotation.BasePrint;
import com.test_annotation.PrintMe;
import com.testlib.util.Consts;
import com.testlib.util.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import java.io.IOException;
import java.util.Set;

import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(Processor.class)   //注册注解处理器
/**
 * 处理器接收的参数 替代 {@link AbstractProcessor#getSupportedOptions()} 函数
 */
@SupportedOptions(Consts.ARGUMENTS_NAME)
/**
 * 指定使用的Java版本 替代 {@link AbstractProcessor#getSupportedSourceVersion()} 函数
 * 声明我们注解支持的JDK的版本
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**
 * 注册给哪些注解的  替代 {@link AbstractProcessor#getSupportedAnnotationTypes()} 函数
 * 声明我们要处理哪一些注解 该方法返回字符串的集合表示该处理器用于处理哪些注解
 */
@SupportedAnnotationTypes({Consts.ANN_TYPE})
public class PrintMeProcessor extends AbstractProcessor{

    private Types typeUtils;
    private String value;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        typeUtils = processingEnvironment.getTypeUtils();
    }

    public void genPrint(){
        //创建函数的参数
        ParameterSpec parameterSpec = ParameterSpec.builder(String.class, "str")
                .build();
        //创建函数
        //$T  代指 TypeName 类
        //$N  代指变量或方法名称
        //$S  代指字符串
        MethodSpec methodSpec = MethodSpec.methodBuilder("printMe")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(parameterSpec)
                .addStatement("System.out.println($S+str)",value)
                .build();

        TypeSpec typeSpec = TypeSpec.classBuilder("PrintImpl")
                .addModifiers(PUBLIC)
                .addSuperinterface(BasePrint.class)
                .addMethod(methodSpec)
                .build();

        JavaFile file = JavaFile.builder("com.test", typeSpec).build();
        try {
            file.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (!Utils.isEmpty(set)) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(PrintMe.class);
            if (!Utils.isEmpty(elements)) {
                try {
                    System.out.println("===="+elements.toString());
                    dealData(elements);
                    genPrint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    private void dealData(Set<? extends Element> elements) {
        TypeElement activity = processingEnv.getElementUtils().getTypeElement(Consts.ACTIVITY);
        //节点自描述 Mirror
        TypeMirror type_Activity = activity.asType();
        for (Element element : elements) {
            TypeMirror tm = element.asType();
            PrintMe route = element.getAnnotation(PrintMe.class);
            if(typeUtils.isSubtype(tm,type_Activity)){
                value = route.value();
                //因为代码发生在编译前，这是的程序还未运行，所以app中需要使用的静态map是无用的
            }

        }
    }
}

