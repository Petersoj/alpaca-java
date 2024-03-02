package net.jacobpeterson.alpaca.buildsrc.plugin.jsonschema2pojoadapted

/**
 * {@link JSONSchemaFileConfig} provides the configuration fields for a single JSON schema file.
 */
class JSONSchemaFileConfig {

    private File sourceFile
    private String targetPackage
    private String outputFilePath

    /**
     * Instantiates a new {@link JSONSchemaFileConfig}.
     *
     * @param sourceFile the source file
     * @param targetPackage the target package
     */
    JSONSchemaFileConfig(File sourceFile, String targetPackage) {
        this.sourceFile = sourceFile
        this.targetPackage = targetPackage
        this.outputFilePath = outputFilePath
    }

    File getSourceFile() {
        return sourceFile
    }

    String getTargetPackage() {
        return targetPackage
    }
}
