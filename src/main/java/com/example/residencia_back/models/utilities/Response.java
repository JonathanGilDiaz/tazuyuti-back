/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 05 Ene 2025
 * @date 05/01/2025
 */
package com.example.residencia_back.models.utilities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
* Represents the response returned after an operation, indicating success or failure.
*
* @param success  Indicates if the operation was successful (true) or not (false).
* @param message  Provides additional information or feedback about the operation.
* @param data     Contains any relevant data returned by the operation, if applicable.
*/

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Response {
    private boolean success;
    private String message;
    private Object data;

}