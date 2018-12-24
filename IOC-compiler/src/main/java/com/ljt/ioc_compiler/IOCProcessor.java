package com.ljt.ioc_compiler;

import com.google.auto.service.AutoService;
import com.ljt.ioc_annotatation.BindView;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by lijiateng on 2018/12/19.
 */
@AutoService(Processor.class)
public class IOCProcessor extends AbstractProcessor {

    private Filer mFiler;
    private Elements mElementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<>();

        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }

        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        // 所有的注解都会经过此方法
        // element 对应的是所有的注解属性，enclosingElement 包含着对应的类
        // 解析成以 activity 为 key 值，List<Element> 为 value 的 map

        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        LinkedHashMap<Element, List<Element>> hashMap = new LinkedHashMap<>();
        for (Element element : elements) {

            Element key = element.getEnclosingElement();

            List<Element> value = hashMap.get(key);
            if (value != null) {
                value.add(element);
                continue;
            }

            value = new ArrayList<>();
            value.add(element);
            hashMap.put(key, value);

        }

        // 生成代码
        for (Map.Entry<Element, List<Element>> entry : hashMap.entrySet()) {

            Element key = entry.getKey();
            List<Element> value = entry.getValue();

            String className = key.getSimpleName().toString();
            ClassName activityClassName = ClassName.bestGuess(className);
            ClassName unbinderClassName = ClassName.get("com.ljt.ioc", "Unbinder");
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className + "_ViewBinding")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addSuperinterface(unbinderClassName)
                    .addField(activityClassName, "target", Modifier.PRIVATE);

            // unbind 方法
            ClassName callSuper = ClassName.get("android.support.annotation", "CallSuper");
            MethodSpec.Builder unbindMethodBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addAnnotation(callSuper)
                    .addModifiers(Modifier.PUBLIC);
            unbindMethodBuilder.addStatement("$L target = this.target", activityClassName);
            unbindMethodBuilder.addStatement("this.target = null", activityClassName);

            // 构造函数
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addParameter(activityClassName, "target")
                    .addStatement("this.target = target");

            // for 循环寻找注解的属性，进行注入
            for (Element element : value) {
                // target.textView1 = Utils.findViewById(target, R.id.tv_test1);
                String fieldName = element.getSimpleName().toString();
                ClassName utilsName = ClassName.get("com.ljt.ioc", "Utils");

                int resId = element.getAnnotation(BindView.class).value();
                constructorBuilder.addStatement("this.target.$L = $T.findViewById(target, $L)",
                        fieldName, utilsName, resId);

                unbindMethodBuilder.addStatement("target.$L = null", fieldName);

            }

            classBuilder.addMethod(unbindMethodBuilder.build());
            classBuilder.addMethod(constructorBuilder.build());

            // 生成类
            try {
                String packageName = mElementUtils.getPackageOf(key).getQualifiedName().toString();
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("Generated by IOCProcessor, DO NOT modify!")
                        .build()
                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return false;
    }

    public Set<Class<? extends Annotation>> getSupportedAnnotations() {
        LinkedHashSet<Class<? extends Annotation>> classes = new LinkedHashSet<>();
        classes.add(BindView.class);
        return classes;
    }
}
