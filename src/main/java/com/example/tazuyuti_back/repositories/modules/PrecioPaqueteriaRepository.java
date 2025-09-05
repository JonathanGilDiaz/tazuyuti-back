/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.repositories.modules;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.PrecioPaqueteria;

public interface PrecioPaqueteriaRepository
        extends JpaRepository<PrecioPaqueteria, Integer>, JpaSpecificationExecutor<PrecioPaqueteria> {

    List<PrecioPaqueteria> findByEstadoTrue();

}
