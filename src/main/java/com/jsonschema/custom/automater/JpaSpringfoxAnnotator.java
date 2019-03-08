package com.jsonschema.custom.automater;

import com.fasterxml.jackson.databind.JsonNode;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.GenerationConfig;

/**
 * Class to be used alongside Json Scheme2POJO library to generate JPA and Swagger/Springfox related annotations to
 * Java POJO classes. This can help support rapid development of code. Future enhancements will utilize the
 * {@link GenerationConfig} per suggestions from extended {@link AbstractAnnotator} in parent class.
 *
 * Please reference the JSON Schema2POJO Git Repo for more information
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo">JSON Schema2POJO Git Repo</a>
 * @author Jack Phillips
 */
public class JpaSpringfoxAnnotator extends AnnotatorAutomator {
    /**
     * Add the necessary annotation to cause only non-null values to be included
     * during serialization.
     *
     * @param clazz a generated pojo class, that is serialized to JSON
     * @param schema the object schema associated with this clazz
     */
    @Override
    public void propertyInclusion(JDefinedClass clazz, JsonNode schema) {
        // Class level annotations to process
        handleJpaInclusions(clazz, schema);
        handleJsonIgnoreInclusions(clazz, schema);
    }

    /**
     * Add the necessary annotation to mark a Java field as a JSON property
     *
     * @param field the field that contains data that will be serialized
     * @param clazz the owner of the field (class to which the field belongs)
     * @param propertyName the name of the JSON property that this field represents
     * @param propertyNode the schema node defining this property
     */
    @Override
    public void propertyField(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        // Process JPA properties
        handleJpaPropertyFields(field, clazz, propertyName, propertyNode);
        // Process Springfox/Swagger Properties
        handleSpringfoxPropertyFields(field, clazz, propertyName, propertyNode);
    }
}
