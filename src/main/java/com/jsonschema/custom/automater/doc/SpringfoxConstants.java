package com.jsonschema.custom.automater.doc;

import io.swagger.annotations.ApiModelProperty;
import java.lang.annotation.Annotation;

/**
 * Constants used for Springfox/Swagger erlated Documentation purposes
 */
public final class SpringfoxConstants {
    // Class constants
    public static final Class<? extends Annotation> API_PROPERTY_CLASS = ApiModelProperty.class;
    // String constants for field mappings
    public static final String API_MODEL_DESC = "description";
    public static final String REQUIRED = "required";
    public static final String VALUE = "value";
}
