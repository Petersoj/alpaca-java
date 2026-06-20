import org.gradle.api.provider.Property

interface OpenApiGeneratorAdaptedExtension {

    val environmentUrlProduction: Property<String>
    val environmentUrlSandbox: Property<String>
}
