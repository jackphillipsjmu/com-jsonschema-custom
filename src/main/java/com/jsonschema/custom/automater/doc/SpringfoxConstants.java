package com.jsonschema.custom.automater.doc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import java.lang.annotation.Annotation;

/**
 * Constants used for Springfox/Swagger related Documentation and JSON purposes that are not covered by the
 * vanilla JSON Schema 2 POJO library.
 */
public final class SpringfoxConstants {
    // Class constants
    public static final Class<? extends Annotation> API_PROPERTY_CLASS = ApiModelProperty.class;
    // String constants for field mappings
    public static final String API_MODEL_DESC = "description";
    public static final String REQUIRED = "required";
    public static final String VALUE = "value";
    // Constants used to include or ignore JSON properties
    public static final Class<? extends Annotation> JSON_IGNORE_PROPERTIES = JsonIgnoreProperties.class;
    public static final String IGNORE_UNKNOWN = "ignoreUnknown";
}
