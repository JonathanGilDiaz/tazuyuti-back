/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.entities.administration;

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
* Represents an option in the system's menu, defining its attributes and behavior.
*
* @param id          The unique identifier of the option (automatically generated).
* @param descripcion A description of the option (optional, max length 250).
* @param opcion      The name of the option (required, max length 80).
* @param url        The URL associated with the option (optional, max length 50).
* @param icono      The icon representing the option (optional, max length 50).
* @param nivel      The hierarchical level of the option (required).
* @param activo     Indicates whether the option is active (default is true).
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "administracion", name = "opciones")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "descripcion", length = 250, nullable = true)
    private String descripcion;
    @Column(name = "opcion", length = 80, nullable = false)
    private String opcion;
    @Column(name = "url", length = 50, nullable = true)
    private String url;
    @Column(name = "icono", length = 50, nullable = true)
    private String icono;
    @Column(name = "nivel", nullable = false)
    private int nivel;
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
