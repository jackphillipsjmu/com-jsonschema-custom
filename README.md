# Custom Library to Provide Common JPA and Springfox/Swagger Documentation Annotations

## Setup
- Clone project to your local environment `git clone git@github.com:jackphillipsjmu/com-jsonschema-custom.git`
- Build the project using either your locally installed gradle instance or the Gradle wrapper supplied in this repository `./gradlew build`
- Move the built `.jar` file located in`build/libs/com-jsonschema-custom-<VERSION>.jar` to the Gradle project you want to use the dependency in (Ex. `$PROJECT_DIR/libs/com-jsonschema-custom-<VERSION>.jar`)
- Modify the `build.gradle` file to pull in the dependency `classpath(files("libs/com-jsonschema-custom-<VERSION>.jar"))`
  - Ensure that in your `buil.gradle` file that you have the JSON Schema to POJO dependency as well `classpath("org.jsonschema2pojo:jsonschema2pojo-gradle-plugin:$json_schema_pojo_version")`
- Add or update the `build.gradle` `jsonSchema2Pojo` task to use the custom library
- Example:
```
jsonSchema2Pojo {
    sourceType = 'jsonschema'
    source = files("${sourceSets.main.output.resourcesDir}/schema")
    targetDirectory = file("${project.buildDir}/generated-sources/js2p")
    targetPackage = 'com.example.api'
    includeJsr303Annotations = true
    serializable = true
    customAnnotator = 'com.jsonschema.custom.automater.JpaSpringfoxAnnotator'
    includeAdditionalProperties = false
}
```
- Add a JSON schema file to your project in the `$PROJECT_DIR/src/main/resources/schema` directory. To see what is supported with the base JSON Schema to POJO library check out their [GitHub repository](https://github.com/joelittlejohn/jsonschema2pojo).
- Build your project and use your generated classes!

## Example JSON Schema File
```
{
  "$schema": "http://json-schema.org/schema#",
  "title": "Example Entity with JPA and Swagger Annotations",
  "type": "object",
  "entity": true,                        // Place @Entity annotation at the Class level
  "table": {"tableName": "foo_jpa_tbl"}, // Place @Table annotation with the corresponding name at the Class level
  "properties": {
    "id": {
      "description": "ID for the POJO",  // Places @ApiModelProperty("ID for the POJO") annotation above this field
      "javaType": "java.lang.Long",      // Specify base Object type for this field
      "column": "id",                    // Places @Column annotation above this field with the name "id"
      "isIdColumn": true,                // Places @Id annotation above this field
      "generatedValue": {                // Places @GeneratedValue(strategy = GenerationType.IDENTITY) above this field
        "strategy": "IDENTITY"
      }
    },
    "fooField": {
       "required": true,                 // Places @ApiModelProperty(required = true) annotation above this field
       "column": "required_foo_column",
       "description": "An example column that maps to JPA and is documented by Swagger",
       "type": "string"
     },
    "barField": {
      "column": "bar_column",
      "description": "An example column that maps to JPA and is documented by Swagger",
      "type": "string"
    }
  }
}
```
- The POJO Class it creates will look something like this:
```

/**
 * Example Entity with JPA and Swagger Annotations
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "foo_jpa_tbl")
@JsonPropertyOrder({
    "id",
    "fooField",
    "barField"
})
public class Foo implements Serializable
{

    /**
     * ID for the POJO
     * 
     */
    @JsonProperty("id")
    @JsonPropertyDescription("ID for the POJO")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty("ID for the POJO")
    private Long id;
    /**
     * An example column that maps to JPA and is documented by Swagger
     * (Required)
     * 
     */
    @NotNull
    @JsonProperty("fooField")
    @JsonPropertyDescription("An example column that maps to JPA and is documented by Swagger")
    @Column(name = "required_foo_column")
    @ApiModelProperty(value = "An example column that maps to JPA and is documented by Swagger", required = true)
    private String fooField;
    /**
     * An example column that maps to JPA and is documented by Swagger
     * 
     */
    @JsonProperty("barField")
    @JsonPropertyDescription("An example column that maps to JPA and is documented by Swagger")
    @Column(name = "bar_column")
    @ApiModelProperty("An example column that maps to JPA and is documented by Swagger")
    private String barField;
    
    // Getters, Setters, toString, hashCode, equals ommitted
```
