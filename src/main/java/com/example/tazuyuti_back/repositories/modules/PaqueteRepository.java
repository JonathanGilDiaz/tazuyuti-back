package com.example.tazuyuti_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.tazuyuti_back.entities.modules.Paquete;

import jakarta.transaction.Transactional;

public interface PaqueteRepository extends JpaRepository<Paquete, Integer>, JpaSpecificationExecutor<Paquete> {

        long countByUsuario_Sucursal_Id(int sucursalId);

        @Modifying
        @Transactional
        @Query("UPDATE Paquete p SET p.estado.id = :estadoId WHERE p.id = :paqueteId")
        int actualizarEstado(@Param("paqueteId") int paqueteId, @Param("estadoId") int estadoId);
}
