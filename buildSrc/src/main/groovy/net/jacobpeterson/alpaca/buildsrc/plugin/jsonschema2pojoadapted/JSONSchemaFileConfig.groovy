package net.jacobpeterson.alpaca.buildsrc.plugin.jsonschema2pojoadapted

/**
 * {@link JSONSchemaFileConfig} provides the configuration fields for a single JSON schema file.
 */
class JSONSchemaFileConfig {

    private File sourceFile
    private String targetPackage

    /**
     * Instantiates a new {@link JSONSchemaFileConfig}.
     *
     * @param sourceFile the source file
     * @param targetPackage the target package
     */
    JSONSchemaFileConfig(File sourceFile, String targetPackage) {
        this.sourceFile = sourceFile
        this.targetPackage = targetPackage
    }

    File getSourceFile() {
        return sourceFile
    }

    String getTargetPackage() {
        return targetPackage
    }
}
