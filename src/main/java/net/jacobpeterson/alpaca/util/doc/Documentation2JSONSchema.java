package net.jacobpeterson.alpaca.util.doc;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * The {@link Documentation2JSONSchema} is used strictly to expedite the process of creating JSON schemas for POJO
 * generation in this library using Alpaca's website documentation.
 */
public class Documentation2JSONSchema {

    /**
     * This is a standalone helper method that converts properties HTML copied from the Alpaca docs page into a json
     * schema property list that includes the type and the description. It's kinda hacky, but it helps in the generation
     * of JSON POJOs with Javadocs. Must be in the format of a list of:
     * <pre>
     * &lt;div class="spec-table"&gt;
     *     &lt;div class="spec-row"&gt;
     *     &lt;div class="spec-left"&gt;&lt;span class="spec-name"&gt;id&lt;/span&gt;&lt;/div&gt;
     *     &lt;div class="spec-right"&gt;
     *         &lt;div class="spec-type"&gt;string&lt;uuid&gt;&lt;/div&gt;
     *         &lt;div class="spec-desc"&gt;Account ID.&lt;/div&gt;
     *     &lt;/div&gt;
     *     &lt;/div&gt;
     *     ...
     * &lt;/div&gt;
     * </pre>
     *
     * @param alpacaDocXML the alpaca doc xml
     */
    public static void printAlpacaHTMLWebDocAsJSONSchema(String alpacaDocXML)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String nameXPath = "//span[" + xPathClassName("spec-name") + "]";
        String typeXPath = "//div[" + xPathClassName("spec-type") + "]";
        String descriptionXPath = "//div[" + xPathClassName("spec-desc") + "]";

        System.out.println(getSchemaFromDoc(alpacaDocXML, nameXPath, typeXPath, descriptionXPath));
    }

    /**
     * Converts a generic HTML doc for Alpaca with JSON property fields for "name", "type", and "description" into
     * schema JSON used for
     * <a href="https://github.com/joelittlejohn/jsonschema2pojo/wiki/Reference">jsonSchema2POJO</a>.
     *
     * @param docHTML          the doc html
     * @param nameXPath        the name x path
     * @param typeXPath        the type x path
     * @param descriptionXPath the description x path
     *
     * @return the schema from doc
     *
     * @throws ParserConfigurationException the parser configuration exception
     * @throws SAXException                 the sax exception
     * @throws IOException                  the io exception
     * @throws XPathExpressionException     the x path expression exception
     */
    private static String getSchemaFromDoc(String docHTML, String nameXPath, String typeXPath, String descriptionXPath)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        String schemaJSON = "";
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(docHTML.getBytes()));
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        XPathExpression propertyNamesXPath = xPath.compile(nameXPath);
        XPathExpression propertyTypesXPath = xPath.compile(typeXPath);
        XPathExpression propertyDescriptionsXPath = xPath.compile(descriptionXPath);

        NodeList propertyNameNodes = (NodeList) propertyNamesXPath.evaluate(document, XPathConstants.NODESET);
        NodeList propertyTypesNodes = (NodeList) propertyTypesXPath.evaluate(document, XPathConstants.NODESET);
        NodeList propertyDescriptionsNodes = (NodeList) propertyDescriptionsXPath.evaluate(document,
                XPathConstants.NODESET);

        for (int propertyIndex = 0; propertyIndex < propertyNameNodes.getLength(); propertyIndex++) {
            String propertyName = propertyNameNodes.item(propertyIndex).getTextContent();
            String propertyType = propertyTypesNodes.item(propertyIndex).getTextContent();
            String propertyDescription = propertyDescriptionsNodes.item(propertyIndex).getTextContent();

            // Property description may be null
            if (propertyDescription == null || propertyDescription.isEmpty()) {
                propertyDescription = propertyName;
            }

            schemaJSON += "\"" + propertyName + "\": {\n";
            schemaJSON += "\"existingJavaType\": \"" + parseDocTypeToSchemaJavaType(propertyType) + "\",\n";
            schemaJSON += "\"title\": \"" + propertyDescription + "\"\n";
            schemaJSON += "},\n";
        }
        return schemaJSON;
    }

    /**
     * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo/wiki/Reference#type">jsonSchema2Pojo Ref</a>
     */
    private static String parseDocTypeToSchemaJavaType(String docType) {
        docType = docType.toLowerCase();

        if (docType.contains("string") || docType.contains("timestamp")) {
            return "java.lang.String";
        } else if (docType.contains("boolean") || docType.contains("bool")) {
            return "java.lang.Boolean";
        } else if (docType.contains("int") || docType.contains("integer")) {
            return "java.lang.Integer";
        } else if (docType.contains("double") || docType.contains("float") ||
                docType.contains("number")) {
            return "java.lang.Double";
        } else {
            return "UNKNOWN DOC DATA TYPE";
        }
    }

    /**
     * @see <a href="https://devhints.io/xpath#class-check">https://devhints.io/xpath#class-check</a>
     */
    private static String xPathClassName(String className) {
        return String.format("contains(concat(' ',normalize-space(@class),' '),' %s ')", className);
    }
}
