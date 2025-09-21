/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.entities.administration;

import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onUpdate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a user entity in the administration schema, mapping to the "usuarios" table.
 *
 * @param id                  The unique identifier of the user (automatically generated).
 * @param activo              Indicates if the user is active (default is true).
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "administracion", name = "usuarios")
public class UserActive {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ACTIVO_REQUERIDO)        
    @Schema(description = SystemText.User.ENTITY_ACTIVE, example = "false")        
    @Column(name = "activo", nullable = false)         
    private Boolean activo = true;

}
