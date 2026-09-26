plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.guava:guava:33.7.1-jre")

    // Define versions for `module-common.gradle.kts` plugins:
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.7.0")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.1.1")
    implementation("io.github.ben-manes.versions:io.github.ben-manes.versions.gradle.plugin:0.61.0")

    // Define versions for `openapi-generator-adapted.gradle.kts` plugins:
    implementation("org.openapi.generator:org.openapi.generator.gradle.plugin:7.25.0")
}
