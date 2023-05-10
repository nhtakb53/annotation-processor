plugins {
    id("java")
    id("maven-publish")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    annotationProcessor("com.squareup:javapoet:1.13.0")
    annotationProcessor("com.google.auto.service:auto-service:1.0-rc7")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

// maven local 에 publish
publishing {
    publications {
        create<MavenPublication>("my-artifact") {
            from(components["java"])
            groupId = "org.example"
            artifactId = "processor"
            version = "1.0-SNAPSHOT"
        }
    }

    repositories {
        mavenLocal()
    }
}