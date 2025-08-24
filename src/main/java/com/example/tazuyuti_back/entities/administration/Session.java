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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* Represents a user session in the system, tracking login and logout times.
*
* @param id          The unique identifier of the session (automatically generated).
* @param usuario     The user associated with the session (Many-to-One relationship with Usuario).
* @param fecha_inicio The timestamp when the session started (required).
* @param fecha_fin   The timestamp when the session ended (optional).
* @param token       A unique token associated with the session (optional, max length 255).
* @param activo      Indicates whether the session is currently active (default is true).
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "administracion", name = "sesiones")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private User usuario;
    @Column(name = "fecha_inicio", nullable = false)
    private Timestamp fecha_inicio;
    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_fin", nullable = true)
    private Timestamp fecha_fin;
    @Column(name = "token", length = 255, nullable = true)
    private String token;
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;
}
