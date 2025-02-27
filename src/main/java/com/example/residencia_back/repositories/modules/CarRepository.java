/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.1 Modificado el 26 Feb 2025
 * @date 26/02/2025
 */
package com.example.residencia_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.residencia_back.entities.modules.Car;

/**
 * Repository interface for managing Vehiculo entities.
 *
 * This interface extends JpaRepository to provide basic CRUD operations and
 * custom query methods for Car objects. It utilizes Spring Data JPA for
 * efficient data access and manipulation related to users.
 */
public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    
}
