package com.jsonschema.custom.automater.doc;

import com.fasterxml.jackson.databind.JsonNode;
import com.jsonschema.custom.automater.AnnotatorAutomator;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.GenerationConfig;

/**
 * Class used alongside JSON Schema2POJO Library to generate Swagger/Springfox related Java Objects for documentation
 * purposes. This can help support rapid development of code. Future enhancements will utilize the
 * {@link GenerationConfig} per suggestions from extended {@link AbstractAnnotator}.
 *
 * Please reference the JSON Schema2POJO Git Repo for more information
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo">JSON Schema2POJO Git Repo</a>
 * @author Jack Phillips
 */
public class SpringfoxAnnotator extends AnnotatorAutomator {
    /**
     * Add the necessary annotation to mark a Java field as a JSON property.
     *
     * @param field the field that contains data that will be serialized
     * @param clazz the owner of the field (class to which the field belongs)
     * @param propertyName the name of the JSON property that this field represents
     * @param propertyNode the schema node defining this property
     */
    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        handleSpringfoxPropertyFields(field, clazz, propertyName, propertyNode);
    }
}
