/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 03 Jun 2025
 * @date 03/06/2025
 */
package com.example.tazuyuti_back.entities.modules;
   
import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.tazuyuti_back.helpers.SystemText;
import com.example.tazuyuti_back.validator.onCreate;
import com.example.tazuyuti_back.validator.onUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(schema = "modulos", name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = SystemText.Producto.ENTITY_ID, example = "10")
    @NotNull(groups = onUpdate.class, message = SystemText.Producto.ENTITY_ID_REQUERIDO)
    @Column(name = "id")
    private Integer id;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El nombre del producto es requerido")         
    @Schema(description = "Nombre del producto", example = "Gatorade fresa")        
    @Column(name = "nombre", nullable = false)         
    private String nombre;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El codigo del producto es requerido")         
    @Schema(description = "Código del producto", example = "GDFGKJDFG")        
    @Column(name = "codigo", nullable = false)         
    private String codigo;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "La unidad del producto es requerido")         
    @Schema(description = "Unidad del producto", example = "Pieza/Granel")        
    @Column(name = "unidad", nullable = false)         
    private String unidad;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El Costo del producto es requerido")         
    @Schema(description = "Costo del producto", example = "10.50")        
    @Column(name = "costo", nullable = false)         
    private double costo;

    @NotNull(groups = {onCreate.class, onUpdate.class}, message = "El precio del producto es requerido")         
    @Schema(description = "Precio del producto", example = "10.50")        
    @Column(name = "precio", nullable = false)         
    private double precio;

    @Column(name = "estado", nullable = false)         
    private boolean estado;
    
    @CreationTimestamp
    @Schema(hidden = true)
    @Column(name = "fecha_creacion", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fechaCreacion;

    @Temporal(TemporalType.TIMESTAMP)
    @Schema(hidden = true)
    @Column(name = "fecha_actualizacion", nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Timestamp fechaActualizacion;

}
