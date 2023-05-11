package org.example.processor.clazz;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("org.example.processor.clazz.MoneyGetter")
public class MoneyGetterProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                if (element.getKind() == ElementKind.CLASS) {
                    generateGetterMethods((TypeElement) element);
                }
            }
        }
        return true;
    }

    private void generateGetterMethods(TypeElement element) {
        String className = element.getSimpleName().toString();
        String packageName = element.getEnclosingElement().toString();
        String outputDir = processingEnv.getOptions().getOrDefault("outputDir", "");
        String outputPath = outputDir + File.separator + packageName.replace(".", File.separator) + File.separator + className + ".class";

        // 파일이 이미 존재하는지 확인
        File existingFile = new File(outputPath);
        if (existingFile.exists()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Modifying existing file: " + existingFile.getAbsolutePath());
        }

        try {
            FileObject fileObject = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, packageName, className + ".class");
            Writer writer = fileObject.openWriter();

            writer.write("package " + packageName + ";\n\n");
            writer.write("public class " + className + " {\n");

            for (Element enclosedElement : element.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    String fieldName = enclosedElement.getSimpleName().toString();
                    String fieldType = enclosedElement.asType().toString();
                    String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

                    writer.write("\tpublic " + fieldType + " " + getterName + "() {\n");
                    writer.write("\t\treturn this." + fieldName + ";\n");
                    writer.write("\t}\n\n");
                }
            }

            writer.write("}\n");
            writer.close();

            if (!outputDir.isEmpty()) {
                File outputDirectory = new File(outputDir);
                File packageDirectory = new File(outputDirectory, packageName.replace(".", "/"));
                packageDirectory.mkdirs();

                File outputFile = new File(packageDirectory, className + ".java");
                Files.copy(existingFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}