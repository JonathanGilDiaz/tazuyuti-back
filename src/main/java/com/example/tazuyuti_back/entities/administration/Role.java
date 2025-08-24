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
* Represents a role in the system, defining user permissions and access levels.
*
* @param id       The unique identifier of the role (automatically generated).
* @param rol      The name of the role (required, max length 80).
* @param activo   Indicates whether the role is active (default is true).
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@Table(schema = "administracion", name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "rol", length = 80, nullable = false)
    private String rol;
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
