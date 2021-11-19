package com.bus.compiler;

import com.bus.annotation.InjectBus;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author jv.lee
 * @date 2019-07-10
 * @description
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.bus.annotation.InjectBus"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InjectBusProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        messager = processingEnvironment.getMessager();
        filer = processingEnvironment.getFiler();

        messager.printMessage(Diagnostic.Kind.NOTE, "processor init->");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set.isEmpty()) {
            return false;
        }
        //获取所有被InjectBus注解的类节点
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(InjectBus.class);
        for (Element element : elements) {
            //类节点的上一个节点：包节点
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            //获取简单类名
            String className = element.getSimpleName().toString();
            messager.printMessage(Diagnostic.Kind.NOTE, "被@Inject注解的类有：" + className);
            //最终生成的类文件名
            String finalClassName = className + "$Inject";

//            InjectBus aRouter = element.getAnnotation(InjectBus.class);
//
//            MethodSpec methodSpec = MethodSpec.methodBuilder("findTargetClass")
//                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                    .returns(Class.class)
//                    .addParameter(String.class, "path")
//                    .addStatement("return $S.equals(path) ? $T.class : null", aRouter.path(), ClassName.get((TypeElement) element))
//                    .build();
//
//            TypeSpec typeSpec = TypeSpec.classBuilder(finalClassName)
//                    .addModifiers(Modifier.PUBLIC)
//                    .addMethod(methodSpec)
//                    .build();
//
//            JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
//            try {
//                javaFile.writeTo(filer);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        return true;
    }
}