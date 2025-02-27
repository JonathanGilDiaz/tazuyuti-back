/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.1 Modificado el 26 Feb 2025
 * @date 26/02/2025
 */
package com.example.residencia_back.entities.catalogs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a fuel entity in the catalog schema, mapping to the
 * "cat_area_asignacion" table.
 *
 * @param id The unique identifier of the Entity (automatically generated).
 * @param nombre The name of the fuel Assignment area (required,
 * non-nullable, max length: 30).

 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "catalogos", name = "cat_area_asignacion")
public class AssignmentArea {
    
    @Id
    @Schema(example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;
}
