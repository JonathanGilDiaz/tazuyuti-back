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
import org.springframework.data.repository.query.Param;
import com.example.tazuyuti_back.entities.modules.DetalleVenta;
import com.example.tazuyuti_back.entities.modules.Venta;

public interface DetalleVentaRepository
        extends JpaRepository<DetalleVenta, Integer>, JpaSpecificationExecutor<DetalleVenta> {

    @Query("""
                SELECT dv
                FROM DetalleVenta dv
                WHERE dv.venta IN :ventas
            """)
    List<DetalleVenta> findByVentas(@Param("ventas") List<Venta> ventas);
}
