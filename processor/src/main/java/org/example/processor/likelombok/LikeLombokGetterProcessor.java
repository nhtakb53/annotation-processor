package org.example.processor.likelombok;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.bytebuddy.pool.TypePool;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("LikeLombokGetter")
public class LikeLombokGetterProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("LikeLombokGetter Process");
        // 해당 애노테이션이 붙어 있는 엘리먼트들을 가져 온다.
        // Element : 클래스, 인터페이스, 메소드 등 애노테이션을 붙일 수 있는 target
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(LikeLombokGetter.class);
        for (Element element: elements) {
            TypeElement typeElement = (TypeElement) element;
            TypeMirror type = typeElement.asType();
            TypePool typePool = TypePool.Default.of(typeElement.getClass().getClassLoader());
            ClassName className = ClassName.get(typeElement);

            // MethodSpec: Method 만드는 객체
            MethodSpec pullout = MethodSpec.methodBuilder("pullOut")
                    .addModifiers(Modifier.PRIVATE)          // 접근 제한자 설정
                    .returns(String.class)                  // Method return Type 설정
                    .addStatement("return $S", "Dog!")   // return 시 값 전달 설정
                    .build();

            // TypeSpec : Type 만드는 객체
            TypeSpec cuteMoney = TypeSpec.classBuilder("CuteMoney")
                    .addModifiers(Modifier.PUBLIC)  // 접근 제한자 설정
                    .addMethod(pullout)             // 해당 클래스에 메소드 추가
                    .addSuperinterface(className)
                    .build();

            // Filer : 소스코드,클래스코드 및 리소스를 생성할 수 있는 인터페이스
            // processingEnv : AbstractProcessor 상속 받으면 쓸 수 있는 전역 변수
            Filer filer = processingEnv.getFiler();
            try {
                JavaFile.builder(className.packageName(), cuteMoney)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR : " + e);
            }
        }

        return true;
    }

}
