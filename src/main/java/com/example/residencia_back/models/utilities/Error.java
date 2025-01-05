/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
 */
package com.example.residencia_back.models.utilities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a container for error messages that can be used to 
 * provide detailed feedback in case of validation or processing failures.
 *
 * @param errors  A list of error messages that describe the issues encountered.
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Error {

    private List<String> errors;

}