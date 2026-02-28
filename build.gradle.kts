plugins {
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"  // 1.1.7+ required for Gradle 9
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"   // adds open to Spring-annotated classes
    kotlin("plugin.jpa") version "2.1.0"       // adds no-arg constructors to @Entity classes
}

group = "com.ticketing"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
