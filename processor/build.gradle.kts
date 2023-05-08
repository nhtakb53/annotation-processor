plugins {
    id("java")
    id("maven-publish")
}

group = "org.example"
version = "2.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    annotationProcessor("com.google.auto.service:auto-service:1.0-rc7")
    implementation("com.squareup:javapoet:1.13.0")
    // compileOnly("org.projectlombok:lombok")
    // annotationProcessor("org.projectlombok:lombok")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("my-artifact") {
            from(components["java"])
            groupId = "org.example"
            artifactId = "annotation-processor"
            version = "2.0-SNAPSHOT"
        }
    }

    repositories {
        mavenLocal()
    }
}