package com.example.tazuyuti_back.repositories.modules;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tazuyuti_back.entities.administration.User;
import com.example.tazuyuti_back.entities.modules.Paquete;

import jakarta.transaction.Transactional;

public interface PaqueteRepository extends JpaRepository<Paquete, Integer>, JpaSpecificationExecutor<Paquete> {

        long countByUsuario_Sucursal_Id(int sucursalId);

        @Modifying
        @Transactional
        @Query("UPDATE Paquete p SET p.estado.id = :estadoId WHERE p.id = :paqueteId")
        int actualizarEstado(@Param("paqueteId") int paqueteId, @Param("estadoId") int estadoId);

        @Query("SELECT p FROM Paquete p " +
                        "JOIN p.envio e " +
                        "WHERE e.detalleRuta.id = :idDetalleRuta " +
                        "AND p.estado.id <> 5")
        List<Paquete> findByDetalleRutaAndEstadoNot5(@Param("idDetalleRuta") Integer idDetalleRuta);

        @Query("SELECT p FROM Paquete p WHERE p.usuario = :usuario AND p.fechaCreacion >= :fechaInicio AND p.estado.id <> 5")
        List<Paquete> findPaquetesActivosByUsuarioDesdeFecha(
                        @Param("usuario") User usuario,
                        @Param("fechaInicio") Timestamp fechaInicio);

        @Query("SELECT p FROM Paquete p " +
                        "WHERE p.usuario = :usuario " +
                        "AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
                        "AND p.estado.id <> 5")
        List<Paquete> findPaquetesActivosByUsuarioEntreFechas(
                        @Param("usuario") User usuario,
                        @Param("fechaInicio") Timestamp fechaInicio,
                        @Param("fechaFin") Timestamp fechaFin);

}
