package com.example.tazuyuti_back.repositories.modules;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.example.tazuyuti_back.entities.modules.PrecioBoleto;

public interface PrecioBoletoRepository
        extends JpaRepository<PrecioBoleto, Integer>, JpaSpecificationExecutor<PrecioBoleto> {

    List<PrecioBoleto> findByEstadoTrue();

}
