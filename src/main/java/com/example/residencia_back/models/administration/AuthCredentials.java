/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.models.administration;

import com.example.residencia_back.helpers.SystemText;
import com.example.residencia_back.validator.onCreate;
import com.example.residencia_back.validator.onUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
* Represents the credentials required for user authentication.
*
* @param correo   The email address of the user attempting to log in.
* @param password The password associated with the user's email.
*/

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class AuthCredentials {

    @Schema(description = SystemText.Login.ENTITY_CORREO)        // This annotation indicates that information for the swagger
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_CORREO_REQUERIDO)        // This annotation indicates that this parameter must not be null.
    @NotBlank(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_CORREO_REQUERIDO)       // This annotation indicates that this parameter must not be empty.
    private String usuario;
    
    @Schema(description = SystemText.Login.ENTITY_PASSWORD)        // This annotation indicates that information for the swagger
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_PASSWORD_REQUERIDO)        // This annotation indicates that this parameter must not be null.
    @NotBlank(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_PASSWORD_REQUERIDO)       // This annotation indicates that this parameter must not be empty.
    private String password;
    
    @Schema(description = SystemText.Login.ENTITY_RECAPCHAT_RESPONSE)        // This annotation indicates that information for the swagger
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_RECAPCHAT_RESPONSE_REQUERIDO)        // This annotation indicates that this parameter must not be null.
    @NotBlank(groups = {onCreate.class, onUpdate.class}, message = SystemText.Login.ENTITY_RECAPCHAT_RESPONSE_REQUERIDO)       // This annotation indicates that this parameter must not be empty.
    private String recaptchaResponse;

}
