package com.example.tazuyuti_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.CancelarPaquete;

public interface CancelarPaqueteRepository extends JpaRepository<CancelarPaquete, Integer>, JpaSpecificationExecutor<CancelarPaquete> {

}
