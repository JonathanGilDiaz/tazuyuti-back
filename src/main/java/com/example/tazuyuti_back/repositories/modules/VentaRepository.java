/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.repositories.modules;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer>, JpaSpecificationExecutor<Venta> {

        long countByUsuario_Sucursal_Id(int sucursalId);

        List<Venta> findByUsuarioAndFechaCreacionAfterAndEstado(User usuario, Timestamp fecha, Boolean estado);

        List<Venta> findByUsuarioAndFechaCreacionBetweenAndEstado(
                        User usuario,
                        Timestamp inicio,
                        Timestamp fin,
                        Boolean estado);
}
