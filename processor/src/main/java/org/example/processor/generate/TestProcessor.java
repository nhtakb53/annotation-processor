package org.example.processor.generate;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class) // META-INF/services/javax.annotation.processing.Processor 파일을 자동으로 생성
public class TestProcessor extends AbstractProcessor {

    // 이 프로세서가 어떤 애노테이션을 처리 할 것 인지 정하는 메소드
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Test.class.getName());
    }

    // 어떤 소스버전을 지원 할지 정하는 메소드
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    // 해당 애노테이션으로 작업을 처리하는 메소드
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 해당 애노테이션이 붙어 있는 엘리먼트들을 가져 온다.
        // Element : 클래스, 인터페이스, 메소드 등 애노테이션을 붙일 수 있는 target
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Test.class);
        for (Element element: elements) {
            Name simpleName = element.getSimpleName();

            TypeElement typeElement = (TypeElement) element;
            ClassName className = ClassName.get(typeElement);

            // MethodSpec: Method 만드는 객체
            MethodSpec pullout = MethodSpec.methodBuilder("pullOut")
                    .addModifiers(Modifier.PUBLIC)          // 접근 제한자 설정
                    .returns(String.class)                  // Method return Type 설정
                    .addStatement("return $S", "foo test!!")   // return 시 값 전달 설정
                    .build();

            // TypeSpec : Type 만드는 객체
            TypeSpec fooType = TypeSpec.classBuilder("FooBuilder")
                    .addModifiers(Modifier.PUBLIC)  // 접근 제한자 설정
                    .addMethod(pullout)             // 해당 클래스에 메소드 추가
                    .addSuperinterface(className)
                    .build();

            // Filer : 소스코드,클래스코드 및 리소스를 생성할 수 있는 인터페이스
            // processingEnv : AbstractProcessor 상속 받으면 쓸 수 있는 전역 변수
            Filer filer = processingEnv.getFiler();
            try {
                JavaFile.builder(className.packageName(), fooType)
                        .build()
                        .writeTo(filer);
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR : " + e);
            }
        }

        return true;
    }

}
