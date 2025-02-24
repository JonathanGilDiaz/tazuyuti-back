/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.repositories.administration;

import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.residencia_back.entities.administration.SessionAttempt;
import com.example.residencia_back.entities.administration.User;

/**
* Repository interface for managing IntentoSession entities.
*
* This interface extends JpaRepository to provide basic CRUD operations 
* and custom query methods for IntentoSession objects. It utilizes Spring Data JPA 
* for seamless database interactions and enables querying based on specific criteria.
*
* @param SessionAttempt The entity type that this repository manages.
* @param Integer        The type of the entity's primary key.
*/

public interface SessionAttemptRepository extends JpaRepository<SessionAttempt, Integer> {

    public int countByUsuarioAndFechaBetween(User usuario, Timestamp fechaInicial, Timestamp fechaFinal);

    @Transactional
    int deleteByUsuarioAndFechaBetween(User usuario, Timestamp fechaInicial, Timestamp fechaFinal);
}
