package com.example.tazuyuti_back.repositories.modules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tazuyuti_back.entities.modules.DetalleRuta;

public interface DetalleRutaRepository
        extends JpaRepository<DetalleRuta, Integer>, JpaSpecificationExecutor<DetalleRuta> {

    @Query("SELECT MAX(d.fecha) FROM DetalleRuta d WHERE d.ruta.id = :rutaId")
    Optional<LocalDate> findMaxFechaByRuta(@Param("rutaId") int rutaId);

    @Query("SELECT COUNT(DISTINCT d.fecha) FROM DetalleRuta d WHERE d.ruta.id = :rutaId AND d.fecha >= :hoy")
    int countDistinctFechas(@Param("rutaId") int rutaId, @Param("hoy") LocalDate hoy);

    List<DetalleRuta> findByRutaIdAndEstadoTrueOrderByFechaAscIdAsc(int rutaId);

    boolean existsByRutaIdAndFecha(int rutaId, LocalDate fecha);

}
