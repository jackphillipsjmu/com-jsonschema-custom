package com.jsonschema.custom.automater;

import com.fasterxml.jackson.databind.JsonNode;
import com.jsonschema.custom.automater.jpa.JpaConstants;
import com.jsonschema.custom.automater.doc.SpringfoxConstants;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import org.jsonschema2pojo.AbstractAnnotator;
import org.jsonschema2pojo.GenerationConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Abstract to be Class used alongside JSON Schema2POJO Library to generate customized Java Objects and provide common
 * functions across the project. This can help support rapid development of code. Future enhancements will utilize the
 * {@link GenerationConfig} per suggestions from the extended {@link AbstractAnnotator} class.
 * <p>
 * Please reference the JSON Schema2POJO Git Repo for more information
 *
 * @author Jack Phillips
 * @see <a href="https://github.com/joelittlejohn/jsonschema2pojo">JSON Schema2POJO Git Repo</a>
 */
public abstract class AnnotatorAutomator extends AbstractAnnotator {

    /**
     * Add the necessary JsonIgnore annotations to the generated class.
     *
     * @param clazz  a generated pojo class, that is serialized to JSON
     * @param schema the object schema associated with this clazz
     */
    public void handleJsonIgnoreInclusions(JDefinedClass clazz, JsonNode schema) {
        if (schema.has(SpringfoxConstants.IGNORE_UNKNOWN)) {
            boolean shouldIgnoreUnknown = schema.get(SpringfoxConstants.IGNORE_UNKNOWN).asBoolean(false);
            clazz.annotate(SpringfoxConstants.JSON_IGNORE_PROPERTIES)
                    .param(SpringfoxConstants.IGNORE_UNKNOWN, shouldIgnoreUnknown);
        }
    }

    /**
     * Add the necessary JPA annotations to the generated class.
     *
     * @param clazz  a generated pojo class, that is serialized to JSON
     * @param schema the object schema associated with this clazz
     */
    public void handleJpaInclusions(JDefinedClass clazz, JsonNode schema) {
        // If we have an entity tag then append the annotation to the class
        if (schema.has(JpaConstants.ENTITY)) {
            clazz.annotate(Entity.class);
        }
        // if we have a table tag then append the annotation to the class
        if (schema.has(JpaConstants.TABLE)) {
            JsonNode node = schema.get(JpaConstants.TABLE);
            JAnnotationUse annotation = clazz.annotate(Table.class);
            // Process table name internal value if needed
            if (node.has(JpaConstants.TABLE_NAME)) {
                annotation.param(JpaConstants.NAME, node.get(JpaConstants.TABLE_NAME).asText());
            }
        }
    }

    /**
     * Add the necessary annotation to mark a Java field as a JPA property
     *
     * @param field        the field that contains data that will be serialized
     * @param clazz        the owner of the field (class to which the field belongs)
     * @param propertyName the name of the JSON property that this field represents
     * @param propertyNode the schema node defining this property
     */
    public void handleJpaPropertyFields(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        // Id and GeneratedValue fields
        handleIdFields(field, propertyNode);
        // Column fields
        handleColumnFields(field, propertyNode);
        // Multiplicity, i.e. OneToMany, ManyToOne, etc.
        handleMultiplicityFields(field, propertyNode);
        // JoinTable Values
        handleJoinTable(field, clazz, propertyName, propertyNode);
    }

    /**
     * Add the necessary annotation to mark a Java field as a JSON property
     *
     * @param field        the field that contains data that will be serialized
     * @param clazz        the owner of the field (class to which the field belongs)
     * @param propertyName the name of the JSON property that this field represents
     * @param propertyNode the schema node defining this property
     */
    public void handleSpringfoxPropertyFields(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        handleSpringfoxPropertyField(field, propertyNode);
    }

    /**
     * Process ID related fields and append to generated POJO
     *
     * @param field        the field that contains data that will be serialized
     * @param propertyNode the schema node defining this property
     */
    private void handleIdFields(JFieldVar field, JsonNode propertyNode) {
        if (propertyNode.has(JpaConstants.IS_ID_COLUMN) && propertyNode.get(JpaConstants.IS_ID_COLUMN).asBoolean(true)) {
            field.annotate(Id.class);
        }

        if (propertyNode.has(JpaConstants.GENERATED_VALUE)) {
            JsonNode generatedValueNode = propertyNode.get(JpaConstants.GENERATED_VALUE);
            JAnnotationUse jAnnotationUse = field.annotate(GeneratedValue.class);
            if (generatedValueNode.has(JpaConstants.STRATEGY)) {
                jAnnotationUse.param(JpaConstants.STRATEGY, GenerationType.valueOf(generatedValueNode.get(JpaConstants.STRATEGY).asText()));
            }
        }
    }

    /**
     * Process Column related fields and append to generated POJO
     *
     * @param field        the field that contains data that will be serialized
     * @param propertyNode the schema node defining this property
     */
    private void handleColumnFields(JFieldVar field, JsonNode propertyNode) {
        if (propertyNode.has(JpaConstants.COLUMN)) {
            JAnnotationUse jAnnotationUse = field.annotate(Column.class);
            if (propertyNode.has(JpaConstants.COLUMN_NAME)) {
                jAnnotationUse.param(JpaConstants.NAME, propertyNode.get(JpaConstants.COLUMN_NAME).asText());
            } else if (!propertyNode.get(JpaConstants.COLUMN).isBoolean()) {
                jAnnotationUse.param(JpaConstants.NAME, propertyNode.get(JpaConstants.COLUMN).asText());
            }
        }
    }

    /**
     * Process Multiplicity related fields and append to generated POJO, i.e. OneToOne, ManyToMany, etc.
     *
     * @param field        the field that contains data that will be serialized
     * @param propertyNode the schema node defining this property
     */
    private void handleMultiplicityFields(JFieldVar field, JsonNode propertyNode) {
        JAnnotationUse jAnnotationUse = null;
        JsonNode internalNode = null;

        if (propertyNode.has(JpaConstants.ONE_TO_ONE)) {
            jAnnotationUse = field.annotate(OneToOne.class);
            internalNode = propertyNode.get(JpaConstants.ONE_TO_ONE);
        } else if (propertyNode.has(JpaConstants.MANY_TO_MANY)) {
            jAnnotationUse = field.annotate(ManyToMany.class);
            internalNode = propertyNode.get(JpaConstants.MANY_TO_MANY);
        } else if (propertyNode.has(JpaConstants.ONE_TO_MANY)) {
            jAnnotationUse = field.annotate(OneToMany.class);
            internalNode = propertyNode.get(JpaConstants.ONE_TO_MANY);
        } else if (propertyNode.has(JpaConstants.MANY_TO_ONE)) {
            jAnnotationUse = field.annotate(ManyToOne.class);
            internalNode = propertyNode.get(JpaConstants.MANY_TO_ONE);
        }

        if (Objects.nonNull(internalNode) && internalNode.has(JpaConstants.FETCH) && Objects.nonNull(jAnnotationUse)) {
            String fetch = internalNode.get(JpaConstants.FETCH).asText();
            FetchType fetchType = FetchType.valueOf(fetch);
            jAnnotationUse.param(JpaConstants.FETCH, fetchType);
        }
    }

    /**
     * Process Join Table related fields and append to generated POJO.
     *
     * @param field        the field that contains data that will be serialized
     * @param clazz        the owner of the field (class to which the field belongs)
     * @param propertyName the name of the JSON property that this field represents
     * @param propertyNode the schema node defining this property
     */
    private void handleJoinTable(JFieldVar field, JDefinedClass clazz, String propertyName, JsonNode propertyNode) {
        if (propertyNode.has(JpaConstants.JOIN_TABLE)) {
            JAnnotationUse jAnnotationUse = field.annotate(JoinTable.class);
            JsonNode node = propertyNode.get(JpaConstants.JOIN_TABLE);

            if (node.has(JpaConstants.NAME)) {
                String tableName = node.get(JpaConstants.NAME).asText();
                jAnnotationUse.param(JpaConstants.NAME, tableName);
            }

            if (node.has(JpaConstants.JOIN_COLUMNS)) {
                handleJoinColumns(node, jAnnotationUse, JpaConstants.JOIN_COLUMNS);
                handleJoinColumns(node, jAnnotationUse, JpaConstants.INVERSE_JOIN_COLUMNS);
            }
        }
    }

    /**
     * Processes Join Column related fields and appends to generated POJO.
     *
     * @param propertyNode   the schema node defining this property
     * @param jAnnotationUse JAnnotationUser to append necessary values for POJO generation
     * @param annotationKey  String key to use in join column
     */
    private void handleJoinColumns(JsonNode propertyNode, JAnnotationUse jAnnotationUse, String annotationKey) {
        if (propertyNode.has(JpaConstants.JOIN_COLUMNS)) {
            JsonNode node = propertyNode.get(annotationKey);
            if (Objects.isNull(node)) {
                return;
            }

            // Create Join Column
            JAnnotationUse joinColumn = jAnnotationUse.annotationParam(annotationKey, JoinColumn.class);
            // Get name value
            if (node.has(JpaConstants.NAME)) {
                String joinColumnName = node.get(JpaConstants.NAME).asText();
                joinColumn.param(JpaConstants.NAME, joinColumnName);
            }
            // Get REFERENCED_COLUMN_NAME value and append
            if (node.has(JpaConstants.REFERENCED_COLUMN_NAME)) {
                joinColumn.param(JpaConstants.REFERENCED_COLUMN_NAME, node.get(JpaConstants.REFERENCED_COLUMN_NAME).asText());
            }
        }
    }

    /**
     * Process Java fields and append {@link io.swagger.annotations.ApiModelProperty} along with other internal
     * properties if necessary.
     *
     * @param field        that can have a JDocComment associated with it, used for serializing data.
     * @param propertyNode Base class for all JSON nodes, which form the basis of JSON Tree Model
     *                     that Jackson implements.
     */
    private void handleSpringfoxPropertyField(JFieldVar field, JsonNode propertyNode) {
        // Determine the fields that need to be appended
        final boolean hasDescription = propertyNode.has(SpringfoxConstants.API_MODEL_DESC);
        final boolean isRequired = propertyNode.has(SpringfoxConstants.REQUIRED);
        // If we should alter the generated POJO then do so
        if (hasDescription || isRequired) {
            // Annotate field with @ApiModelProperty
            JAnnotationUse jAnnotationUse = field.annotate(SpringfoxConstants.API_PROPERTY_CLASS);
            // Grab description if available and append to generated annotation
            if (hasDescription) {
                String descriptionValue = propertyNode.get(SpringfoxConstants.API_MODEL_DESC).asText();
                jAnnotationUse.param(SpringfoxConstants.VALUE, descriptionValue);
            }
            if (isRequired) {
                // Mark field as required or not defaulting to false if the value does not exist
                boolean requiredValue = propertyNode.get(SpringfoxConstants.REQUIRED).asBoolean(false);
                jAnnotationUse.param(SpringfoxConstants.REQUIRED, requiredValue);
            }
        }
    }
}

