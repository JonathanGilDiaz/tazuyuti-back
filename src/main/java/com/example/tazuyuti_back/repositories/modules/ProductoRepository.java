/**
 * @author Jonathan Gilberto Diaz Reyes
 * @version 1.0.0 Creado el 23 Feb 2025
 * @date 23/02/2025
 */
package com.example.tazuyuti_back.repositories.modules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.example.tazuyuti_back.entities.modules.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer>, JpaSpecificationExecutor<Producto> {

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
           "FROM Producto p " +
           "WHERE (p.nombre = :nombre OR p.codigo = :codigo) " +
           "AND p.estado = true")
    boolean existsByNombreOrCodigoAndEstadoTrue(String nombre, String codigo);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
       "FROM Producto p " +
       "WHERE (p.nombre = :nombre OR p.codigo = :codigo) " +
       "AND p.estado = true " +
       "AND p.id <> :id")
    boolean existsByNombreOrCodigoAndEstadoTrueAndIdNot(String nombre, String codigo, int id);

     List<Producto> findByEstadoTrue();
}
