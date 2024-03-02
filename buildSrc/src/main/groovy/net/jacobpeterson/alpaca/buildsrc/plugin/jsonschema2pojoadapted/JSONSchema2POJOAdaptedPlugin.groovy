package net.jacobpeterson.alpaca.buildsrc.plugin.jsonschema2pojoadapted

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jsonschema2pojo.gradle.JsonSchemaExtension

/**
 * {@link JSONSchema2POJOAdaptedPlugin} is a Gradle plugin that adapts the <code>jsonschema2pojo</code> Gradle plugin
 * to add/change some features.
 */
class JSONSchema2POJOAdaptedPlugin implements Plugin<Project> {

    public static final EXTENSION_KEY = "jsonSchema2POJOAdaptedConfig"

    @Override
    void apply(Project project) {
        project.extensions.create(EXTENSION_KEY, JsonSchemaExtension)
        if (project.plugins.hasPlugin("java")) {
            project.tasks.register("generatePOJOs", GeneratePOJOsTask)
            project.tasks.compileJava.dependsOn "generatePOJOs"
        } else {
            throw new GradleException("'java' Gradle plugin required.")
        }
    }
}
