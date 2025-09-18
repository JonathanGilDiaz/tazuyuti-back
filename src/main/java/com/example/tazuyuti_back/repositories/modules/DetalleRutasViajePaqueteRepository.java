package com.example.tazuyuti_back.repositories.modules;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tazuyuti_back.entities.modules.DetalleRutasViajePaquete;

public interface DetalleRutasViajePaqueteRepository
        extends JpaRepository<DetalleRutasViajePaquete, Integer>, JpaSpecificationExecutor<DetalleRutasViajePaquete> {

    @Query("SELECT d FROM DetalleRutasViajePaquete d " +
            "WHERE d.detalleRuta.id = :idDetalleRuta " +
            "AND d.paquete.estado.id <> 5")
    List<DetalleRutasViajePaquete> findByDetalleRutaAndPaqueteEstadoNot5(
            @Param("idDetalleRuta") Integer idDetalleRuta);
}
