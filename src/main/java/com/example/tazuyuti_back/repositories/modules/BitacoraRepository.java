/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.repositories.modules;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Bitacora;

public interface BitacoraRepository extends JpaRepository<Bitacora, Integer>, JpaSpecificationExecutor<Bitacora> {

    long countByUsuario_Sucursal_Id(int sucursalId);

    List<Bitacora> findByUsuarioAndFechaCreacionAfter(User usuario, Timestamp fecha);

    List<Bitacora> findByUsuarioAndFechaCreacionBetween(
            User usuario,
            Timestamp inicio,
            Timestamp fin);

    Optional<Bitacora> findByDetalleRuta_Id(int detalleRutaId);
}
