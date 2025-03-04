package com.albiosz.honeycombs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import com.github.victools.jsonschema.module.jackson.JacksonOption;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonSchema {

    private JsonSchema() {}

    public static <T> String generateJsonSchema(Class<T> dtoClass) throws JsonProcessingException {

        JacksonModule module = new JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_REQUIRED);

        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON);
        configBuilder.with(module);
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(dtoClass);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jsonSchema);
    }

    public static boolean isValidJson(String jsonToValidate, String jsonSchema) throws JSONException {

        JSONObject rawJsonSchema = new JSONObject(new JSONTokener(jsonSchema));
        rawJsonSchema.put("additionalProperties", false);

        JSONObject json = new JSONObject(new JSONTokener(jsonToValidate));

        // Load and validate schema
        SchemaLoader loader = SchemaLoader.builder()
                .schemaJson(rawJsonSchema)
                .draftV7Support()
                .build();

        Schema schema = loader.load().build();
        try {
            schema.validate(json); // Will throw an exception if invalid
        } catch (ValidationException e) {
            // TODO: Think how to return it in a smarter way (not using sout)
            System.out.println(e.getMessage());
            e.getCausingExceptions().stream()
                .map(ValidationException::getMessage)
                .forEach(System.out::println);
            return false;
        }
        return true;
    }
}
