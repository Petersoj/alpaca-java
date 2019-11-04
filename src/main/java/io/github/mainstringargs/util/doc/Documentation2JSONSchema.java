package io.github.mainstringargs.util.doc;

public class Documentation2JSONSchema {

    /**
     * This is a standalone helper method that converts property text copied from the docs page into a json schema
     * property list that includes the type and the description. It's kinda hacky, but it helps in the generation of
     * JSON POJOs with Javadocs. Must be in the format of:
     * <p>
     * property name
     * <p>
     * type
     * <p>
     * description
     * <p>
     * E.g. The following text has been copied from the "Properties" section of this:
     * https://docs.alpaca.markets/api-documentation/api-v2/account/ webpage.
     * <p>
     * id
     * <p>
     * string
     * <p>
     * Account ID.
     * <p>
     * account_number
     * <p>
     * string
     * <p>
     * Account number.
     * <p>
     * etc...
     * <p>
     * And would be converted to:
     * <p>
     * "id": {
     * <p>
     * "type": "string",
     * <p>
     * "description": "Account ID."
     * <p>
     * },
     * <p>
     * "account_number": {
     * <p>
     * "type": "string",
     * <p>
     * "description": "Account number."
     * <p>
     * },
     *
     * @param docRowsAndColumns Must be copied from the docs webpage and in the format of:  <p> property name <p> type
     *                          <p> description <p> property name <p> type <p> etc...
     */
    public static void printCopiedAlpacaWebDocAsJSONSchema(String docRowsAndColumns) {
        String[] lines = docRowsAndColumns.split("\n");
        int indexOfDocLine = 0; // 0 for name, 1 for type, 2 for description
        for (String line : lines) {
            if (indexOfDocLine == 0) {
                System.out.println("\"" + line + "\": {");
                indexOfDocLine = 1;
            } else if (indexOfDocLine == 1) {
                System.out.println("\"type\": \"" + parseCopiedAlpacaDocTypeToSchemaType(line) + "\",");
                indexOfDocLine = 2;
            } else {
                System.out.println("\"description\": \"" + line + "\"");
                System.out.println("},");
                indexOfDocLine = 0;
            }
        }
    }

    private static String parseCopiedAlpacaDocTypeToSchemaType(String alpacaDocType) {
        alpacaDocType = alpacaDocType.toLowerCase();
        if (alpacaDocType.contains("string") || alpacaDocType.contains("timestamp")) {
            return "string";
        } else if (alpacaDocType.contains("boolean") || alpacaDocType.contains("bool")) {
            return "boolean";
        } else if (alpacaDocType.contains("int") || alpacaDocType.contains("integer")) {
            return "integer";
        } else if (alpacaDocType.contains("double") || alpacaDocType.contains("float") ||
                alpacaDocType.contains("number")) {
            return "double";
        } else {
            return "UNKOWN ALPACA DOC DATA TYPE";
        }
    }
}
