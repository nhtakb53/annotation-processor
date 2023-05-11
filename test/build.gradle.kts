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
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("org.example:processor:1.0-SNAPSHOT")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.example:processor:1.0-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}