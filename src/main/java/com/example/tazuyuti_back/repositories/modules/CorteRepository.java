/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.repositories.modules;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Corte;

public interface CorteRepository
        extends JpaRepository<Corte, Integer>, JpaSpecificationExecutor<Corte> {

    Optional<Corte> findFirstByUsuarioAndEstado(User usuario, String estado);

    long countByUsuario_Sucursal_Id(int sucursalId);
}
