/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 24 Ago 2024
 * @date 24/08/2025
 */
package com.example.tazuyuti_back.entities.catalogs;

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
 * Represents a Sucursal entity in the catalog schema
 *
 * @param id        The unique identifier of the Sucursal (auto-generated).
 * @param nombre    The name of the branch (Sucursal).
 * @param horario   The opening hours or schedule of the branch.
 * @param telefono  The contact phone number of the branch (10 digits).
 * @param direccion The address of the branch.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "catalogos", name = "cat_sucursales")
public class Sucursal {

    @Id
    @Builder.Default
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id = 0;

    @Builder.Default
    @Column(name = "nombre", length = 50)
    private String nombre = "";

    @Builder.Default
    @Column(name = "horario")
    private String horario = "";

     @Builder.Default
    @Column(name = "telefono", length = 10)
    private String telefono = "";

     @Builder.Default
    @Column(name = "direccion")
    private String direccion = "";
}
