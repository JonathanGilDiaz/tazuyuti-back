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


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "catalogos", name = "cat_estado_paquete")
public class EstadoPaquete {

    @Id
    @Builder.Default
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id = 0;

    @Builder.Default
    @Column(name = "nombre", length = 50)
    private String nombre = "";
}
