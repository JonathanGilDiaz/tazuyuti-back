package com.example.tazuyuti_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.Paquete;

public interface PaqueteRepository extends JpaRepository<Paquete, Integer>, JpaSpecificationExecutor<Paquete> {

        long countByUsuario_Sucursal_Id(int sucursalId);

}
