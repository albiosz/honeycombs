package com.albiosz.honeycombs.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class Json {

    public static <T> boolean isValid(String json, Class<T> schemaClass) {
        String jsonSchema = assertDoesNotThrow(() -> JsonSchema.generateJsonSchema(schemaClass));
        return assertDoesNotThrow(() -> JsonSchema.isValidJson(json, jsonSchema));
    }

    public static <T> T unmarshal(String json, Class<T> parseClass, ObjectMapper objectMapper) {
        return assertDoesNotThrow(() -> objectMapper.readValue(json, parseClass));
    }
}
