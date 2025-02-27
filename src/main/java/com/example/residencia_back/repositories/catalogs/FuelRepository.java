/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.1 Modificado el 26 Feb 2025
 * @date 26/02/2025
 */
package com.example.residencia_back.repositories.catalogs;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.residencia_back.entities.catalogs.Fuel;

public interface FuelRepository extends JpaRepository<Fuel, Integer> {

}
