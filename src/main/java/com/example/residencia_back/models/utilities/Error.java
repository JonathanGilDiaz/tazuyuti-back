/**
 * @author Dirección de Tecnologías e Innovación Digital - Secretaría de Finanzas
 * @version 1.0.0 Creado el 19 Nov 2024
 * @date 19/11/2024
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