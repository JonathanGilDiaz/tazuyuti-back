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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Represents a fuel entity in the catalog schema, mapping to the
 * "cat_combustible" table.
 *
 * @param id The unique identifier of the fuel type (automatically generated).
 * @param nombre The name of the fuel type (required,
 * non-nullable, max length: 30).
 * @param precio The price of the fuel (numeric with precision and scale).
 * @param fecha_actualizacion The timestamp indicating when the fuel was last
 * updated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "catalogos", name = "cat_combustible")
public class Fuel {

    @Id
    @Schema(example = "1")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Temporal(TemporalType.TIMESTAMP)   // This annotation indicates that the field stores date and time.
    @Schema(hidden = true)              // This annotation indicates that the attribute will not be displayed in the swagger
    @Column(name = "fecha_actualizacion", nullable = true)         // This annotation indicates that Associates the attribute with a database column and indicates the validations it must have in order to be assigned a value.
    private Timestamp fecha_actualizacion;
}
