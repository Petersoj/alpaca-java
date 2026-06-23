import org.jreleaser.model.Active.ALWAYS
import org.jreleaser.model.Active.NEVER

plugins {
    java
    id("org.jreleaser") version "1.24.0"
}

group = PROJECT_GROUP
version = PROJECT_VERSION

repositories {
    mavenCentral()
}

jreleaser {
    signing {
        pgp {
            active = ALWAYS
            armored = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepositories.add(layout.buildDirectory.dir(JRELEASER_MAVEN_REPOSITORY_DIRECTORY)
                            .map { it.asFile.path })
                    skipPublicationCheck = true
                }
            }
        }
    }
    release {
        github {
            uploadAssets = NEVER
        }
    }
}
subprojects {
    tasks.configureEach {
        rootProject.tasks.jreleaserFullRelease.configure {
            mustRunAfter(this@configureEach)
        }
    }
}
