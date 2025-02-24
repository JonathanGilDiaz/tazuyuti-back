/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.repositories.administration;

import jakarta.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.residencia_back.entities.administration.Session;
import com.example.residencia_back.entities.administration.User;

/**
* Repository interface for managing Session entities.
*
* This interface extends JpaRepository to provide basic CRUD operations 
* and custom query methods for Session objects. It utilizes Spring Data JPA 
* for efficient data access and manipulation related to user sessions.
*
* @param Session The entity type that this repository manages.
* @param Integer The type of the entity's primary key.
*/

public interface SessionRepository extends JpaRepository<Session, Integer> {

    @Modifying
    @Transactional
    @Query(value = "update Session set activo = :activo, fecha_fin = :fecha_fin where usuario = :user and activo = true")
    public void setUpdateActivo(@Param("activo") boolean status, @Param("user") User user, @Param("fecha_fin") Timestamp fecha_fin);

    Optional<Session> findByToken(String token);
}
