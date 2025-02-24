/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.residencia_back.repositories.administration;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.residencia_back.entities.administration.Menu;

/**
* Repository interface for managing Menu entities.
*
* This interface extends JpaRepository to provide basic CRUD operations 
* and custom query methods for Menu objects. It utilizes Spring Data JPA 
* for efficient data access and manipulation based on role and option level.
*
* @param Menu   The entity type that this repository manages.
* @param Integer The type of the entity's primary key.
*/

public interface MenuRepository extends JpaRepository<Menu, Integer> {

    
    List<Menu> findByRolIdAndOpcionNivelOrderById(int rolId, int nivel);

    List<Menu> findByRolIdAndDepensAndOpcionNivel(int rolId, int depens, int nivel);
}
