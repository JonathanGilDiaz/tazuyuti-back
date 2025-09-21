/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.administration;

import com.example.tazuyuti_back.entities.catalogs.Sucursal;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents a User entity in the administration schema, mapping to the
 * "usuarios" table.
 *
 * @param id                 Unique identifier of the user (auto-generated).
 * @param nombre             First name of the user (required).
 * @param usuario            Username of the user (required, max 50 characters).
 * @param contrasenia        Password of the user (required, ignored in JSON responses).
 * @param rol                Role assigned to the user (required, maps to "rol_id").
 * @param sucursal           Branch (Sucursal) assigned to the user (required, maps to "sucursal_id").
 * @param fecha_creacion     Timestamp when the user was created (auto-generated, hidden in responses).
 * @param fecha_actualizacion Timestamp when the user was last updated (optional, hidden in responses).
 * @param activo             Indicates if the user is active (default true, hidden in responses).
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "administracion", name = "usuarios")
public class User {

    @Id                        
    @GeneratedValue(strategy = GenerationType.IDENTITY)                      
    @Schema(description = SystemText.User.ENTITY_ID, example = "10")        
    @NotNull(groups = onUpdate.class, message = SystemText.User.ENTITY_USUARIO_ID_REQUERIDO)        
    @Column(name = "id")        
    private int id;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_USUARIO_NOMBRE_REQUERIDO)         
    @Schema(description = SystemText.User.ENTITY_NOMBRE, example = "Juan")        
    @Column(name = "nombre", nullable = false)         
    private String nombre;

    @Column(name = "usuario", length = 50, nullable = false)         
    private String usuario;

    @Column(name = "contrasenia", length = 128, nullable = false)         
    private String contrasenia;

    @Schema(description = SystemText.User.ENTITY_ROL, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_USUARIO_ROL_REQUERIDO)            
    @OneToOne               // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "rol_id", referencedColumnName = "id")                
    private Role rol;

    @Schema(description = SystemText.User.ENTITY_SUCURSAL_ID, example = "{ \"id\": 1 }")        
    @NotNull(groups = {onCreate.class, onUpdate.class}, message = SystemText.User.ENTITY_SUCURSAL_ID_REQUERIDO)            
    @ManyToOne              // This annotation indicates that it has a one-to-one relationship with the assigned table.
    @JoinColumn(name = "sucursal_id", referencedColumnName = "id")                
    private Sucursal sucursal;

    @JsonIgnore                         // This annotation indicates that the attribute will not be displayed in the returned json
    @CreationTimestamp                  // This annotation indicates that the field is automatically set to the creation date and time.
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "fecha_creacion", nullable = false)         
    private Timestamp fecha_creacion;

    @JsonIgnore                         // This annotation indicates that the attribute will not be displayed in the returned json
    @Temporal(TemporalType.TIMESTAMP)   // This annotation indicates that the field stores date and time.
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "fecha_actualizacion", nullable = true)         
    private Timestamp fecha_actualizacion;

    @Builder.Default                    // This annotation indicates that the attribute has a default value
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "activo", nullable = false)         
    private Boolean activo = true;

}
