/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 02 Ene 2025
 * @date 02/01/2025
 */
package com.example.residencia_back.repositories.administration;

import com.example.residencia_back.entities.administration.User;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


/**
* Repository interface for managing Usuario entities.
*
* This interface extends JpaRepository to provide basic CRUD operations 
* and custom query methods for Usuario objects. It utilizes Spring Data JPA 
* for efficient data access and manipulation related to users.
*
* @param User The entity type that this repository manages.
* @param Integer The type of the entity's primary key.
*/

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsuario(String usuario);

    Optional<User> findFirstByUsuarioAndActivoTrue(String username);

    Optional<User> findFirstByUsuarioOrderByIdDesc(String username);

    @Transactional
    @Modifying
    @Query("update User u set u.activo = ?1 where u.id = ?2")
    void setActivoForUsuario(boolean activo, int id);

}
