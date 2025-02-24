/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.models.response;

import lombok.Builder;
import lombok.Data;

/**
 * Represents the response model for a file sent to the front end.
 * This model includes metadata about the file, such as its name, URL, type, and size.
 *
 * @param nombre  The name of the file.
 * @param url     The URL to access or download the file.
 * @param tipo    The type or format of the file (e.g., "pdf", "jpeg").
 * @param tamano  The size of the file in bytes.
 */

@Data
@Builder
public class ResponseFile {
    private String nombre;
    private String url;
    private String tipo;
    private long tamano;
}
