package org.example.processor.bytebuddy;

import com.google.auto.service.AutoService;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.matcher.ElementMatchers;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

@AutoService(Processor.class)
public class MyAnnotationProcessor extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(MyAnnotation.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : annotatedElements) {
                if (element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    try {
                        enhanceClass(typeElement);
                    } catch (IOException | ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    private void enhanceClass(TypeElement typeElement) throws IOException, ReflectiveOperationException {
        String className = typeElement.getQualifiedName().toString();
        System.out.println("MyAnnotation Processor process" + className);
        Class<?> clazz = Class.forName(className);

        DynamicType.Unloaded<?> dynamicType = new ByteBuddy()
                .redefine(clazz)
                .method(ElementMatchers.isGetter().and(ElementMatchers.isPublic().and(ElementMatchers.isFinal())))
                .intercept(MethodDelegation.to(FieldGetterInterceptor.class))
                .make();

        Class<?> enhancedClass = dynamicType
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        // 클래스의 변경된 바이트 코드를 적용하기 위해 JVM 메모리에 쓰는 로직
        Field field = Class.forName("java.lang.ClassLoader").getDeclaredField("classes");
        field.setAccessible(true);
        Set<Class<?>> classes = (Set<Class<?>>) field.get(getClass().getClassLoader());
        classes.remove(clazz);
        classes.add(enhancedClass);
    }

    public static class FieldGetterInterceptor {
        public static Object intercept(@Origin Method method) throws Exception {
            Field field = method.getDeclaringClass().getDeclaredField(fieldName(method));
            field.setAccessible(true);
            return field.get(null);
        }

        private static String fieldName(Method method) {
            String methodName = method.getName();
            if (methodName.startsWith("get") && methodName.length() > 3) {
                return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
            }
            throw new IllegalArgumentException("Invalid getter method: " + methodName);
        }
    }

}
