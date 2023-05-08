plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
//    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
//    compileOnly("org.example:annotation-processor:1.4-SNAPSHOT")
    annotationProcessor("org.example:annotation-processor:1.4-SNAPSHOT")
    annotationProcessor("com.google.auto.service:auto-service:1.0-rc7")
//    annotationProcessor("com.squareup:javapoet:1.13.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}