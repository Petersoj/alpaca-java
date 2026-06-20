plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.google.guava:guava:33.6.0-jre")

    // Define versions for `module-common.gradle.kts` plugins:
    implementation("io.freefair.lombok:io.freefair.lombok.gradle.plugin:9.5.0")
    implementation("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:5.1.0")
    implementation("com.github.ben-manes:gradle-versions-plugin:0.54.0")

    // Define versions for `openapi-generator-adapted.gradle.kts` plugins:
    implementation("org.openapi.generator:org.openapi.generator.gradle.plugin:7.23.0")
}
