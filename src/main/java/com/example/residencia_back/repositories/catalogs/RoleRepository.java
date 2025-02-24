/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.repositories.catalogs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.residencia_back.entities.administration.Role;

/**
* Repository interface for managing Rol entities.
*
* This interface extends JpaRepository to provide basic CRUD operations 
* and custom query methods for Rol objects. It utilizes Spring Data JPA 
* for efficient data access and manipulation related to users.
*
* @param Role The entity type that this repository manages.
* @param Integer The type of the entity's primary key.
*/

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	List<Role> findByActivoTrue();

}
