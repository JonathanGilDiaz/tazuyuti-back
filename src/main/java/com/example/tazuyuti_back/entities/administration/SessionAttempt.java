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
import org.hibernate.annotations.CreationTimestamp;

/**
* Represents an entry for a session attempt in the system, tracking unsuccessful or successful login attempts.
*
* @param id        The unique identifier of the session attempt (automatically generated).
* @param usuario   The user associated with the session attempt (Many-to-One relationship with Usuario).
* @param fecha     The timestamp when the session attempt was made (automatically generated).
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "administracion", name = "intento_sesion")
public class SessionAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private User usuario;

    @CreationTimestamp
    @Column(name = "fecha")
    private Timestamp fecha;

}
