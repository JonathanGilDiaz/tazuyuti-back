/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.helpers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CopyObject {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T deepCopy(T object, Class<T> type) {
        try {
            // Serialize and deserialize to create a new copy of the object
            return objectMapper.readValue(objectMapper.writeValueAsString(object), type);
        } catch (Exception e) {
            throw new RuntimeException("Error al realizar una copia profunda", e);
        }
    }

    public static <T> T deepCopy(T object, TypeReference<T> typeReference) {
        try {
            // Serialize and deserialize to create a new copy of the object
            return objectMapper.readValue(objectMapper.writeValueAsString(object), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Error al realizar una copia profunda", e);
        }
    }
}