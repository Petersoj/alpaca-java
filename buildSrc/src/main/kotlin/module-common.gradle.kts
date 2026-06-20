import net.ltgt.gradle.errorprone.CheckSeverity.WARN
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.JavaVersion.VERSION_25

plugins {
    `java-library`
    id("io.freefair.lombok")
    id("net.ltgt.errorprone")
    id("com.github.ben-manes.versions")
    `maven-publish`
}

group = PROJECT_GROUP
version = PROJECT_VERSION

java {
    sourceCompatibility = VERSION_25
    targetCompatibility = VERSION_25
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

val guavaVersion = "33.6.0-jre"

dependencies {
    api("org.jspecify:jspecify:1.0.0")
    api("com.google.guava:guava:$guavaVersion")

    implementation("org.slf4j:slf4j-api:2.0.17")

    errorprone("com.google.errorprone:error_prone_core:2.49.0")
    errorprone("com.uber.nullaway:nullaway:0.13.4")
    errorprone("net.jacobpeterson:final-coat:1.2.3")
}

tasks.withType(JavaCompile::class).configureEach {
    options.errorprone {
        allErrorsAsWarnings = true
        allSuggestionsAsWarnings = true
        disableWarningsInGeneratedCode = true
        excludedPaths = ".*/build/.*"

        disable("MissingSummary")
        disable("NullableOptional")
        check("Varifier", WARN)
        check("IdentifierName", WARN)
        check("MissingBraces", WARN)
        check("FieldCanBeFinal", WARN)
        check("MissingDefault", WARN)
        check("SwitchDefault", WARN)
        check("RedundantNullCheck", WARN)
        check("FieldMissingNullable", WARN)
        check("ParameterMissingNullable", WARN)
        check("ReturnMissingNullable", WARN)

        check("NullAway", WARN)
        option("NullAway:OnlyNullMarked", true)
        option("NullAway:JSpecifyMode", true)
        check("RequireExplicitNullMarking", WARN)

        check("FinalCoat", WARN)
    }
}

tasks.withType(Javadoc::class).configureEach {
    options {
        (this as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
        links("https://docs.oracle.com/en/java/javase/${java.targetCompatibility.majorVersion}/docs/api/",
                "https://jspecify.dev/docs/api/",
                "https://guava.dev/releases/$guavaVersion/api/docs/",
                "https://errorprone.info/api/latest/")
    }
}
tasks.withType(Javadoc::class).configureEach {
    project.configurations.flatMap { it.dependencies.withType(ProjectDependency::class) }.forEach {
        project(it.path).tasks.withType(Javadoc::class).forEach { dependencyJavadocTask ->
            this@configureEach.dependsOn(dependencyJavadocTask)
            this@configureEach.options {
                (this as StandardJavadocDocletOptions).linksOffline(
                        "https://javadoc.io/doc/${it.group}/${it.name}/${it.version}/",
                        dependencyJavadocTask.destinationDir!!.path)
            }
        }
    }
}

publishing {
    publications.create("jreleaser", MavenPublication::class) {
        from(components["java"])
        pom {
            name = provider { artifactId }
            description = provider { project.description }
            url = "https://github.com/Petersoj/alpaca-java"
            inceptionYear = "2025"
            licenses {
                license {
                    name = "MIT License"
                    url = "https://opensource.org/licenses/MIT"
                }
            }
            developers {
                developer {
                    id = "Petersoj"
                    name = "Jacob Peterson"
                }
            }
            scm {
                connection = pom.url.map { "scm:git:$it.git" }
                developerConnection = connection
                url = pom.url
            }
        }
    }
    repositories.maven {
        name = "jreleaser"
        url = uri(rootProject.layout.buildDirectory.dir(JRELEASER_MAVEN_REPOSITORY_DIRECTORY))
    }
}
