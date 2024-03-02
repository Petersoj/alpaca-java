package net.jacobpeterson.alpaca.buildsrc.plugin.jsonschema2pojoadapted

import org.gradle.api.DefaultTask
import org.gradle.api.model.ReplacedBy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.jsonschema2pojo.GenerationConfig
import org.jsonschema2pojo.Jsonschema2Pojo
import org.jsonschema2pojo.gradle.GradleRuleLogger

import java.nio.file.Files
import java.nio.file.Path

import static JSONSchema2POJOAdaptedPlugin.EXTENSION_KEY
import static com.google.common.base.Preconditions.checkArgument

/**
 * {@link GeneratePOJOsTask} is a Gradle task that invokes <code>jsonschema2pojo</code> with special configurations
 * and generation logic.
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo/blob/master/jsonschema2pojo-gradle-plugin/src/main/groovy/org/jsonschema2pojo/gradle/GenerateJsonSchemaJavaTask.groovy">GenerateJsonSchemaJavaTask</a>
 */
class GeneratePOJOsTask extends DefaultTask {

    @ReplacedBy("configurationString")
    GenerationConfig configuration

    @Input
    String getConfigurationString() {
        configuration.toString()
    }

    @Internal
    List<JSONSchemaFileConfig> jsonSchemaFileConfigs

    GeneratePOJOsTask() {
        group = "Build"
        description = "Generates Java POJOs from JSON schemas."

        // Validate plugin configuration
        configuration = project.extensions.getByName(EXTENSION_KEY) as GenerationConfig
        checkArgument(!configuration.source.hasNext(), "'source' should not be set for '${name}'.")
        checkArgument(configuration.targetDirectory == null, "'targetDirectory' should not be set for '${name}'.")

        // Set custom defaults on plugin configuration
        configuration.targetDirectory = project.file(
                "${project.layout.buildDirectory.getAsFile().get().getPath()}/generated-sources/jsonschema2pojo")
        configuration.targetPackage = configuration.targetPackage == "" ?
                "${project.group}.model" : configuration.targetPackage
        configuration.propertyWordDelimiters = ["-", "_"] as char[]
        configuration.annotationStyle = "gson"
        configuration.sourceType = "jsonschema"
        configuration.customDateTimePattern = "yyyy-MM-ddTHH:mm:ssZ"
        configuration.includeConstructors = true
        configuration.serializable = true
        configuration.includeGetters = true
        configuration.includeSetters = true
        configuration.includeCopyConstructor = true
        configuration.generateBuilders = true

        // Get project main java source set
        final def projectMainJavaSourceSet =
                project.extensions.getByType(SourceSetContainer).named("main").get().java

        // Walk through source files to create a list of JSON schema files to process
        jsonSchemaFileConfigs = new ArrayList<>()
        projectMainJavaSourceSet.srcDirs.each { srcDir ->
            if (!srcDir.exists()) {
                return
            }
            final def sourceDirPath = srcDir.getAbsolutePath()
            srcDir.eachFileRecurse { sourceFile ->
                final def sourceFilePath = sourceFile.getAbsolutePath()
                if (sourceFilePath.endsWith(".json")) {
                    def targetPackage = sourceFile.getParentFile().getAbsolutePath()
                            .substring(sourceDirPath.length())
                            .replace(File.separator, ".").replace("-", "").replace("_", "")
                            .toLowerCase()
                    targetPackage = targetPackage.replace("${project.group}", "")
                    targetPackage = targetPackage.startsWith(".") ? targetPackage.substring(1) : targetPackage
                    targetPackage = configuration.targetPackage +
                            (targetPackage.isEmpty() ? "" : targetPackage)
                    jsonSchemaFileConfigs.add(new JSONSchemaFileConfig(sourceFile, targetPackage))

                    inputs.file(sourceFile)
                    outputs.file(project.file(configuration.targetDirectory.getAbsolutePath() + File.separator +
                            targetPackage.replace(".", File.separator) + File.separator +
                            sourceFile.getName().replace(".json", ".java")))
                }
            }
        }

        outputs.dir(configuration.targetDirectory)
        outputs.cacheIf { true }

        projectMainJavaSourceSet.srcDirs(configuration.targetDirectory)
    }

    @TaskAction
    def execute() {
        logger.info("Deleting existing generated POJOs...")
        Files.walk(configuration.targetDirectory.toPath())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(file -> logger.info("Deleting file: {}", file.getPath()))
                .forEach(File::delete)
        logger.info("Deleted existing generated POJOs.")

        logger.info("Generating POJOs from JSON schemas...")
        final def gradleRuleLogger = new GradleRuleLogger(logger)
        jsonSchemaFileConfigs.forEach { jsonSchemaFileConfig ->
            configuration.source = List.of(jsonSchemaFileConfig.sourceFile.toURI().toURL())
            configuration.targetPackage = jsonSchemaFileConfig.targetPackage
            Jsonschema2Pojo.generate(configuration, gradleRuleLogger)
            logger.info("Generated POJO for: {}", jsonSchemaFileConfig.targetPackage + "." +
                    jsonSchemaFileConfig.sourceFile.getName())
        }
        logger.info("Generated POJOs from JSON schemas.")
    }
}
