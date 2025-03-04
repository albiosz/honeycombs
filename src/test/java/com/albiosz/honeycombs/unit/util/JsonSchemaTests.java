package com.albiosz.honeycombs.unit.util;

import com.albiosz.honeycombs.util.JsonSchema;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.albiosz.honeycombs.util.JsonSchema.generateJsonSchema;
import static org.junit.jupiter.api.Assertions.*;

class JsonSchemaTests {

    static class TestDTO {
        @JsonProperty(required = true)
        public String name;
        public int age;
    }

    @Test
    void testGenerateJsonSchema() throws JsonProcessingException {
        String schema = generateJsonSchema(TestDTO.class);
        assertNotNull(schema);
        assertTrue(schema.contains("\"type\":\"object\""));
        assertTrue(schema.contains("\"properties\""));
        assertTrue(schema.contains("\"name\""));
        assertTrue(schema.contains("\"age\""));
        assertTrue(schema.contains("\"required\""));
    }

    @Test
    @DisplayName("invalid Json - missing required field")
    void testIsValidJsonMissingRequiredField() throws JsonProcessingException {
        String schema = generateJsonSchema(TestDTO.class);
        String invalidJson = "{\"age\":30}";

        boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(invalidJson, schema));
        assertFalse(isValid);
    }

    @Test
    @DisplayName("invalid Json - wrong type")
    void testIsValidJsonWrongType() throws JsonProcessingException {
        String schema = generateJsonSchema(TestDTO.class);
        String invalidJson = "{\"name\":\"John Doe\",\"age\":\"thirty\"}";

        boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(invalidJson, schema));
        assertFalse(isValid);
    }

    @Test
    @DisplayName("invalid Json - extra field")
    void testIsValidJsonExtraField() throws JsonProcessingException {
        String schema = generateJsonSchema(TestDTO.class);
        String invalidJson = "{\"name\":\"John Doe\",\"age\":30,\"extra\":\"field\"}";

        boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(invalidJson, schema));
        assertFalse(isValid);
    }

    @Test
    @DisplayName("valid Json")
    void testIsValidJson() throws JsonProcessingException {
        String schema = generateJsonSchema(TestDTO.class);
        String validJson = "{\"name\":\"John Doe\",\"age\":30}";

        boolean isValid = assertDoesNotThrow(() -> JsonSchema.isValidJson(validJson, schema));
        assertTrue(isValid);
    }
}
