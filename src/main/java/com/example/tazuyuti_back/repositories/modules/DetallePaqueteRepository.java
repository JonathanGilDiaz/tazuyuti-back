package com.example.tazuyuti_back.repositories.modules;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.DetallePaquete;

public interface DetallePaqueteRepository extends JpaRepository<DetallePaquete, Integer>, JpaSpecificationExecutor<DetallePaquete> {

}
