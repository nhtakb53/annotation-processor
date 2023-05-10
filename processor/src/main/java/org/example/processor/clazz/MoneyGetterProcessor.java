package org.example.processor.clazz;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.*;
import java.io.*;
import java.util.*;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("org.example.processor.clazz.MoneyGetterProcessor")
public class MoneyGetterProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(MoneyGetter.class)) {
            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() != ElementKind.FIELD) {
                    continue;
                }
                String fieldName = enclosedElement.getSimpleName().toString();
                TypeMirror fieldType = enclosedElement.asType();
                String getterName = "get" + capitalize(fieldName);
                String returnType = fieldType.toString();

                String method = "public " + returnType + " " + getterName + "() {\n";
                method += "return this." + fieldName + ";\n";
                method += "}\n";

                addMethodToClass(element, method);
            }
        }
        return true;
    }

    private void addMethodToClass(Element element, String method) {
        String packageName = elementUtils.getPackageOf(element).toString();
        String className = element.getEnclosingElement().getSimpleName().toString();
        JavaFileObject sourceFile;
        try {
            sourceFile = filer.createSourceFile(
                    packageName + "." + className);
            try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                out.println("package " + packageName + ";");
                out.println();
                out.println("public class " + className + " {");
                out.println();
                out.println(method);
                out.println();
                out.println("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate code: " + e.getMessage());
        }
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
